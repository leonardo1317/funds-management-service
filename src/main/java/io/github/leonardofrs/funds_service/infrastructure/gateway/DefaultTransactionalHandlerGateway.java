package io.github.leonardofrs.funds_service.infrastructure.gateway;

import io.github.leonardofrs.funds_service.domain.gateway.TransactionalHandlerGateway;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class DefaultTransactionalHandlerGateway implements TransactionalHandlerGateway {

  private final TransactionTemplate transactionTemplate;

  public DefaultTransactionalHandlerGateway(TransactionTemplate transactionTemplate) {
    this.transactionTemplate = transactionTemplate;
  }

  @Override
  public <T> T execute(Supplier<T> action) {
    return transactionTemplate.execute(status -> action.get());
  }
}
