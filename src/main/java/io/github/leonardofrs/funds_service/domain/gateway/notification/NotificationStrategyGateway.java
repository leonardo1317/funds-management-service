package io.github.leonardofrs.funds_service.domain.gateway.notification;

import io.github.leonardofrs.funds_service.domain.constants.NotificationType;
import io.github.leonardofrs.funds_service.domain.vo.Notification;

public interface NotificationStrategyGateway {

  NotificationType getType();

  void send(Notification notification);
}
