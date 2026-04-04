package io.github.leonardofrs.funds_service.domain.exceptions;

public class ClientNotFoundException extends RuntimeException {

  public ClientNotFoundException(String message) {
    super(message);
  }
}
