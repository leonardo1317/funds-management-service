package io.github.leonardofrs.funds_service.infrastructure.idempotency;

import java.util.Optional;

public interface RetrieveIdempotencyGateway {

  Optional<Idempotency> execute(String key);
}
