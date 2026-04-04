package io.github.leonardofrs.funds_service.domain.repository;

import io.github.leonardofrs.funds_service.domain.model.Subscription;
import java.util.UUID;

public interface RetrieveSubscriptionRepository {

  Subscription execute(UUID clientId, UUID subscriptionId);
}
