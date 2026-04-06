package io.github.leonardofrs.funds_service.infrastructure.exceptions;

public class OptimisticLockingException extends RuntimeException {

  public OptimisticLockingException(String message) {
    super(message);
  }
}
