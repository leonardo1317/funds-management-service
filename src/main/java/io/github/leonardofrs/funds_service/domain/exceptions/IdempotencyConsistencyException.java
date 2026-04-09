package io.github.leonardofrs.funds_service.domain.exceptions;

public class IdempotencyConsistencyException extends RuntimeException {

  public IdempotencyConsistencyException(String message) {
    super(message);
  }
}
