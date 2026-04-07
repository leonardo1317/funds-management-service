package io.github.leonardofrs.funds_service.application.usecases.impl;

import static java.util.Objects.requireNonNull;

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
    requireNonNull(client, "client is required");
    return createClientGateway.execute(client);
  }
}
