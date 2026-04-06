package io.github.leonardofrs.funds_service.domain.gateway.client;

import io.github.leonardofrs.funds_service.domain.models.Client;

public interface UpdateClientGateway {

  Client execute(Client client);
}
