package io.github.leonardofrs.funds_service.domain.constants;

public enum NotificationType {
  EMAIL,
  SMS;

  public static NotificationType from(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("notification type is required");
    }

    try {
      return NotificationType.valueOf(value.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("unknown notification type: " + value);
    }
  }
}
