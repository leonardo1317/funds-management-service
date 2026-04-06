package io.github.leonardofrs.funds_service.domain.exceptions;

public class IdempotencyAlreadyExistsException extends ConflictException {

  public IdempotencyAlreadyExistsException(String message, Throwable cause) {
    super(message, cause);
  }
}
