package io.github.leonardofrs.funds_service.domain.repository;

import io.github.leonardofrs.funds_service.domain.model.Client;

public interface CreateClientRepository {

  Client execute(Client client);
}
