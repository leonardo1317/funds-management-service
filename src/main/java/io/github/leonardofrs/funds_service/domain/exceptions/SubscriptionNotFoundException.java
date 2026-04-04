package io.github.leonardofrs.funds_service.domain.exceptions;

public class SubscriptionNotFoundException extends RuntimeException {

  public SubscriptionNotFoundException(String message) {
    super(message);
  }
}
