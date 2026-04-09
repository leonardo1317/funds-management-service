package io.github.leonardofrs.funds_service.infrastructure.idempotency;

public interface UpdateIdempotencyGateway {

  Idempotency execute(Idempotency domain, long expectedVersion);
}
