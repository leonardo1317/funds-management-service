package io.github.leonardofrs.funds_service.infrastructure.gateway.documents;

import io.github.leonardofrs.funds_service.domain.constants.IdempotencyStatus;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "idempotency")
public record IdempotencyDocument(@Id
                                  String id,
                                  String service,
                                  IdempotencyStatus status,
                                  String response,
                                  @Indexed(name = "idx_idempotency_expires_at", expireAfter = "0s")
                                  Instant expiresAt
) {

}
