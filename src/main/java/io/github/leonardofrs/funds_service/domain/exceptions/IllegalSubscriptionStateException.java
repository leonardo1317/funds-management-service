package io.github.leonardofrs.funds_service.domain.exceptions;

public class IllegalSubscriptionStateException extends BusinessRuleException {

  public IllegalSubscriptionStateException(String message) {
    super(message);
  }
}
