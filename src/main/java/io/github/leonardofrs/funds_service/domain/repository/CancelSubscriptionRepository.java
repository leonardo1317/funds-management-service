package io.github.leonardofrs.funds_service.domain.repository;

import io.github.leonardofrs.funds_service.domain.constants.SubscriptionStatus;
import io.github.leonardofrs.funds_service.domain.model.Subscription;

public interface CancelSubscriptionRepository {

  Subscription execute(Subscription subscription, SubscriptionStatus currentStatus);
}
