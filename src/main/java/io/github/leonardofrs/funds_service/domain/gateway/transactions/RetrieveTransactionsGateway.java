package io.github.leonardofrs.funds_service.domain.gateway.transactions;

import io.github.leonardofrs.funds_service.domain.models.Transaction;
import io.github.leonardofrs.funds_service.domain.vo.Page;
import java.util.List;
import java.util.UUID;

public interface RetrieveTransactionsGateway {

  List<Transaction> execute(UUID clientId, Page page);
}
