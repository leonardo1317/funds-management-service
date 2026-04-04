package io.github.leonardofrs.funds_service.domain.repository;

import io.github.leonardofrs.funds_service.domain.model.Fund;
import java.util.UUID;

public interface RetrieveFundRepository {

  Fund execute(UUID fundId);
}
