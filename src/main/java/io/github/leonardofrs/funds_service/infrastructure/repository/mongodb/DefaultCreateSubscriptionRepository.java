package io.github.leonardofrs.funds_service.infrastructure.repository.mongodb;

import io.github.leonardofrs.funds_service.domain.model.Subscription;
import io.github.leonardofrs.funds_service.domain.repository.CreateSubscriptionRepository;
import io.github.leonardofrs.funds_service.infrastructure.mappers.SubscriptionMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DefaultCreateSubscriptionRepository implements CreateSubscriptionRepository {

  private final MongoTemplate mongoTemplate;
  private final SubscriptionMapper subscriptionMapper;

  public DefaultCreateSubscriptionRepository(MongoTemplate mongoTemplate,
      SubscriptionMapper subscriptionMapper) {
    this.mongoTemplate = mongoTemplate;
    this.subscriptionMapper = subscriptionMapper;
  }

  @Override
  public Subscription execute(Subscription subscription) {
    var subscriptionDocument = subscriptionMapper.toSubscriptionDocument(subscription);
    return subscriptionMapper.toSubscription(mongoTemplate.save(subscriptionDocument));
  }
}
