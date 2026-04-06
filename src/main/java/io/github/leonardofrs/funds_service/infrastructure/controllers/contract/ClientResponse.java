package io.github.leonardofrs.funds_service.infrastructure.controllers.contract;

import io.github.leonardofrs.funds_service.domain.constants.ClientStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ClientResponse(UUID id,
                             String fullName,
                             String email,
                             String phone,
                             List<String> notificationChannels,
                             BigDecimal balance,
                             ClientStatus status,
                             Instant createdAt,
                             Instant updatedAt
) {

}
