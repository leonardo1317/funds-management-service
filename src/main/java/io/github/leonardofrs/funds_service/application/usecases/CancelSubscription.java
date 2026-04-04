package io.github.leonardofrs.funds_service.application.usecases;


import io.github.leonardofrs.funds_service.domain.dto.CancelSubscriptionData;
import io.github.leonardofrs.funds_service.domain.model.Subscription;
import java.util.UUID;

public interface CancelSubscription {

  Subscription execute(UUID clientId, UUID subscriptionId, CancelSubscriptionData cancelSubscriptionData);
}
