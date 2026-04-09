package io.github.leonardofrs.funds_service.infrastructure.idempotency;

public interface CreateIdempotencyGateway {

  Idempotency execute(Idempotency idempotency);
}
