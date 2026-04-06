package io.github.leonardofrs.funds_service.application.usecases.impl;

import io.github.leonardofrs.funds_service.application.usecases.CreateFund;
import io.github.leonardofrs.funds_service.domain.models.Fund;
import io.github.leonardofrs.funds_service.domain.gateway.fund.CreateFundGateway;

public class DefaultCreateFund implements CreateFund {

  private final CreateFundGateway createFundGateway;

  public DefaultCreateFund(CreateFundGateway createFundGateway) {
    this.createFundGateway = createFundGateway;
  }

  @Override
  public Fund execute(Fund fund) {

    return createFundGateway.execute(fund);
  }
}
