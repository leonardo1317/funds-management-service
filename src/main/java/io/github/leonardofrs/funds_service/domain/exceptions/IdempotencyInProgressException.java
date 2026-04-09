package io.github.leonardofrs.funds_service.domain.exceptions;

public class IdempotencyInProgressException extends RuntimeException {

  private final String key;
  private final String service;

  public IdempotencyInProgressException(String key, String service) {
    super(String.format("Operation with key '%s' is already in progress for service '%s'", key, service));
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
