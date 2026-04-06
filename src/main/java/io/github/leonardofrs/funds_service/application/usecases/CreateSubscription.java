package io.github.leonardofrs.funds_service.application.usecases;

import io.github.leonardofrs.funds_service.application.dto.CreateSubscriptionData;
import io.github.leonardofrs.funds_service.domain.models.Subscription;
import java.util.UUID;

public interface CreateSubscription {
  Subscription execute(UUID clientId, CreateSubscriptionData createSubscriptionData);
}
