package io.github.leonardofrs.funds_service.infrastructure.idempotency.impl;

import io.github.leonardofrs.funds_service.domain.constants.IdempotencyStatus;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "idempotency")
@CompoundIndex(
    name = "idx_unique_key_service",
    def = "{'id': 1, 'service': 1}",
    unique = true
)
public record IdempotencyDocument(@Id
                                  String id,
                                  String service,
                                  IdempotencyStatus status,
                                  String response,
                                  Long version,
                                  Instant createdAt,
                                  Instant updatedAt,
                                  @Indexed(name = "idx_idempotency_expires_at", expireAfter = "0s")
                                  Instant expiresAt
) {

}
