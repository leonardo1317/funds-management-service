package io.github.leonardofrs.funds_service.infrastructure.controllers;

import io.github.leonardofrs.funds_service.application.usecases.CancelSubscription;
import io.github.leonardofrs.funds_service.application.usecases.CreateSubscription;
import io.github.leonardofrs.funds_service.domain.model.Subscription;
import io.github.leonardofrs.funds_service.infrastructure.controllers.contract.CancelSubscriptionRequest;
import io.github.leonardofrs.funds_service.infrastructure.controllers.contract.CreateSubscriptionRequest;
import io.github.leonardofrs.funds_service.infrastructure.mappers.SubscriptionMapper;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/subscriptions")
public class SubscriptionController {

  private final CreateSubscription createSubscription;
  private final CancelSubscription cancelSubscription;
  private final SubscriptionMapper subscriptionMapper;

  public SubscriptionController(
      CreateSubscription createSubscription,
      CancelSubscription cancelSubscription,
      SubscriptionMapper subscriptionMapper
  ) {
    this.createSubscription = createSubscription;
    this.cancelSubscription = cancelSubscription;
    this.subscriptionMapper = subscriptionMapper;
  }

  @PostMapping
  public ResponseEntity<Subscription> subscribe(
      @RequestHeader("X-Client-Id") UUID clientId,
      @RequestBody CreateSubscriptionRequest createSubscriptionRequest) {
    return ResponseEntity.ok(createSubscription.execute(clientId,
        subscriptionMapper.toCreateSubscriptionData(createSubscriptionRequest)));
  }

  @PostMapping("/{subscriptionId}/cancel")
  public ResponseEntity<Subscription> cancel(
      @RequestHeader("X-Client-Id") UUID clientId,
      @PathVariable UUID subscriptionId,
      @RequestBody CancelSubscriptionRequest cancelSubscriptionRequest) {
    return ResponseEntity.ok(cancelSubscription.execute(clientId, subscriptionId, subscriptionMapper.toCancelSubscriptionData(cancelSubscriptionRequest)));
  }
}
