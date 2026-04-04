package io.github.leonardofrs.funds_service.domain.repository;

import java.util.UUID;

public interface CheckSubscriptionRepository {

  boolean execute(UUID clientId, UUID fundId);
}
