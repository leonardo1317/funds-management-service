package io.github.leonardofrs.funds_service.domain.repository;

import io.github.leonardofrs.funds_service.domain.model.Client;

public interface UpdateClientRepository {

  Client execute(Client client);
}
