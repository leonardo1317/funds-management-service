package io.github.leonardofrs.funds_service.domain.exceptions;

public class EmailAlreadyExistsException extends ConflictException {

  public EmailAlreadyExistsException(String message) {
    super(message);
  }
}
