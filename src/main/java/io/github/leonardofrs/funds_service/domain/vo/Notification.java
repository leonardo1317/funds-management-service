package io.github.leonardofrs.funds_service.domain.vo;

import io.github.leonardofrs.funds_service.domain.constants.NotificationType;

public record Notification(ClientContact contact,
                           NotificationType type,
                           String subject,
                           String body) {

  public static Notification of(ClientContact contact, NotificationType type,
      String subject, String body) {
    return new Notification(contact, type, subject, body);
  }
}
