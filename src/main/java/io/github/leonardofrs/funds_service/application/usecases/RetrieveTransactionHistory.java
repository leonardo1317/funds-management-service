package io.github.leonardofrs.funds_service.application.usecases;

import io.github.leonardofrs.funds_service.domain.model.Transaction;
import java.util.List;
import java.util.UUID;

public interface RetrieveTransactionHistory {

  List<Transaction> execute(UUID clientId);
}
