package io.github.leonardofrs.funds_service.infrastructure.repository.mongodb;

import io.github.leonardofrs.funds_service.domain.repository.TransactionalHandler;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class DefaultTransactionalHandler implements TransactionalHandler {

  private final TransactionTemplate transactionTemplate;

  public DefaultTransactionalHandler(TransactionTemplate transactionTemplate) {
    this.transactionTemplate = transactionTemplate;
  }

  @Override
  public <T> T execute(Supplier<T> action) {
    return transactionTemplate.execute(status -> action.get());
  }
}
