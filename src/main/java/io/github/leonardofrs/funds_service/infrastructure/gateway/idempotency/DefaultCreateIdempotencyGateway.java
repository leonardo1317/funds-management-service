package io.github.leonardofrs.funds_service.infrastructure.gateway.idempotency;

import io.github.leonardofrs.funds_service.domain.exceptions.IdempotencyAlreadyExistsException;
import io.github.leonardofrs.funds_service.domain.gateway.Idempotency.CreateIdempotencyGateway;
import io.github.leonardofrs.funds_service.domain.models.Idempotency;
import io.github.leonardofrs.funds_service.infrastructure.gateway.documents.IdempotencyDocument;
import io.github.leonardofrs.funds_service.infrastructure.mappers.IdempotencyMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DefaultCreateIdempotencyGateway implements CreateIdempotencyGateway {

  private final MongoTemplate mongoTemplate;
  private final IdempotencyMapper mapper;

  public DefaultCreateIdempotencyGateway(MongoTemplate mongoTemplate, IdempotencyMapper mapper) {
    this.mongoTemplate = mongoTemplate;
    this.mapper = mapper;
  }

  @Override
  public Idempotency execute(Idempotency idempotency) {
    try {
      IdempotencyDocument document = mapper.toIdempotencyDocument(idempotency);
      return mapper.toIdempotency(mongoTemplate.insert(document));
    } catch (DuplicateKeyException e) {
      throw new IdempotencyAlreadyExistsException("Idempotency key already exists: " + idempotency.id(), e);
    }
  }
}
