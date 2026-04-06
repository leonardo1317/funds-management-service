package io.github.leonardofrs.funds_service.domain.gateway.subscription;

import io.github.leonardofrs.funds_service.domain.models.Subscription;
import java.util.UUID;

public interface RetrieveSubscriptionGateway {

  Subscription execute(UUID clientId, UUID subscriptionId);
}
