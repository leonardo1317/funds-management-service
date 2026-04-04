package io.github.leonardofrs.funds_service.domain.exceptions;

public class PhoneAlreadyExistsException extends ConflictException {

  public PhoneAlreadyExistsException(String message) {
    super(message);
  }
}
