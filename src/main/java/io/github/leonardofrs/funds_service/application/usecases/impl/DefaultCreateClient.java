package io.github.leonardofrs.funds_service.application.usecases.impl;

import io.github.leonardofrs.funds_service.application.usecases.CreateClient;
import io.github.leonardofrs.funds_service.domain.model.Client;
import io.github.leonardofrs.funds_service.domain.repository.CreateClientRepository;

public class DefaultCreateClient implements CreateClient {

  private final CreateClientRepository createClientRepository;

  public DefaultCreateClient(CreateClientRepository createClientRepository) {
    this.createClientRepository = createClientRepository;
  }

  @Override
  public Client execute(Client client) {

    return createClientRepository.execute(client);
  }
}
