package io.github.leonardofrs.funds_service.infrastructure.repository.mongodb.document;

import io.github.leonardofrs.funds_service.domain.constants.SubscriptionStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "subscriptions")
public record SubscriptionDocument(@Id UUID id,
                                   UUID clientId,
                                   UUID fundId,
                                   String fundName,
                                   BigDecimal amount,
                                   SubscriptionStatus status,
                                   String cancellationReason,
                                   Instant createdAt,
                                   Instant updatedAt) {

}
