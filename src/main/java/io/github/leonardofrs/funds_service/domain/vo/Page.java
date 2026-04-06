package io.github.leonardofrs.funds_service.domain.vo;

public record Page(
    int offset,
    int limit
) {

  private static final int MAX_LIMIT = 100;

  public Page {
    if (offset < 0) {
      throw new IllegalArgumentException(
          "offset must be greater than or equal to 0");
    }
    if (limit <= 0) {
      throw new IllegalArgumentException(
          "limit must be greater than 0");
    }
    if (limit > MAX_LIMIT) {
      throw new IllegalArgumentException(
          "limit must be less than or equal to " + MAX_LIMIT);
    }
  }

  public static Page of(int offset, int limit) {
    return new Page(offset, limit);
  }
}
