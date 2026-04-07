package io.github.leonardofrs.funds_service.application.usecases.impl;

import static io.github.leonardofrs.funds_service.domain.constants.MovementType.DEBIT;
import static io.github.leonardofrs.funds_service.domain.constants.TransactionType.SUBSCRIPTION;
import static java.util.Objects.requireNonNull;

import io.github.leonardofrs.funds_service.application.usecases.CreateSubscription;
import io.github.leonardofrs.funds_service.application.dto.CreateSubscriptionData;
import io.github.leonardofrs.funds_service.application.usecases.SendNotification;
import io.github.leonardofrs.funds_service.domain.exceptions.BusinessRuleException;
import io.github.leonardofrs.funds_service.domain.models.Client;
import io.github.leonardofrs.funds_service.domain.models.Fund;
import io.github.leonardofrs.funds_service.domain.models.Subscription;
import io.github.leonardofrs.funds_service.domain.models.Transaction;
import io.github.leonardofrs.funds_service.domain.gateway.subscription.CheckSubscriptionGateway;
import io.github.leonardofrs.funds_service.domain.gateway.subscription.CreateSubscriptionGateway;
import io.github.leonardofrs.funds_service.domain.gateway.transactions.CreateTransactionGateway;
import io.github.leonardofrs.funds_service.domain.gateway.client.RetrieveClientGateway;
import io.github.leonardofrs.funds_service.domain.gateway.fund.RetrieveFundGateway;
import io.github.leonardofrs.funds_service.domain.exceptions.AlreadySubscribedException;
import io.github.leonardofrs.funds_service.domain.gateway.TransactionalHandlerGateway;
import io.github.leonardofrs.funds_service.domain.gateway.client.UpdateClientGateway;
import java.math.BigDecimal;
import java.util.UUID;

public class DefaultCreateSubscription implements CreateSubscription {

  private final RetrieveClientGateway retrieveClientGateway;
  private final RetrieveFundGateway retrieveFundGateway;
  private final CheckSubscriptionGateway checkSubscriptionGateway;
  private final UpdateClientGateway updateClientGateway;
  private final CreateSubscriptionGateway createSubscriptionGateway;
  private final CreateTransactionGateway createTransactionGateway;
  private final TransactionalHandlerGateway transactionalHandlerGateway;
  private final SendNotification sendNotification;

  public DefaultCreateSubscription(
      RetrieveClientGateway retrieveClientGateway,
      RetrieveFundGateway retrieveFundGateway,
      CheckSubscriptionGateway checkSubscriptionGateway,
      UpdateClientGateway updateClientGateway,
      CreateSubscriptionGateway createSubscriptionGateway,
      CreateTransactionGateway createTransactionGateway,
      TransactionalHandlerGateway transactionalHandlerGateway,
      SendNotification sendNotification
  ) {
    this.retrieveClientGateway = retrieveClientGateway;
    this.retrieveFundGateway = retrieveFundGateway;
    this.checkSubscriptionGateway = checkSubscriptionGateway;
    this.updateClientGateway = updateClientGateway;
    this.createSubscriptionGateway = createSubscriptionGateway;
    this.createTransactionGateway = createTransactionGateway;
    this.transactionalHandlerGateway = transactionalHandlerGateway;
    this.sendNotification = sendNotification;
  }

  @Override
  public Subscription execute(UUID clientId, CreateSubscriptionData createSubscriptionData) {
    requireNonNull(createSubscriptionData);
    Client client = retrieveClientGateway.execute(clientId);
    Fund fund = retrieveFundGateway.execute(createSubscriptionData.fundId());
    BigDecimal amount = createSubscriptionData.amount();

    try {
      fund.validateAmount(amount);
      if (checkSubscriptionGateway.execute(client.id(), fund.id())) {
        throw new AlreadySubscribedException(
            String.format("Client %s is already linked to fund %s", client.id(), fund.name())
        );
      }

      Subscription currentSubscription = transactionalHandlerGateway.execute(
          () -> persistSuccessSubscription(client, fund, amount)
      );

      sendNotification.execute(client, fund.name(), amount);
      return currentSubscription;
    } catch (BusinessRuleException e) {
      var transaction = Transaction.rejected(client.id(), fund.id(), fund.name(), SUBSCRIPTION,
          DEBIT, amount, client.balance(), e.getMessage()
      );
      createTransactionGateway.execute(transaction);
      throw e;
    }
  }

  private Subscription persistSuccessSubscription(Client client, Fund fund, BigDecimal amount) {
    var updatedClient = client.debit(amount);
    var subscription = Subscription.subscribe(client.id(), fund.id(), fund.name(), amount);
    var transaction = Transaction.success(client.id(), fund.id(), fund.name(),
        subscription.id(), SUBSCRIPTION, DEBIT, amount, client.balance(), updatedClient.balance());

    createSubscriptionGateway.execute(subscription);
    updateClientGateway.execute(updatedClient, client.version());
    createTransactionGateway.execute(transaction);

    return subscription;
  }

}
