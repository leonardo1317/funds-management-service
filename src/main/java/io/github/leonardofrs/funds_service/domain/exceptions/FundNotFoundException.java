package io.github.leonardofrs.funds_service.domain.exceptions;

public class FundNotFoundException extends RuntimeException {

  public FundNotFoundException(String message) {
    super(message);
  }
}
