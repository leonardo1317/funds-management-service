package io.github.leonardofrs.funds_service.domain.repository;

import io.github.leonardofrs.funds_service.domain.model.Transaction;
import java.util.List;
import java.util.UUID;

public interface RetrieveTransactionHistoryRepository {

  List<Transaction> execute(UUID clientId);
}
