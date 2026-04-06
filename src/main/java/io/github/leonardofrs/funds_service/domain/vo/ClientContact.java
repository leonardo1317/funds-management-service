package io.github.leonardofrs.funds_service.domain.vo;

import static io.github.leonardofrs.funds_service.domain.assertions.Assertions.requireNonBlank;

public record ClientContact(String email,
                            String phone,
                            String fullName) {

  public ClientContact {
    requireNonBlank(email, "email cannot be blank");
    requireNonBlank(phone, "phone cannot be blank");
    requireNonBlank(fullName, "fullName cannot be blank");
  }

  public static ClientContact of(String email, String phone, String fullName) {
    return new ClientContact(email, phone, fullName);
  }
}
