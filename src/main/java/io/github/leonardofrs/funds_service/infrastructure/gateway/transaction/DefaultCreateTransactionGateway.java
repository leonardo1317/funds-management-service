package io.github.leonardofrs.funds_service.infrastructure.gateway.transaction;

import io.github.leonardofrs.funds_service.domain.models.Transaction;
import io.github.leonardofrs.funds_service.domain.gateway.transactions.CreateTransactionGateway;
import io.github.leonardofrs.funds_service.infrastructure.mappers.TransactionMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DefaultCreateTransactionGateway implements CreateTransactionGateway {

  private final MongoTemplate mongoTemplate;
  private final TransactionMapper transactionMapper;

  public DefaultCreateTransactionGateway(
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
