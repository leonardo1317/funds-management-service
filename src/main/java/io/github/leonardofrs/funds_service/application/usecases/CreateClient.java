package io.github.leonardofrs.funds_service.application.usecases;

import io.github.leonardofrs.funds_service.domain.models.Client;

public interface CreateClient {

  Client execute(Client client);
}
