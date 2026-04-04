package io.github.leonardofrs.funds_service.domain.exceptions;

public class BusinessRuleException extends RuntimeException {

  public BusinessRuleException(String message) {
    super(message);
  }
}
