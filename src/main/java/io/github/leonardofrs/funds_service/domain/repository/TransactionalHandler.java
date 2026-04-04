package io.github.leonardofrs.funds_service.domain.repository;

import java.util.function.Supplier;

@FunctionalInterface
public interface TransactionalHandler {

  <T> T execute(Supplier<T> action);
}
