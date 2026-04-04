package io.github.leonardofrs.funds_service.infrastructure.repository.mongodb;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoWriteException;
import io.github.leonardofrs.funds_service.domain.exceptions.ConflictException;
import io.github.leonardofrs.funds_service.domain.exceptions.EmailAlreadyExistsException;
import io.github.leonardofrs.funds_service.domain.exceptions.PhoneAlreadyExistsException;
import io.github.leonardofrs.funds_service.domain.model.Client;
import io.github.leonardofrs.funds_service.domain.repository.CreateClientRepository;
import io.github.leonardofrs.funds_service.infrastructure.exceptions.PersistenceException;
import io.github.leonardofrs.funds_service.infrastructure.mappers.ClientMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DefaultCreateClientRepository implements CreateClientRepository {

  private static final int MONGO_DUPLICATE_KEY_CODE = 11000;
  private final MongoTemplate mongoTemplate;
  private final ClientMapper clientMapper;

  public DefaultCreateClientRepository(
      MongoTemplate mongoTemplate, ClientMapper clientMapper
  ) {
    this.mongoTemplate = mongoTemplate;
    this.clientMapper = clientMapper;
  }

  @Override
  public Client execute(Client client) {
    try {
      var clientDocument = clientMapper.toClientDocument(client);
      return clientMapper.toClient(mongoTemplate.save(clientDocument));
    } catch (DuplicateKeyException ex) {
      if (isDuplicateKeyError(ex)) {
        throw handleSpecificDuplicateKey(ex, client);
      }

      throw new PersistenceException("Unexpected error saving client", ex);
    }
  }

  private boolean isDuplicateKeyError(DuplicateKeyException ex) {
    if (ex.getCause() instanceof MongoWriteException mongoWriteEx) {
      return mongoWriteEx.getError().getCode() == MONGO_DUPLICATE_KEY_CODE;
    }
    return false;
  }

  private ConflictException handleSpecificDuplicateKey(DuplicateKeyException ex, Client client) {
    String message = ex.getMessage();

    if (message.contains("idx_clients_email")) {
      return new EmailAlreadyExistsException(
          String.format("Email '%s' is already registered.", client.email()));
    }

    if (message.contains("idx_clients_phone")) {
      return new PhoneAlreadyExistsException(
          String.format("Phone number '%s' is already registered.", client.phone()));
    }

    return new ConflictException("Client data is already registered.");
  }
}
