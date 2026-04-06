package io.github.leonardofrs.funds_service.domain.gateway;

import java.util.function.Supplier;

@FunctionalInterface
public interface TransactionalHandlerGateway {

  <T> T execute(Supplier<T> action);
}
