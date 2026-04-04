package io.github.leonardofrs.funds_service.domain.model;

import static io.github.leonardofrs.funds_service.domain.assertions.Assertions.requireNonBlank;
import static io.github.leonardofrs.funds_service.domain.constants.SubscriptionStatus.ACTIVE;
import static io.github.leonardofrs.funds_service.domain.constants.SubscriptionStatus.CANCELLED;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

import io.github.leonardofrs.funds_service.domain.constants.SubscriptionStatus;
import io.github.leonardofrs.funds_service.domain.exceptions.IllegalSubscriptionStateException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Subscription(UUID id,
                           UUID clientId,
                           UUID fundId,
                           String fundName,
                           BigDecimal amount,
                           SubscriptionStatus status,
                           String cancellationReason,
                           Instant createdAt,
                           Instant updatedAt) {

  public Subscription {
    id = requireNonNullElse(id, UUID.randomUUID());
    requireNonNull(clientId, "clientId is required");
    requireNonNull(fundId, "fundId is required");
    requireNonBlank(fundName, "fundName is required");
    requireNonNull(amount, "amount is required");
    status = requireNonNullElse(status, ACTIVE);
    cancellationReason = requireNonNullElse(cancellationReason, "");
    createdAt = requireNonNullElse(createdAt, Instant.now());
    updatedAt = requireNonNullElse(updatedAt, Instant.now());
  }

  public static Subscription subscribe(UUID clientId, UUID fundId, String fundName,
      BigDecimal amount) {
    return new Subscription(
        UUID.randomUUID(),
        clientId,
        fundId,
        fundName,
        amount,
        ACTIVE,
        "",
        Instant.now(),
        Instant.now());
  }

  public Subscription cancel(String cancellationReason) {
    if (status == CANCELLED) {
      throw new IllegalSubscriptionStateException("Subscription already cancelled");
    }

    return new Subscription(
        id,
        clientId,
        fundId,
        fundName,
        amount,
        CANCELLED,
        cancellationReason,
        createdAt,
        Instant.now());
  }
}
