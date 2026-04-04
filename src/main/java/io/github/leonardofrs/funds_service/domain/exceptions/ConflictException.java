package io.github.leonardofrs.funds_service.domain.exceptions;

public class ConflictException extends RuntimeException {

  public ConflictException(String message) {
    super(message);
  }
}
