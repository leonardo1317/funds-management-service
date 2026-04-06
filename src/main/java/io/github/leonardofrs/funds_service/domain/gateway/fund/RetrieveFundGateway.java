package io.github.leonardofrs.funds_service.domain.gateway.fund;

import io.github.leonardofrs.funds_service.domain.models.Fund;
import java.util.UUID;

public interface RetrieveFundGateway {

  Fund execute(UUID fundId);
}
