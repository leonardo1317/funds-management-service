package io.github.leonardofrs.funds_service.application.usecases;

import io.github.leonardofrs.funds_service.domain.models.Fund;

public interface CreateFund {

  Fund execute(Fund fund);
}
