package io.github.leonardofrs.funds_service.domain.gateway.notification;

import io.github.leonardofrs.funds_service.domain.vo.Notification;

public interface SendNotificationGateway {

  void execute (Notification notification);
}
