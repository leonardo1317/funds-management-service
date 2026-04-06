package io.github.leonardofrs.funds_service.infrastructure.controllers.contract;

import io.github.leonardofrs.funds_service.domain.constants.SubscriptionStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SubscriptionResponse(UUID id,
                                   UUID clientId,
                                   UUID fundId,
                                   String fundName,
                                   BigDecimal amount,
                                   SubscriptionStatus status,
                                   String cancellationReason,
                                   Instant createdAt,
                                   Instant updatedAt) {

}
