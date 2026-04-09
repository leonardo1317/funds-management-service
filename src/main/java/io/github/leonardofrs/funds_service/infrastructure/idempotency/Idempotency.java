package io.github.leonardofrs.funds_service.infrastructure.idempotency;

import io.github.leonardofrs.funds_service.domain.constants.IdempotencyStatus;
import java.time.Duration;
import java.time.Instant;

public record Idempotency(
    String id,
    String service,
    IdempotencyStatus status,
    String response,
    Long version,
    Instant createdAt,
    Instant updatedAt,
    Instant expiresAt
) {
  private static final Duration DEFAULT_TTL = Duration.ofDays(30);
  private static final Duration LOCK_TIMEOUT = Duration.ofMinutes(5);

  public static Idempotency create(String id, String service) {
    Instant now = Instant.now();
    return new Idempotency(
        id, service, IdempotencyStatus.PROCESSING, null, 1L,
        now,
        now,
        now.plus(DEFAULT_TTL)
    );
  }

  public Idempotency success(String response) {
    return new Idempotency(id, service, IdempotencyStatus.SUCCESS, response,
        version + 1, createdAt, Instant.now(), expiresAt);
  }

  public Idempotency failed(String error) {
    return new Idempotency(id, service, IdempotencyStatus.FAILED, error,
        version + 1, createdAt, Instant.now(), expiresAt);
  }

  public Idempotency markAsProcessing() {
    return new Idempotency(id, service, IdempotencyStatus.PROCESSING, null,
        version + 1, createdAt, Instant.now(), expiresAt);
  }

  public boolean isSuccess() {
    return status == IdempotencyStatus.SUCCESS;
  }

  public boolean isProcessing() {
    return status == IdempotencyStatus.PROCESSING;
  }

  public boolean isFailed() {
    return status == IdempotencyStatus.FAILED;
  }

  public boolean isStale() {
    return isProcessing() && Instant.now().isAfter(createdAt.plus(LOCK_TIMEOUT));
  }
}
