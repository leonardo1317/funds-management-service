package io.github.leonardofrs.funds_service.application.usecases;

import io.github.leonardofrs.funds_service.domain.model.Client;

public interface CreateClient {

  Client execute(Client client);
}
