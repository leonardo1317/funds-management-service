package io.github.leonardofrs.funds_service.infrastructure.repository.mongodb;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import io.github.leonardofrs.funds_service.domain.exceptions.SubscriptionNotFoundException;
import io.github.leonardofrs.funds_service.domain.model.Subscription;
import io.github.leonardofrs.funds_service.domain.repository.RetrieveSubscriptionRepository;
import io.github.leonardofrs.funds_service.infrastructure.mappers.SubscriptionMapper;
import io.github.leonardofrs.funds_service.infrastructure.repository.mongodb.document.SubscriptionDocument;
import java.util.UUID;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DefaultRetrieveSubscriptionRepository implements RetrieveSubscriptionRepository {

  private final MongoTemplate mongoTemplate;
  private final SubscriptionMapper subscriptionMapper;

  public DefaultRetrieveSubscriptionRepository(
      MongoTemplate mongoTemplate,
      SubscriptionMapper subscriptionMapper
  ) {
    this.mongoTemplate = mongoTemplate;
    this.subscriptionMapper = subscriptionMapper;
  }

  @Override
  public Subscription execute(UUID clientId, UUID subscriptionId) {
    var subscriptionDocument = mongoTemplate.findOne(
        query(where("_id")
            .is(subscriptionId)
            .and("clientId")
            .is(clientId)),
        SubscriptionDocument.class);

    if (subscriptionDocument == null) {
      throw new SubscriptionNotFoundException(
          String.format("Subscription not found %s for client %s", subscriptionId, clientId));
    }
    return subscriptionMapper.toSubscription(subscriptionDocument);
  }
}
