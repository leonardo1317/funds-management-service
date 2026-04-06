package io.github.leonardofrs.funds_service.domain.gateway.Idempotency;

import io.github.leonardofrs.funds_service.domain.models.Idempotency;

public interface CreateIdempotencyGateway {

  Idempotency execute(Idempotency idempotency);
}
