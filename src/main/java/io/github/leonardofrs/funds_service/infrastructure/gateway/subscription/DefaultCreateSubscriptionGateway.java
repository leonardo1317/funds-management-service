package io.github.leonardofrs.funds_service.infrastructure.gateway.subscription;

import io.github.leonardofrs.funds_service.domain.models.Subscription;
import io.github.leonardofrs.funds_service.domain.gateway.subscription.CreateSubscriptionGateway;
import io.github.leonardofrs.funds_service.infrastructure.mappers.SubscriptionMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DefaultCreateSubscriptionGateway implements CreateSubscriptionGateway {

  private final MongoTemplate mongoTemplate;
  private final SubscriptionMapper mapper;

  public DefaultCreateSubscriptionGateway(MongoTemplate mongoTemplate,
      SubscriptionMapper mapper) {
    this.mongoTemplate = mongoTemplate;
    this.mapper = mapper;
  }

  @Override
  public Subscription execute(Subscription subscription) {
    var subscriptionDocument = mapper.toSubscriptionDocument(subscription);
    return mapper.toSubscription(mongoTemplate.insert(subscriptionDocument));
  }
}
