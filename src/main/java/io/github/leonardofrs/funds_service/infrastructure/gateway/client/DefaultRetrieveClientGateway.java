package io.github.leonardofrs.funds_service.infrastructure.gateway.client;

import io.github.leonardofrs.funds_service.domain.exceptions.ClientNotFoundException;
import io.github.leonardofrs.funds_service.domain.models.Client;
import io.github.leonardofrs.funds_service.domain.gateway.client.RetrieveClientGateway;
import io.github.leonardofrs.funds_service.infrastructure.mappers.ClientMapper;
import io.github.leonardofrs.funds_service.infrastructure.gateway.documents.ClientDocument;
import java.util.UUID;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DefaultRetrieveClientGateway implements RetrieveClientGateway {

  private final MongoTemplate mongoTemplate;
  private final ClientMapper mapper;

  public DefaultRetrieveClientGateway(MongoTemplate mongoTemplate, ClientMapper mapper) {
    this.mongoTemplate = mongoTemplate;
    this.mapper = mapper;
  }

  @Override
  public Client execute(UUID clientId) {
    var clientDocument = mongoTemplate.findById(clientId, ClientDocument.class);
    if (clientDocument == null) {
      throw new ClientNotFoundException(String.format("Client not found %s", clientId));
    }

    return mapper.toClient(clientDocument);
  }
}
