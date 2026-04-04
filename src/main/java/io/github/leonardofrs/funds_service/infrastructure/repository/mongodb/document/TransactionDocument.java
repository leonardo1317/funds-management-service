package io.github.leonardofrs.funds_service.infrastructure.repository.mongodb.document;

import io.github.leonardofrs.funds_service.domain.constants.MovementType;
import io.github.leonardofrs.funds_service.domain.constants.TransactionStatus;
import io.github.leonardofrs.funds_service.domain.constants.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("transactions")
public record TransactionDocument(@Id UUID id,
                                  UUID clientId,
                                  UUID fundId,
                                  String fundName,
                                  UUID subscriptionId,
                                  TransactionType type,
                                  MovementType movementType,
                                  BigDecimal amount,
                                  BigDecimal balanceBefore,
                                  BigDecimal balanceAfter,
                                  TransactionStatus status,
                                  String errorMessage,
                                  Instant createdAt) {

}
