package io.github.leonardofrs.funds_service.infrastructure.repository.mongodb;

import io.github.leonardofrs.funds_service.domain.model.Transaction;
import io.github.leonardofrs.funds_service.domain.repository.CreateTransactionRepository;
import io.github.leonardofrs.funds_service.infrastructure.mappers.TransactionMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DefaultCreateTransactionRepository implements CreateTransactionRepository {

  private final MongoTemplate mongoTemplate;
  private final TransactionMapper transactionMapper;

  public DefaultCreateTransactionRepository(
      MongoTemplate mongoTemplate,
      TransactionMapper transactionMapper
  ) {
    this.mongoTemplate = mongoTemplate;
    this.transactionMapper = transactionMapper;
  }

  @Override
  public Transaction execute(Transaction transaction) {
    var transactionDocument = transactionMapper.toTransactionDocument(transaction);
    return transactionMapper.toTransaction(mongoTemplate.save(transactionDocument));
  }
}
