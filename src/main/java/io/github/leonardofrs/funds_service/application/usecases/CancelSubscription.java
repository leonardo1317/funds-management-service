package io.github.leonardofrs.funds_service.application.usecases;


import io.github.leonardofrs.funds_service.application.dto.CancelSubscriptionData;
import io.github.leonardofrs.funds_service.domain.models.Subscription;
import java.util.UUID;

public interface CancelSubscription {

  Subscription execute(UUID clientId, UUID subscriptionId,
      CancelSubscriptionData cancelSubscriptionData);
}
