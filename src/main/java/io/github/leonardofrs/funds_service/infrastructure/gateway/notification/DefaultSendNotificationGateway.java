package io.github.leonardofrs.funds_service.infrastructure.gateway.notification;

import io.github.leonardofrs.funds_service.domain.constants.NotificationType;
import io.github.leonardofrs.funds_service.domain.gateway.notification.SendNotificationGateway;
import io.github.leonardofrs.funds_service.domain.gateway.notification.NotificationStrategyGateway;
import io.github.leonardofrs.funds_service.domain.vo.Notification;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultSendNotificationGateway implements SendNotificationGateway {

  private static final Logger log = LoggerFactory.getLogger(DefaultSendNotificationGateway.class);
  private final Map<NotificationType, NotificationStrategyGateway> strategies;
  private final ExecutorService executor;

  public DefaultSendNotificationGateway(List<NotificationStrategyGateway> strategies) {
    this.strategies = strategies.stream()
        .collect(Collectors.toMap(
            NotificationStrategyGateway::getType,
            Function.identity()
        ));
    this.executor = Executors.newFixedThreadPool(5);
  }

  @Override
  public void execute(Notification notification) {
    CompletableFuture
        .runAsync(() -> sendNotification(notification), executor)
        .orTimeout(5, TimeUnit.SECONDS)
        .exceptionally(throwable -> {
          log.error("Notification failed after retries: {}", throwable.getMessage());
          return null;
        });
  }

  private void sendNotification(Notification notification) {
    var type = notification.type();
    NotificationStrategyGateway strategy = strategies.get(notification.type());
    if (strategy == null) {
      throw new IllegalArgumentException("No strategy for: " + type);
    }
    strategy.send(notification);
  }
}
