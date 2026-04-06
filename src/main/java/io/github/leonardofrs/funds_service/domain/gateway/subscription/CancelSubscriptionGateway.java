package io.github.leonardofrs.funds_service.domain.gateway.subscription;

import io.github.leonardofrs.funds_service.domain.constants.SubscriptionStatus;
import io.github.leonardofrs.funds_service.domain.models.Subscription;

public interface CancelSubscriptionGateway {

  Subscription execute(Subscription subscription, SubscriptionStatus currentStatus);
}
