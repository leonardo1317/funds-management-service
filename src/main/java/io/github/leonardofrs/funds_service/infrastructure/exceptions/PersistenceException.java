package io.github.leonardofrs.funds_service.infrastructure.exceptions;

public class PersistenceException extends RuntimeException {

  public PersistenceException(String message, Throwable cause) {
    super(message, cause);
  }
}
