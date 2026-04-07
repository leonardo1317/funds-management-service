package io.github.leonardofrs.funds_service.domain.assertions;

import static java.util.Objects.isNull;

import java.math.BigDecimal;

public final class Assertions {

  private Assertions() {
  }

  public static String requireNonBlank(String value, String message) {
    if (isNull(value) || value.isBlank()) {
      throw new IllegalArgumentException(message);
    }
    return value;
  }
  public static BigDecimal requireNonNegative(BigDecimal value, String message) {
    if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException(message);
    }
    return value;
  }
  public static BigDecimal requirePositive(BigDecimal value, String message) {
    if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException(message);
    }
    return value;
  }
}
