package io.github.leonardofrs.funds_service.application.usecases.impl;

import io.github.leonardofrs.funds_service.application.usecases.IdempotencyHandler;
import io.github.leonardofrs.funds_service.domain.exceptions.IdempotencyAlreadyExistsException;
import io.github.leonardofrs.funds_service.domain.exceptions.IdempotencyException;
import io.github.leonardofrs.funds_service.domain.gateway.Idempotency.CreateIdempotencyGateway;
import io.github.leonardofrs.funds_service.domain.gateway.Idempotency.RetrieveIdempotencyGateway;
import io.github.leonardofrs.funds_service.domain.gateway.Idempotency.UpdateIdempotencyGateway;
import io.github.leonardofrs.funds_service.domain.gateway.JsonSerializerGateway;
import io.github.leonardofrs.funds_service.domain.models.Idempotency;
import java.util.function.Supplier;

public class DefaultIdempotencyHandler implements IdempotencyHandler {

  private final CreateIdempotencyGateway createIdempotencyGateway;
  private final RetrieveIdempotencyGateway retrieveIdempotencyGateway;
  private final UpdateIdempotencyGateway updateIdempotencyGateway;
  private final JsonSerializerGateway jsonSerializerGateway;

  public DefaultIdempotencyHandler(CreateIdempotencyGateway createIdempotencyGateway,
      RetrieveIdempotencyGateway retrieveIdempotencyGateway,
      UpdateIdempotencyGateway updateIdempotencyGateway,
      JsonSerializerGateway jsonSerializerGateway
  ) {
    this.createIdempotencyGateway = createIdempotencyGateway;
    this.retrieveIdempotencyGateway = retrieveIdempotencyGateway;
    this.updateIdempotencyGateway = updateIdempotencyGateway;
    this.jsonSerializerGateway = jsonSerializerGateway;
  }

  @Override
  public <T> T execute(String key, String service, Supplier<T> operation, Class<T> responseType) {
    try {
      var idempotency = Idempotency.processing(key, service);
      createIdempotencyGateway.execute(idempotency);
      return processOperation(idempotency, operation);
    } catch (IdempotencyAlreadyExistsException e) {
      return handleExisting(key, service, operation, responseType);
    }
  }

  private <T> T handleExisting(String key, String service, Supplier<T> operation,
      Class<T> responseType) {
    Idempotency existing = retrieveIdempotencyGateway.execute(key)
        .orElseThrow(() -> new IdempotencyException(key, service, "Error de consistencia crítica"));

    if (!existing.service().equals(service)) {
      throw new IdempotencyException(key, service, "Llave ya utilizada en otro servicio");
    }

    if (existing.isSuccess()) {
      return jsonSerializerGateway.deserialize(existing.response(), responseType);
    }

    if (existing.isProcessing()) {
      throw new IdempotencyException(key, service, "Operación en progreso. Intente nuevamente.");
    }

    var retryIdempotency = existing.processing();
    updateIdempotencyGateway.execute(retryIdempotency);
    return processOperation(retryIdempotency, operation);
  }

  private <T> T processOperation(Idempotency idempotency, Supplier<T> operation) {
    try {
      T result = operation.get();

      String serialized = jsonSerializerGateway.serialize(result);
      updateIdempotencyGateway.execute(idempotency.success(serialized));

      return result;
    } catch (Exception e) {
      updateIdempotencyGateway.execute(idempotency.failed(e.getMessage()));
      throw e;
    }
  }
}
