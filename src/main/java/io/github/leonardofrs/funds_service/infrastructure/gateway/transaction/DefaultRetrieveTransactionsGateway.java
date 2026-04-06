package io.github.leonardofrs.funds_service.infrastructure.gateway.transaction;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import io.github.leonardofrs.funds_service.domain.models.Transaction;
import io.github.leonardofrs.funds_service.domain.gateway.transactions.RetrieveTransactionsGateway;
import io.github.leonardofrs.funds_service.domain.vo.Page;
import io.github.leonardofrs.funds_service.infrastructure.mappers.TransactionMapper;
import io.github.leonardofrs.funds_service.infrastructure.gateway.documents.TransactionDocument;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class DefaultRetrieveTransactionsGateway implements RetrieveTransactionsGateway {

  private final MongoTemplate mongoTemplate;
  private final TransactionMapper transactionMapper;

  public DefaultRetrieveTransactionsGateway(
      MongoTemplate mongoTemplate,
      TransactionMapper transactionMapper) {
    this.mongoTemplate = mongoTemplate;
    this.transactionMapper = transactionMapper;
  }

  @Override
  public List<Transaction> execute(UUID clientId, Page page) {
    Query query = new Query(where("clientId").is(clientId))
        .with(Sort.by(Sort.Direction.DESC, "createdAt"))
        .skip(page.offset())
        .limit(page.limit());

    var documents = mongoTemplate.find(query, TransactionDocument.class);

    return transactionMapper.toTransactions(documents);
  }
}
