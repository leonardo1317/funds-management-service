package io.github.leonardofrs.funds_service.infrastructure.controllers.contract;

import io.github.leonardofrs.funds_service.domain.constants.FundStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record FundResponse(UUID id,
                           String name,
                           BigDecimal minimumAmount,
                           String category,
                           FundStatus status,
                           Instant createdAt,
                           Instant updatedAt) {
}
