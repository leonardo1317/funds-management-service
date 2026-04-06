package io.github.leonardofrs.funds_service.infrastructure.gateway.transaction;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import io.github.leonardofrs.funds_service.domain.gateway.transactions.CountTransactionsGateway;
import io.github.leonardofrs.funds_service.infrastructure.gateway.documents.TransactionDocument;
import java.util.UUID;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class DefaultCountTransactionsGateway implements CountTransactionsGateway {

  private final MongoTemplate mongoTemplate;

  public DefaultCountTransactionsGateway(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public long execute(UUID clientId) {
    Query query = new Query(where("clientId").is(clientId));
    return mongoTemplate.count(query, TransactionDocument.class);
  }
}
