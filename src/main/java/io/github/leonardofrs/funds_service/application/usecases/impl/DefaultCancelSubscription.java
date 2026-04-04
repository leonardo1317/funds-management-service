package io.github.leonardofrs.funds_service.application.usecases.impl;

import static io.github.leonardofrs.funds_service.domain.constants.MovementType.CREDIT;
import static io.github.leonardofrs.funds_service.domain.constants.TransactionType.CANCELLATION;
import static java.util.Objects.requireNonNull;

import io.github.leonardofrs.funds_service.application.usecases.CancelSubscription;
import io.github.leonardofrs.funds_service.domain.constants.SubscriptionStatus;
import io.github.leonardofrs.funds_service.domain.dto.CancelSubscriptionData;
import io.github.leonardofrs.funds_service.domain.exceptions.BusinessRuleException;
import io.github.leonardofrs.funds_service.domain.model.Client;
import io.github.leonardofrs.funds_service.domain.model.Subscription;
import io.github.leonardofrs.funds_service.domain.model.Transaction;
import io.github.leonardofrs.funds_service.domain.repository.CreateTransactionRepository;
import io.github.leonardofrs.funds_service.domain.repository.RetrieveClientRepository;
import io.github.leonardofrs.funds_service.domain.repository.RetrieveSubscriptionRepository;
import io.github.leonardofrs.funds_service.domain.repository.TransactionalHandler;
import io.github.leonardofrs.funds_service.domain.repository.UpdateClientRepository;
import io.github.leonardofrs.funds_service.domain.repository.CancelSubscriptionRepository;
import java.util.UUID;

public class DefaultCancelSubscription implements CancelSubscription {

  private final RetrieveSubscriptionRepository retrieveSubscriptionRepository;
  private final RetrieveClientRepository retrieveClientRepository;
  private final UpdateClientRepository updateClientRepository;
  private final CancelSubscriptionRepository cancelSubscriptionRepository;
  private final CreateTransactionRepository createTransactionRepository;
  private final TransactionalHandler transactionalHandler;

  public DefaultCancelSubscription(
      RetrieveSubscriptionRepository retrieveSubscriptionRepository,
      RetrieveClientRepository retrieveClientRepository,
      UpdateClientRepository updateClientRepository,
      CancelSubscriptionRepository cancelSubscriptionRepository,
      CreateTransactionRepository createTransactionRepository,
      TransactionalHandler transactionalHandler
  ) {
    this.retrieveSubscriptionRepository = retrieveSubscriptionRepository;
    this.retrieveClientRepository = retrieveClientRepository;
    this.updateClientRepository = updateClientRepository;
    this.cancelSubscriptionRepository = cancelSubscriptionRepository;
    this.createTransactionRepository = createTransactionRepository;
    this.transactionalHandler = transactionalHandler;
  }

  @Override
  public Subscription execute(UUID clientId, UUID subscriptionId,
      CancelSubscriptionData cancelSubscriptionData) {
    requireNonNull(cancelSubscriptionData);
    Subscription subscription = retrieveSubscriptionRepository.execute(clientId, subscriptionId);
    Client client = retrieveClientRepository.execute(clientId);

    try {
      return transactionalHandler.execute(() -> persistCancelSuccess(client, subscription,
          cancelSubscriptionData.cancellationReason()));

    } catch (BusinessRuleException e) {
      var transaction = Transaction.rejected(
          clientId,
          subscription.fundId(),
          subscription.fundName(),
          CANCELLATION,
          CREDIT,
          subscription.amount(),
          client.balance(),
          e.getMessage()
      );

      createTransactionRepository.execute(transaction);
      throw e;
    }
  }

  private Subscription persistCancelSuccess(Client client, Subscription subscription,
      String cancellationReason) {
    SubscriptionStatus previousStatus = subscription.status();
    Subscription cancelledSubscription = subscription.cancel(cancellationReason);
    Client updatedClient = client.credit(subscription.amount());

    var transaction = Transaction.success(
        client.id(),
        subscription.fundId(),
        subscription.fundName(),
        subscription.id(),
        CANCELLATION,
        CREDIT,
        subscription.amount(),
        client.balance(),
        updatedClient.balance()
    );

    cancelSubscriptionRepository.execute(cancelledSubscription, previousStatus);
    updateClientRepository.execute(updatedClient);
    createTransactionRepository.execute(transaction);

    return cancelledSubscription;
  }
}
