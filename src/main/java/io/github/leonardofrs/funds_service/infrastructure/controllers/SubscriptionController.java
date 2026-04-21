package io.github.leonardofrs.funds_service.infrastructure.controllers;

import static io.github.leonardofrs.funds_service.infrastructure.controllers.constants.Services.SUBSCRIPTION;

import io.github.leonardofrs.funds_service.application.dto.CancelSubscriptionData;
import io.github.leonardofrs.funds_service.application.dto.CreateSubscriptionData;
import io.github.leonardofrs.funds_service.application.usecases.CancelSubscription;
import io.github.leonardofrs.funds_service.application.usecases.CreateSubscription;
import io.github.leonardofrs.funds_service.infrastructure.idempotency.IdempotencyHandler;
import io.github.leonardofrs.funds_service.infrastructure.controllers.contract.CancelSubscriptionRequest;
import io.github.leonardofrs.funds_service.infrastructure.controllers.contract.CreateSubscriptionRequest;
import io.github.leonardofrs.funds_service.infrastructure.controllers.contract.SubscriptionResponse;
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
  private final IdempotencyHandler idempotencyHandler;
  private final SubscriptionMapper subscriptionMapper;

  public SubscriptionController(
      CreateSubscription createSubscription,
      CancelSubscription cancelSubscription,
      IdempotencyHandler idempotencyHandler,
      SubscriptionMapper subscriptionMapper
  ) {
    this.createSubscription = createSubscription;
    this.cancelSubscription = cancelSubscription;
    this.idempotencyHandler = idempotencyHandler;
    this.subscriptionMapper = subscriptionMapper;
  }

  @PostMapping
  public ResponseEntity<SubscriptionResponse> subscribe(
      @RequestHeader("idempotency-key") String idempotencyKey,
      @RequestHeader("X-Client-Id") UUID clientId,
      @RequestBody CreateSubscriptionRequest createSubscriptionRequest) {

    CreateSubscriptionData createSubscriptionData = subscriptionMapper.toCreateSubscriptionData(
        createSubscriptionRequest);

    SubscriptionResponse subscriptionResponse = idempotencyHandler.execute(
        idempotencyKey,
        SUBSCRIPTION.name(),
        () -> subscriptionMapper.toSubscriptionResponse(
            createSubscription.execute(clientId, createSubscriptionData)),
        SubscriptionResponse.class
    );

    return ResponseEntity.ok(subscriptionResponse);

  }

  @PostMapping("/{subscriptionId}/cancel")
  public ResponseEntity<SubscriptionResponse> cancel(
      @RequestHeader("idempotency-key") String idempotencyKey,
      @RequestHeader("X-Client-Id") UUID clientId,
      @PathVariable UUID subscriptionId,
      @RequestBody CancelSubscriptionRequest cancelSubscriptionRequest) {
    CancelSubscriptionData cancelSubscriptionData = subscriptionMapper.toCancelSubscriptionData(
        cancelSubscriptionRequest);

    SubscriptionResponse subscriptionResponse = idempotencyHandler.execute(
        idempotencyKey,
        SUBSCRIPTION.name(),
        () -> subscriptionMapper.toSubscriptionResponse(
            cancelSubscription.execute(clientId, subscriptionId, cancelSubscriptionData)),
        SubscriptionResponse.class
    );

    return ResponseEntity.ok(subscriptionResponse);
  }
}
