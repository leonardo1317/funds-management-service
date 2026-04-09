package io.github.leonardofrs.funds_service.infrastructure.idempotency.impl;

import io.github.leonardofrs.funds_service.domain.exceptions.DuplicateIdempotencyKeyException;
import io.github.leonardofrs.funds_service.domain.exceptions.IdempotencyConsistencyException;
import io.github.leonardofrs.funds_service.domain.exceptions.IdempotencyInProgressException;
import io.github.leonardofrs.funds_service.domain.exceptions.IdempotencyInfrastructureException;
import io.github.leonardofrs.funds_service.domain.gateway.ObjectSerializerGateway;
import io.github.leonardofrs.funds_service.infrastructure.idempotency.CreateIdempotencyGateway;
import io.github.leonardofrs.funds_service.infrastructure.idempotency.Idempotency;
import io.github.leonardofrs.funds_service.infrastructure.idempotency.IdempotencyHandler;
import io.github.leonardofrs.funds_service.infrastructure.idempotency.RetrieveIdempotencyGateway;
import io.github.leonardofrs.funds_service.infrastructure.idempotency.UpdateIdempotencyGateway;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultIdempotencyHandler implements IdempotencyHandler {

  private final CreateIdempotencyGateway createIdempotencyGateway;
  private final RetrieveIdempotencyGateway retrieveIdempotencyGateway;
  private final UpdateIdempotencyGateway updateIdempotencyGateway;
  private final ObjectSerializerGateway objectSerializerGateway;
  private static final Logger log = LoggerFactory.getLogger(DefaultIdempotencyHandler.class);

  public DefaultIdempotencyHandler(CreateIdempotencyGateway createIdempotencyGateway,
      RetrieveIdempotencyGateway retrieveIdempotencyGateway,
      UpdateIdempotencyGateway updateIdempotencyGateway,
      ObjectSerializerGateway objectSerializerGateway
  ) {
    this.createIdempotencyGateway = createIdempotencyGateway;
    this.retrieveIdempotencyGateway = retrieveIdempotencyGateway;
    this.updateIdempotencyGateway = updateIdempotencyGateway;
    this.objectSerializerGateway = objectSerializerGateway;
  }

  @Override
  public <T> T execute(String key, String service, Supplier<T> operation, Class<T> responseType) {
    try {
      var idempotency = Idempotency.create(key, service);
      createIdempotencyGateway.execute(idempotency);
      return processOperation(idempotency, operation);
    } catch (DuplicateIdempotencyKeyException e) {
      return handleExisting(key, service, operation, responseType);
    }
  }

  private <T> T handleExisting(String key, String service, Supplier<T> operation,
      Class<T> responseType) {
    var existing = retrieveIdempotencyGateway.execute(key)
        .orElseThrow(() -> {
          log.error("[CRITICAL] Idempotency key {} not found after DuplicateKeyException. " +
              "Database state inconsistency detected. ALERT OPERATIONS TEAM.", key);
          return new IdempotencyConsistencyException(
              "Idempotency state consistency violation for key: " + key);
        });

    if (existing.isSuccess()) {
      return objectSerializerGateway.deserialize(existing.response(), responseType);
    }

    if (existing.isFailed() || existing.isStale()) {
      var retryState = existing.markAsProcessing();
      updateIdempotencyGateway.execute(retryState, existing.version());
      return processOperation(retryState, operation);
    }

    throw new IdempotencyInProgressException(key, service);
  }

  private <T> T processOperation(Idempotency idempotency, Supplier<T> operation) {
    T result;
    try {
      result = operation.get();
    } catch (Exception e) {
      markAsFailed(idempotency, e);
      throw e;
    }

    try {
      String jsonResponse = objectSerializerGateway.serialize(result);
      var successState = idempotency.success(jsonResponse);
      updateIdempotencyGateway.execute(successState, idempotency.version());

      return result;
    } catch (Exception e) {
      throw new IdempotencyInfrastructureException(
          "Business operation succeeded but idempotency state could not be updated.", e);
    }
  }

  private void markAsFailed(Idempotency idempotency, Exception businessException) {
    try {
      var failedState = idempotency.failed(businessException.getMessage());
      updateIdempotencyGateway.execute(failedState, idempotency.version());
    } catch (Exception e) {
      log.error(
          "Could not mark key {} as FAILED. Business error was: {}. Infrastructure error: {}",
          idempotency.id(), businessException.getMessage(), e.getMessage());
    }
  }
}
