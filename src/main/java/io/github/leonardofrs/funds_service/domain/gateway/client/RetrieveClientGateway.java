package io.github.leonardofrs.funds_service.domain.gateway.client;

import io.github.leonardofrs.funds_service.domain.models.Client;
import java.util.UUID;

public interface RetrieveClientGateway {

  Client execute(UUID clientId);
}
