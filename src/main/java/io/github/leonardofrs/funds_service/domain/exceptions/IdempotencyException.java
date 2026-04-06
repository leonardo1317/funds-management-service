package io.github.leonardofrs.funds_service.domain.exceptions;

public class IdempotencyException extends RuntimeException {

  private final String key;
  private final String service;

  public IdempotencyException(String key, String service, String message) {
    super(message);
    this.key = key;
    this.service = service;
  }

  public String getKey() {
    return key;
  }

  public String getService() {
    return service;
  }
}
