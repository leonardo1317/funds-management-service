package io.github.leonardofrs.funds_service.infrastructure.gateway.idempotency;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import io.github.leonardofrs.funds_service.domain.gateway.Idempotency.UpdateIdempotencyGateway;
import io.github.leonardofrs.funds_service.domain.models.Idempotency;
import io.github.leonardofrs.funds_service.infrastructure.gateway.documents.IdempotencyDocument;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
public class DefaultUpdateIdempotencyGateway implements UpdateIdempotencyGateway {

  private final MongoTemplate mongoTemplate;

  public DefaultUpdateIdempotencyGateway(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public Idempotency execute(Idempotency idempotency) {
    Query query = Query.query(where("_id").is(idempotency.id()));
    Update update = new Update()
        .set("status", idempotency.status().name())
        .set("response", idempotency.response());
    mongoTemplate.updateFirst(query, update, IdempotencyDocument.class);
    return idempotency;
  }
}
