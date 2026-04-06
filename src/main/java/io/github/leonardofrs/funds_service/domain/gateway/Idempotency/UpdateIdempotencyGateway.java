package io.github.leonardofrs.funds_service.domain.gateway.Idempotency;

import io.github.leonardofrs.funds_service.domain.models.Idempotency;

public interface UpdateIdempotencyGateway {

  Idempotency execute(Idempotency idempotency);
}
