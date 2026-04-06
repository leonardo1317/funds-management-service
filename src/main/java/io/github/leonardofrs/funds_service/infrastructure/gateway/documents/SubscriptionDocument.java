package io.github.leonardofrs.funds_service.infrastructure.gateway.documents;

import io.github.leonardofrs.funds_service.domain.constants.SubscriptionStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "subscriptions")
@CompoundIndex(
    name = "idx_subscriptions_client_fund_status",
    def = "{'clientId': 1, 'fundId': 1, 'status': 1}"
)
public record SubscriptionDocument(@Id
                                   UUID id,
                                   UUID clientId,
                                   UUID fundId,
                                   String fundName,
                                   BigDecimal amount,
                                   SubscriptionStatus status,
                                   String cancellationReason,
                                   Instant createdAt,
                                   Instant updatedAt) {

}
