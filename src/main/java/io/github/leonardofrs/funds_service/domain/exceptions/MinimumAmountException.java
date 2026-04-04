package io.github.leonardofrs.funds_service.domain.exceptions;

public class MinimumAmountException extends BusinessRuleException {

  public MinimumAmountException(String message) {
    super(message);
  }
}
