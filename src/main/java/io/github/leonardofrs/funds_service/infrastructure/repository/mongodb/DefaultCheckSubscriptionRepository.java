package io.github.leonardofrs.funds_service.infrastructure.repository.mongodb;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import io.github.leonardofrs.funds_service.domain.constants.SubscriptionStatus;
import io.github.leonardofrs.funds_service.domain.repository.CheckSubscriptionRepository;
import io.github.leonardofrs.funds_service.infrastructure.repository.mongodb.document.SubscriptionDocument;
import java.util.UUID;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DefaultCheckSubscriptionRepository implements CheckSubscriptionRepository {

  private final MongoTemplate mongoTemplate;

  public DefaultCheckSubscriptionRepository(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public boolean execute(UUID clientId, UUID fundId) {
    return mongoTemplate.exists(
        query(where("clientId")
            .is(clientId)
            .and("fundId")
            .is(fundId)
            .and("status")
            .is(SubscriptionStatus.ACTIVE)),
        SubscriptionDocument.class
    );
  }
}
