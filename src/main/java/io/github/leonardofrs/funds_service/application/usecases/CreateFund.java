package io.github.leonardofrs.funds_service.application.usecases;

import io.github.leonardofrs.funds_service.domain.model.Fund;

public interface CreateFund {

  Fund execute(Fund fund);
}
