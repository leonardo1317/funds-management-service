package io.github.leonardofrs.funds_service.infrastructure.controllers.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PageResponse<T>(
    @JsonProperty("items")
    List<T> items,
    @JsonProperty("pagination")
    Pagination pagination
) {

  public static <T> PageResponse<T> of(
      List<T> items,
      int offset,
      int limit,
      long total) {

    return new PageResponse<>(
        items,
        new Pagination(offset, limit, total)
    );
  }

  public record Pagination(
      @JsonProperty("offset")
      int offset,
      @JsonProperty("limit")
      int limit,
      @JsonProperty("total")
      long total
  ) {

  }
}
