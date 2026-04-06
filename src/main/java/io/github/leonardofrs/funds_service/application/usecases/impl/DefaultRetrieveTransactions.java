package io.github.leonardofrs.funds_service.application.usecases.impl;

import io.github.leonardofrs.funds_service.application.usecases.RetrieveTransactions;
import io.github.leonardofrs.funds_service.domain.models.Transaction;
import io.github.leonardofrs.funds_service.domain.gateway.transactions.CountTransactionsGateway;
import io.github.leonardofrs.funds_service.domain.gateway.transactions.RetrieveTransactionsGateway;
import io.github.leonardofrs.funds_service.domain.vo.Page;
import io.github.leonardofrs.funds_service.domain.vo.PageResult;
import java.util.List;
import java.util.UUID;

public class DefaultRetrieveTransactions implements RetrieveTransactions {

  private final RetrieveTransactionsGateway retrieveTransactionsGateway;
  private final CountTransactionsGateway countTransactionsGateway;

  public DefaultRetrieveTransactions(
      RetrieveTransactionsGateway retrieveTransactionsGateway,
      CountTransactionsGateway countTransactionsGateway
  ) {
    this.retrieveTransactionsGateway = retrieveTransactionsGateway;
    this.countTransactionsGateway = countTransactionsGateway;
  }

  @Override
  public PageResult<Transaction> execute(UUID clientId, Page page) {
    long total = countTransactionsGateway.execute(clientId);

    if (total == 0) {
      return PageResult.of(List.of(), 0);
    }

    List<Transaction> transactions = retrieveTransactionsGateway.execute(clientId, page);

    return PageResult.of(transactions, total);
  }
}
