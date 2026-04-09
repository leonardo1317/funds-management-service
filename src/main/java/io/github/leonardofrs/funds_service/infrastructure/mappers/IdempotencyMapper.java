package io.github.leonardofrs.funds_service.infrastructure.mappers;

import io.github.leonardofrs.funds_service.infrastructure.idempotency.Idempotency;
import io.github.leonardofrs.funds_service.infrastructure.idempotency.impl.IdempotencyDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IdempotencyMapper {

  IdempotencyDocument toIdempotencyDocument(Idempotency idempotency);

  Idempotency toIdempotency(IdempotencyDocument idempotencyDocument);
}
