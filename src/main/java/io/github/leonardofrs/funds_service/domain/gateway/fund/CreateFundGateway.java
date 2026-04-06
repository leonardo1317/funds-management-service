package io.github.leonardofrs.funds_service.domain.gateway.fund;

import io.github.leonardofrs.funds_service.domain.models.Fund;

public interface CreateFundGateway {

  Fund execute(Fund fund);
}
