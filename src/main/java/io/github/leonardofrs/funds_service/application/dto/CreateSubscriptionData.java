package io.github.leonardofrs.funds_service.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateSubscriptionData(UUID fundId,
                                     BigDecimal amount) {

}
