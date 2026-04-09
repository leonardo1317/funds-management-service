package io.github.leonardofrs.funds_service.infrastructure.idempotency.impl;

import io.github.leonardofrs.funds_service.domain.exceptions.DuplicateIdempotencyKeyException;
import io.github.leonardofrs.funds_service.infrastructure.idempotency.CreateIdempotencyGateway;
import io.github.leonardofrs.funds_service.infrastructure.idempotency.Idempotency;
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
      throw new DuplicateIdempotencyKeyException(
          String.format("Idempotency key '%s' is already being used for service '%s'",
              idempotency.id(), idempotency.service()), e);
    }
  }
}
