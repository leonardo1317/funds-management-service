package io.github.leonardofrs.funds_service.domain.gateway.transactions;

import io.github.leonardofrs.funds_service.domain.models.Transaction;

public interface CreateTransactionGateway {

  Transaction execute(Transaction transaction);
}
