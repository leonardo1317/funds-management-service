package io.github.leonardofrs.funds_service.application.usecases.impl;

import io.github.leonardofrs.funds_service.application.usecases.SendNotification;
import io.github.leonardofrs.funds_service.domain.constants.NotificationType;
import io.github.leonardofrs.funds_service.domain.gateway.notification.SendNotificationGateway;
import io.github.leonardofrs.funds_service.domain.models.Client;
import io.github.leonardofrs.funds_service.domain.models.Fund;
import io.github.leonardofrs.funds_service.domain.vo.ClientContact;
import io.github.leonardofrs.funds_service.domain.vo.Notification;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSendNotification implements SendNotification {

  private static final String SUBJECT = "Notificación BTG Pactual";
  private static final String BODY_TEMPLATE = "Hola %s, se ha realizado una operación en tu cuenta por $%.2f COP en el fondo %s.";
  private static final Logger log = LoggerFactory.getLogger(DefaultSendNotification.class);

  private final SendNotificationGateway sendNotificationGateway;

  public DefaultSendNotification(SendNotificationGateway sendNotificationGateway) {
    this.sendNotificationGateway = sendNotificationGateway;
  }

  @Override
  public void execute(Client client, String fundName, BigDecimal amount) {
    String body = String.format(BODY_TEMPLATE, client.fullName(), amount, fundName);

    for (String channel : client.notificationChannels()) {
      try {
        NotificationType type = NotificationType.valueOf(channel.toUpperCase());
        var clientContact = ClientContact.of(client.email(), client.phone(), client.fullName());
        Notification notification = Notification.of(clientContact, type, SUBJECT, body);
        sendNotificationGateway.execute(notification);
      } catch (IllegalArgumentException e) {
        log.warn("Unknown notification channel: {} for client {}", channel, client.id());
      } catch (Exception e) {
        log.error("Failed to send {} notification to client {}: {}",
            channel, client.id(), e.getMessage(), e);
      }
    }
  }
}
