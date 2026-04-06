package io.github.leonardofrs.funds_service.domain.gateway.subscription;

import java.util.UUID;

public interface CheckSubscriptionGateway {

  boolean execute(UUID clientId, UUID fundId);
}
