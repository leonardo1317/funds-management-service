package io.github.leonardofrs.funds_service.infrastructure.idempotency;

import java.util.function.Supplier;

public interface IdempotencyHandler {

  <T> T execute(String key, String service, Supplier<T> operation, Class<T> responseType);
}
