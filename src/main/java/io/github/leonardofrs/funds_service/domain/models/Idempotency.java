package io.github.leonardofrs.funds_service.domain.models;

import static io.github.leonardofrs.funds_service.domain.constants.IdempotencyStatus.PROCESSING;

import io.github.leonardofrs.funds_service.domain.constants.IdempotencyStatus;
import java.time.Duration;
import java.time.Instant;

public record Idempotency(String id,
                          String service,
                          IdempotencyStatus status,
                          String response,
                          Instant expiresAt) {

  private static final Duration DEFAULT_TTL = Duration.ofDays(30);

  public static Idempotency processing(String id, String service) {
    return new Idempotency(
        id,
        service,
        PROCESSING,
        null,
        Instant.now().plus(DEFAULT_TTL)
    );
  }

  public Idempotency processing() {
    return new Idempotency(id, service, PROCESSING, null, expiresAt);
  }

  public Idempotency success(String response) {
    return new Idempotency(
        id,
        service,
        IdempotencyStatus.SUCCESS,
        response,
        expiresAt
    );
  }

  public Idempotency failed(String error) {
    return new Idempotency(
        id,
        service,
        IdempotencyStatus.FAILED,
        error,
        expiresAt
    );
  }

  public boolean isSuccess() {
    return status == IdempotencyStatus.SUCCESS;
  }

  public boolean isProcessing() {
    return status == PROCESSING;
  }
}
