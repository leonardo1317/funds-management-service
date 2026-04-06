package io.github.leonardofrs.funds_service.infrastructure.gateway.subscription;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import io.github.leonardofrs.funds_service.domain.exceptions.SubscriptionNotFoundException;
import io.github.leonardofrs.funds_service.domain.models.Subscription;
import io.github.leonardofrs.funds_service.domain.gateway.subscription.RetrieveSubscriptionGateway;
import io.github.leonardofrs.funds_service.infrastructure.mappers.SubscriptionMapper;
import io.github.leonardofrs.funds_service.infrastructure.gateway.documents.SubscriptionDocument;
import java.util.UUID;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DefaultRetrieveSubscriptionGateway implements RetrieveSubscriptionGateway {

  private final MongoTemplate mongoTemplate;
  private final SubscriptionMapper mapper;

  public DefaultRetrieveSubscriptionGateway(
      MongoTemplate mongoTemplate,
      SubscriptionMapper mapper
  ) {
    this.mongoTemplate = mongoTemplate;
    this.mapper = mapper;
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
    return mapper.toSubscription(subscriptionDocument);
  }
}
