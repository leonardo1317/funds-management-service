package io.github.leonardofrs.funds_service.domain.repository;

import io.github.leonardofrs.funds_service.domain.model.Transaction;

public interface CreateTransactionRepository {

  Transaction execute(Transaction transaction);
}
