package io.github.leonardofrs.funds_service.infrastructure.repository.mongodb;

import io.github.leonardofrs.funds_service.domain.exceptions.ClientNotFoundException;
import io.github.leonardofrs.funds_service.domain.model.Client;
import io.github.leonardofrs.funds_service.domain.repository.RetrieveClientRepository;
import io.github.leonardofrs.funds_service.infrastructure.mappers.ClientMapper;
import io.github.leonardofrs.funds_service.infrastructure.repository.mongodb.document.ClientDocument;
import java.util.UUID;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DefaultRetrieveClientRepository implements RetrieveClientRepository {

  private final MongoTemplate mongoTemplate;
  private final ClientMapper clientMapper;

  public DefaultRetrieveClientRepository(MongoTemplate mongoTemplate, ClientMapper clientMapper) {
    this.mongoTemplate = mongoTemplate;
    this.clientMapper = clientMapper;
  }

  @Override
  public Client execute(UUID clientId) {
    var clientDocument = mongoTemplate.findById(clientId, ClientDocument.class);
    if (clientDocument == null) {
      throw new ClientNotFoundException(String.format("Client not found %s", clientId));
    }

    return clientMapper.toClient(clientDocument);
  }
}
