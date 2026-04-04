package io.github.leonardofrs.funds_service.infrastructure.repository.mongodb.document;

import io.github.leonardofrs.funds_service.domain.constants.ClientStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "clients")
public record ClientDocument(@Id UUID id,
                             String fullName,
                             String email,
                             String phone,
                             List<String> notificationChannels,
                             BigDecimal balance,
                             ClientStatus status,
                             Long version,
                             Instant createdAt,
                             Instant updatedAt) {

}
