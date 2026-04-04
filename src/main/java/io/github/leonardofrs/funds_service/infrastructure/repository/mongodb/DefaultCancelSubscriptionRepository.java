package io.github.leonardofrs.funds_service.infrastructure.repository.mongodb;

import static io.github.leonardofrs.funds_service.domain.constants.SubscriptionStatus.CANCELLED;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.mongodb.client.result.UpdateResult;
import io.github.leonardofrs.funds_service.domain.constants.SubscriptionStatus;
import io.github.leonardofrs.funds_service.domain.exceptions.IllegalSubscriptionStateException;
import io.github.leonardofrs.funds_service.domain.model.Subscription;
import io.github.leonardofrs.funds_service.domain.repository.CancelSubscriptionRepository;
import io.github.leonardofrs.funds_service.infrastructure.repository.mongodb.document.SubscriptionDocument;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
public class DefaultCancelSubscriptionRepository implements CancelSubscriptionRepository {

  private final MongoTemplate mongoTemplate;

  public DefaultCancelSubscriptionRepository(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public Subscription execute(Subscription subscription, SubscriptionStatus currentStatus) {
    Query query = new Query(where("_id")
        .is(subscription.id())
        .and("status")
        .is(currentStatus));

    Update update = new Update()
        .set("status", CANCELLED)
        .set("cancellationReason", subscription.cancellationReason())
        .set("updatedAt", subscription.updatedAt());

    UpdateResult result = mongoTemplate.updateFirst(query, update, SubscriptionDocument.class);

    if (result.getMatchedCount() == 0) {
      throw new IllegalSubscriptionStateException(
          String.format("Transition failed: Subscription %s is not in the expected state (%s).",
              subscription.id(), currentStatus));
    }

    return subscription;
  }
}
