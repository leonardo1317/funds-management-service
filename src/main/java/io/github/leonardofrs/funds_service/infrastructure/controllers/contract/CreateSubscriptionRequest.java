package io.github.leonardofrs.funds_service.infrastructure.controllers.contract;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateSubscriptionRequest(UUID fundId,
                                        BigDecimal amount) {

}
