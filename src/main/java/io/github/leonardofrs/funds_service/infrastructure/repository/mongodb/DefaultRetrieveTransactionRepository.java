package io.github.leonardofrs.funds_service.infrastructure.repository.mongodb;

import io.github.leonardofrs.funds_service.domain.model.Transaction;
import io.github.leonardofrs.funds_service.domain.repository.RetrieveTransactionHistoryRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class DefaultRetrieveTransactionRepository implements
    RetrieveTransactionHistoryRepository {

  @Override
  public List<Transaction> execute(UUID clientId) {
    return List.of();
  }
}
