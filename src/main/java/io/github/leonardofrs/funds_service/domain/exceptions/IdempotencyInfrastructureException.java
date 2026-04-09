package io.github.leonardofrs.funds_service.domain.exceptions;

public class IdempotencyInfrastructureException extends RuntimeException {

  public IdempotencyInfrastructureException(String message, Throwable cause) {
    super(message, cause);
  }
}
