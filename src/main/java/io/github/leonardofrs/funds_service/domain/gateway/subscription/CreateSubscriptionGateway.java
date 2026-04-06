package io.github.leonardofrs.funds_service.domain.gateway.subscription;

import io.github.leonardofrs.funds_service.domain.models.Subscription;

public interface CreateSubscriptionGateway {

  Subscription execute(Subscription subscription);
}
