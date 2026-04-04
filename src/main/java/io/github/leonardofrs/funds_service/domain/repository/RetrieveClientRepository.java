package io.github.leonardofrs.funds_service.domain.repository;

import io.github.leonardofrs.funds_service.domain.model.Client;
import java.util.UUID;

public interface RetrieveClientRepository {

  Client execute(UUID clientId);
}
