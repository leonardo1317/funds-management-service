package io.github.leonardofrs.funds_service.infrastructure.gateway.subscription;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import io.github.leonardofrs.funds_service.domain.constants.SubscriptionStatus;
import io.github.leonardofrs.funds_service.domain.gateway.subscription.CheckSubscriptionGateway;
import io.github.leonardofrs.funds_service.infrastructure.gateway.documents.SubscriptionDocument;
import java.util.UUID;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DefaultCheckSubscriptionGateway implements CheckSubscriptionGateway {

  private final MongoTemplate mongoTemplate;

  public DefaultCheckSubscriptionGateway(MongoTemplate mongoTemplate) {
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
