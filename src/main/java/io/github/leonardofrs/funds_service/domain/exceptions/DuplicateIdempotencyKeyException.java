package io.github.leonardofrs.funds_service.domain.exceptions;

public class DuplicateIdempotencyKeyException extends ConflictException {

  public DuplicateIdempotencyKeyException(String message, Throwable cause) {
    super(message, cause);
  }
}
