package io.github.leonardofrs.funds_service.infrastructure.mappers;

import io.github.leonardofrs.funds_service.domain.models.Idempotency;
import io.github.leonardofrs.funds_service.infrastructure.gateway.documents.IdempotencyDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IdempotencyMapper {

  IdempotencyDocument toIdempotencyDocument(Idempotency idempotency);

  Idempotency toIdempotency(IdempotencyDocument idempotencyDocument);
}
