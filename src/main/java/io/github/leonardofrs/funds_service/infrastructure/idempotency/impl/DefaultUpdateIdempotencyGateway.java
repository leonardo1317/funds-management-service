package io.github.leonardofrs.funds_service.infrastructure.idempotency.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.mongodb.client.result.UpdateResult;
import io.github.leonardofrs.funds_service.infrastructure.exceptions.OptimisticLockingException;
import io.github.leonardofrs.funds_service.infrastructure.idempotency.Idempotency;
import io.github.leonardofrs.funds_service.infrastructure.idempotency.UpdateIdempotencyGateway;
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
  public Idempotency execute(Idempotency idempotency, long expectedVersion) {
    Query query = new Query(
        where("_id").is(idempotency.id())
            .and("version").is(expectedVersion)
    );

    Update update = new Update()
        .set("status", idempotency.status())
        .set("response", idempotency.response())
        .set("version", idempotency.version())
        .set("createdAt", idempotency.createdAt())
        .set("updatedAt", idempotency.updatedAt())
        .set("expiresAt", idempotency.expiresAt());

    UpdateResult result = mongoTemplate.updateFirst(query, update, IdempotencyDocument.class);

    if (result.getModifiedCount() == 0) {
      throw new OptimisticLockingException("Concurrent update detected for key: " + idempotency.id());
    }

    return idempotency;
  }
}
