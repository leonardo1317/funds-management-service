package io.github.leonardofrs.funds_service.domain.exceptions;

public class InsufficientBalanceException extends BusinessRuleException {

  public InsufficientBalanceException(String message) {
    super(message);
  }
}
