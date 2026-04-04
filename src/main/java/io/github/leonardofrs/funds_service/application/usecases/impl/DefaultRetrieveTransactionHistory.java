package io.github.leonardofrs.funds_service.application.usecases.impl;

import io.github.leonardofrs.funds_service.application.usecases.RetrieveTransactionHistory;
import io.github.leonardofrs.funds_service.domain.model.Transaction;
import io.github.leonardofrs.funds_service.domain.repository.RetrieveTransactionHistoryRepository;
import java.util.List;
import java.util.UUID;

public class DefaultRetrieveTransactionHistory implements RetrieveTransactionHistory {

  private final RetrieveTransactionHistoryRepository retrieveTransactionHistoryRepository;

  public DefaultRetrieveTransactionHistory(
      RetrieveTransactionHistoryRepository retrieveTransactionHistoryRepository
  ) {
    this.retrieveTransactionHistoryRepository = retrieveTransactionHistoryRepository;
  }

  @Override
  public List<Transaction> execute(UUID clientId) {
    return retrieveTransactionHistoryRepository.execute(clientId);
  }
}
