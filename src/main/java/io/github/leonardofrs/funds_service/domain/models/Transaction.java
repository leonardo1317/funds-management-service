package io.github.leonardofrs.funds_service.domain.models;

import static io.github.leonardofrs.funds_service.domain.assertions.Assertions.requireNonBlank;
import static io.github.leonardofrs.funds_service.domain.constants.TransactionStatus.REJECTED;
import static io.github.leonardofrs.funds_service.domain.constants.TransactionStatus.SUCCESS;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

import io.github.leonardofrs.funds_service.domain.constants.MovementType;
import io.github.leonardofrs.funds_service.domain.constants.TransactionStatus;
import io.github.leonardofrs.funds_service.domain.constants.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Transaction(
    UUID id,
    UUID clientId,
    UUID fundId,
    String fundName,
    UUID subscriptionId,
    TransactionType type,
    MovementType movementType,
    BigDecimal amount,
    BigDecimal balanceBefore,
    BigDecimal balanceAfter,
    TransactionStatus status,
    String errorMessage,
    Instant createdAt
) {

  public Transaction {
    id = requireNonNullElse(id, UUID.randomUUID());
    requireNonNull(clientId, "clientId is required");
    requireNonNull(fundId, "fundId is required");
    requireNonBlank(fundName, "fundName is required");
    requireNonNull(type, "type is required");
    requireNonNull(movementType, "movementType is required");
    requireNonNull(status, "status is required");
    requireNonNull(amount, "amount is required");
    requireNonNull(balanceBefore, "balanceBefore is required");
    requireNonNull(balanceAfter, "balanceAfter is required");
    createdAt = requireNonNullElse(createdAt, Instant.now());
  }

  public static Transaction success(
      UUID clientId, UUID fundId, String fundName, UUID subscriptionId,
      TransactionType type, MovementType movementType,
      BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter
  ) {
    return create(clientId, fundId, fundName, subscriptionId, type, movementType,
        amount, balanceBefore, balanceAfter, SUCCESS, null);
  }

  public static Transaction rejected(
      UUID clientId, UUID fundId, String fundName,
      TransactionType type, MovementType movementType,
      BigDecimal amount, BigDecimal balanceBefore, String error
  ) {
    return create(clientId, fundId, fundName, null, type, movementType,
        amount, balanceBefore, balanceBefore, REJECTED, error);
  }

  private static Transaction create(
      UUID clientId, UUID fundId, String fundName, UUID subscriptionId,
      TransactionType type, MovementType movementType,
      BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter,
      TransactionStatus status, String errorMessage
  ) {
    return new Transaction(
        UUID.randomUUID(), clientId, fundId, fundName, subscriptionId,
        type, movementType, amount, balanceBefore, balanceAfter,
        status, errorMessage, Instant.now()
    );
  }
}
