package io.github.leonardofrs.funds_service.application.usecases.impl;

import static io.github.leonardofrs.funds_service.domain.constants.MovementType.CREDIT;
import static io.github.leonardofrs.funds_service.domain.constants.TransactionType.CANCELLATION;
import static java.util.Objects.requireNonNull;

import io.github.leonardofrs.funds_service.application.usecases.CancelSubscription;
import io.github.leonardofrs.funds_service.application.usecases.SendNotification;
import io.github.leonardofrs.funds_service.domain.constants.SubscriptionStatus;
import io.github.leonardofrs.funds_service.application.dto.CancelSubscriptionData;
import io.github.leonardofrs.funds_service.domain.exceptions.BusinessRuleException;
import io.github.leonardofrs.funds_service.domain.models.Client;
import io.github.leonardofrs.funds_service.domain.models.Subscription;
import io.github.leonardofrs.funds_service.domain.models.Transaction;
import io.github.leonardofrs.funds_service.domain.gateway.transactions.CreateTransactionGateway;
import io.github.leonardofrs.funds_service.domain.gateway.client.RetrieveClientGateway;
import io.github.leonardofrs.funds_service.domain.gateway.subscription.RetrieveSubscriptionGateway;
import io.github.leonardofrs.funds_service.domain.gateway.TransactionalHandlerGateway;
import io.github.leonardofrs.funds_service.domain.gateway.client.UpdateClientGateway;
import io.github.leonardofrs.funds_service.domain.gateway.subscription.CancelSubscriptionGateway;
import java.util.UUID;

public class DefaultCancelSubscription implements CancelSubscription {

  private final RetrieveSubscriptionGateway retrieveSubscriptionGateway;
  private final RetrieveClientGateway retrieveClientGateway;
  private final UpdateClientGateway updateClientGateway;
  private final CancelSubscriptionGateway cancelSubscriptionGateway;
  private final CreateTransactionGateway createTransactionGateway;
  private final TransactionalHandlerGateway transactionalHandlerGateway;
  private final SendNotification sendNotification;

  public DefaultCancelSubscription(
      RetrieveSubscriptionGateway retrieveSubscriptionGateway,
      RetrieveClientGateway retrieveClientGateway,
      UpdateClientGateway updateClientGateway,
      CancelSubscriptionGateway cancelSubscriptionGateway,
      CreateTransactionGateway createTransactionGateway,
      TransactionalHandlerGateway transactionalHandlerGateway,
      SendNotification sendNotification
  ) {
    this.retrieveSubscriptionGateway = retrieveSubscriptionGateway;
    this.retrieveClientGateway = retrieveClientGateway;
    this.updateClientGateway = updateClientGateway;
    this.cancelSubscriptionGateway = cancelSubscriptionGateway;
    this.createTransactionGateway = createTransactionGateway;
    this.transactionalHandlerGateway = transactionalHandlerGateway;
    this.sendNotification = sendNotification;
  }

  @Override
  public Subscription execute(UUID clientId, UUID subscriptionId,
      CancelSubscriptionData cancelSubscriptionData) {
    requireNonNull(cancelSubscriptionData);
    Subscription subscription = retrieveSubscriptionGateway.execute(clientId, subscriptionId);
    Client client = retrieveClientGateway.execute(clientId);

    try {
      Subscription currentSubscription = transactionalHandlerGateway.execute(
          () -> persistCancelSuccess(client, subscription,
              cancelSubscriptionData.cancellationReason()));
      sendNotification.execute(client, currentSubscription.fundName(),
          currentSubscription.amount());
      return currentSubscription;
    } catch (BusinessRuleException e) {
      var transaction = Transaction.rejected(clientId, subscription.fundId(),
          subscription.fundName(), CANCELLATION, CREDIT, subscription.amount(), client.balance(),
          e.getMessage());

      createTransactionGateway.execute(transaction);
      throw e;
    }
  }

  private Subscription persistCancelSuccess(Client client, Subscription subscription,
      String cancellationReason) {
    SubscriptionStatus previousStatus = subscription.status();
    Subscription cancelledSubscription = subscription.cancel(cancellationReason);
    Client updatedClient = client.credit(subscription.amount());

    var transaction = Transaction.success(client.id(), subscription.fundId(),
        subscription.fundName(), subscription.id(), CANCELLATION, CREDIT, subscription.amount(),
        client.balance(), updatedClient.balance());

    cancelSubscriptionGateway.execute(cancelledSubscription, previousStatus);
    updateClientGateway.execute(updatedClient, client.version());
    createTransactionGateway.execute(transaction);

    return cancelledSubscription;
  }
}
