package io.github.leonardofrs.funds_service.domain.models;

import static io.github.leonardofrs.funds_service.domain.assertions.Assertions.requireNonBlank;
import static io.github.leonardofrs.funds_service.domain.assertions.Assertions.requireNonNegative;
import static io.github.leonardofrs.funds_service.domain.assertions.Assertions.requirePositive;
import static java.util.Objects.requireNonNullElse;

import io.github.leonardofrs.funds_service.domain.constants.ClientStatus;
import io.github.leonardofrs.funds_service.domain.exceptions.InsufficientBalanceException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record Client(UUID id,
                     String fullName,
                     String email,
                     String phone,
                     List<String> notificationChannels,
                     BigDecimal balance,
                     ClientStatus status,
                     Long version,
                     Instant createdAt,
                     Instant updatedAt
) {

  private static final BigDecimal INITIAL_BALANCE = new BigDecimal("500000");
  private static final String ERROR_AMOUNT_POSITIVE = "amount must be greater than zero";
  private static final String ERROR_BALANCE_NEGATIVE = "balance must not be negative";
  private static final Long INITIAL_VERSION = 0L;

  public Client {
    id = requireNonNullElse(id, UUID.randomUUID());
    requireNonBlank(fullName, "fullName is required");
    requireNonBlank(email, "email is required");
    requireNonBlank(phone, "phone is required");
    notificationChannels = List.copyOf(
        requireNonNullElse(notificationChannels, Collections.emptyList()));
    balance = requireNonNullElse(balance, INITIAL_BALANCE);
    balance = requireNonNegative(balance, ERROR_BALANCE_NEGATIVE);
    status = requireNonNullElse(status, ClientStatus.ACTIVE);
    version = requireNonNullElse(version, INITIAL_VERSION);
    createdAt = requireNonNullElse(createdAt, Instant.now());
    updatedAt = requireNonNullElse(updatedAt, Instant.now());
  }

  public Client debit(BigDecimal amount) {
    requirePositive(amount, ERROR_AMOUNT_POSITIVE);
    if (balance.compareTo(amount) < 0) {
      throw new InsufficientBalanceException("insufficient balance");
    }

    return new Client(
        id, fullName, email, phone, notificationChannels,
        balance.subtract(amount),
        status,
        version + 1,
        createdAt,
        Instant.now()
    );
  }

  public Client credit(BigDecimal amount) {
    requirePositive(amount, ERROR_AMOUNT_POSITIVE);
    return new Client(
        id, fullName, email, phone, notificationChannels,
        balance.add(amount),
        status,
        version + 1,
        createdAt,
        Instant.now()
    );
  }
}
