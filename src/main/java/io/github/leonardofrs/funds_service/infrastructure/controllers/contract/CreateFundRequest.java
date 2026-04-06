package io.github.leonardofrs.funds_service.infrastructure.controllers.contract;

import java.math.BigDecimal;

public record CreateFundRequest(String name,
                                BigDecimal minimumAmount,
                                String category) {

}
