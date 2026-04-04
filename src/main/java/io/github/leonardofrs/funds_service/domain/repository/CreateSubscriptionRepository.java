package io.github.leonardofrs.funds_service.domain.repository;

import io.github.leonardofrs.funds_service.domain.model.Subscription;

public interface CreateSubscriptionRepository {

  Subscription execute(Subscription subscription);
}
