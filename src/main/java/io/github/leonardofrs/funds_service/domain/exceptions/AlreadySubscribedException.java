package io.github.leonardofrs.funds_service.domain.exceptions;

public class AlreadySubscribedException extends BusinessRuleException {

  public AlreadySubscribedException(String message) {
    super(message);
  }
}
