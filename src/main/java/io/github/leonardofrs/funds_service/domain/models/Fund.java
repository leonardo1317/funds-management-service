package io.github.leonardofrs.funds_service.domain.models;

import static io.github.leonardofrs.funds_service.domain.assertions.Assertions.requireNonBlank;
import static java.util.Objects.requireNonNullElse;

import io.github.leonardofrs.funds_service.domain.constants.FundStatus;
import io.github.leonardofrs.funds_service.domain.exceptions.MinimumAmountException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Fund(UUID id,
                   String name,
                   BigDecimal minimumAmount,
                   String category,
                   FundStatus status,
                   Instant createdAt,
                   Instant updatedAt) {

  public Fund {
    id = requireNonNullElse(id, UUID.randomUUID());
    requireNonBlank(name, "name is required");
    requireNonBlank(category, "category is required");
    status = requireNonNullElse(status, FundStatus.OPEN);
    createdAt = requireNonNullElse(createdAt, Instant.now());
    updatedAt = requireNonNullElse(updatedAt, Instant.now());
  }

  public void validateAmount(BigDecimal amount) {
    if (amount == null || amount.compareTo(minimumAmount) < 0) {
      throw new MinimumAmountException(
          "The amount is less than the minimum required by the fund " + name
      );
    }
  }
}
