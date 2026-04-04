package io.github.leonardofrs.funds_service.infrastructure.repository.mongodb.document;

import io.github.leonardofrs.funds_service.domain.constants.FundStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("funds")
public record FundDocument(@Id UUID id,
                           String name,
                           BigDecimal minimumAmount,
                           String category,
                           FundStatus status,
                           Instant createdAt,
                           Instant updatedAt) {

}
