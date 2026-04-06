package io.github.leonardofrs.funds_service.infrastructure.gateway.notification;

import io.github.leonardofrs.funds_service.domain.constants.NotificationType;
import io.github.leonardofrs.funds_service.domain.gateway.notification.NotificationStrategyGateway;
import io.github.leonardofrs.funds_service.domain.vo.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailStrategy implements NotificationStrategyGateway {

  private static final Logger log = LoggerFactory.getLogger(EmailStrategy.class);

  @Override
  public NotificationType getType() {
    return NotificationType.EMAIL;
  }

  @Override
  public void send(Notification notification) {
    log.info("EMAIL to: {} - {}", notification.contact().email(), notification.body());
  }
}
