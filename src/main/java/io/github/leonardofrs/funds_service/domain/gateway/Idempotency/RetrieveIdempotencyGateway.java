package io.github.leonardofrs.funds_service.domain.gateway.Idempotency;

import io.github.leonardofrs.funds_service.domain.models.Idempotency;
import java.util.Optional;

public interface RetrieveIdempotencyGateway {

  Optional<Idempotency> execute(String key);
}
