package io.github.leonardofrs.funds_service.domain.repository;

import io.github.leonardofrs.funds_service.domain.model.Fund;

public interface CreateFundRepository {

  Fund execute(Fund fund);
}
