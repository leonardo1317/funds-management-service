package io.github.leonardofrs.funds_service.infrastructure.mappers;

import io.github.leonardofrs.funds_service.domain.dto.CancelSubscriptionData;
import io.github.leonardofrs.funds_service.domain.dto.CreateSubscriptionData;
import io.github.leonardofrs.funds_service.domain.model.Subscription;
import io.github.leonardofrs.funds_service.infrastructure.controllers.contract.CancelSubscriptionRequest;
import io.github.leonardofrs.funds_service.infrastructure.controllers.contract.CreateSubscriptionRequest;
import io.github.leonardofrs.funds_service.infrastructure.repository.mongodb.document.SubscriptionDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

  SubscriptionDocument toSubscriptionDocument(Subscription subscription);

  Subscription toSubscription(SubscriptionDocument subscriptionDocument);

  CreateSubscriptionData toCreateSubscriptionData(
      CreateSubscriptionRequest createSubscriptionRequest);

  CancelSubscriptionData toCancelSubscriptionData(
      CancelSubscriptionRequest cancelSubscriptionRequest);
}
