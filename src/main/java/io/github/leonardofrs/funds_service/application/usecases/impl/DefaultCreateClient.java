package io.github.leonardofrs.funds_service.application.usecases.impl;

import io.github.leonardofrs.funds_service.application.usecases.CreateClient;
import io.github.leonardofrs.funds_service.domain.models.Client;
import io.github.leonardofrs.funds_service.domain.gateway.client.CreateClientGateway;

public class DefaultCreateClient implements CreateClient {

  private final CreateClientGateway createClientGateway;

  public DefaultCreateClient(CreateClientGateway createClientGateway) {
    this.createClientGateway = createClientGateway;
  }

  @Override
  public Client execute(Client client) {

    return createClientGateway.execute(client);
  }
}
