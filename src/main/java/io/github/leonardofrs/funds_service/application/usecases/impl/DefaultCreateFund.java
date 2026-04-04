package io.github.leonardofrs.funds_service.application.usecases.impl;

import io.github.leonardofrs.funds_service.application.usecases.CreateFund;
import io.github.leonardofrs.funds_service.domain.model.Fund;
import io.github.leonardofrs.funds_service.domain.repository.CreateFundRepository;

public class DefaultCreateFund implements CreateFund {

  private final CreateFundRepository createFundRepository;

  public DefaultCreateFund(CreateFundRepository createFundRepository) {
    this.createFundRepository = createFundRepository;
  }

  @Override
  public Fund execute(Fund fund) {

    return createFundRepository.execute(fund);
  }
}
