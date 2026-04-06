package io.github.leonardofrs.funds_service.infrastructure.gateway.idempotency;

import io.github.leonardofrs.funds_service.domain.gateway.Idempotency.RetrieveIdempotencyGateway;
import io.github.leonardofrs.funds_service.domain.models.Idempotency;
import io.github.leonardofrs.funds_service.infrastructure.gateway.documents.IdempotencyDocument;
import io.github.leonardofrs.funds_service.infrastructure.mappers.IdempotencyMapper;
import java.util.Optional;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DefaultRetrieveIdempotencyGateway implements RetrieveIdempotencyGateway {

  private final MongoTemplate mongoTemplate;
  private final IdempotencyMapper mapper;

  public DefaultRetrieveIdempotencyGateway(MongoTemplate mongoTemplate, IdempotencyMapper mapper) {
    this.mongoTemplate = mongoTemplate;
    this.mapper = mapper;
  }

  @Override
  public Optional<Idempotency> execute(String key) {
    IdempotencyDocument document = mongoTemplate.findById(key, IdempotencyDocument.class);
    return Optional.ofNullable(document).map(mapper::toIdempotency);
  }
}
