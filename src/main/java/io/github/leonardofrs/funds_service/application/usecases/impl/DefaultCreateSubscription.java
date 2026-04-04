package io.github.leonardofrs.funds_service.application.usecases.impl;

import static io.github.leonardofrs.funds_service.domain.constants.MovementType.DEBIT;
import static io.github.leonardofrs.funds_service.domain.constants.TransactionType.SUBSCRIPTION;
import static java.util.Objects.requireNonNull;

import io.github.leonardofrs.funds_service.application.usecases.CreateSubscription;
import io.github.leonardofrs.funds_service.domain.dto.CreateSubscriptionData;
import io.github.leonardofrs.funds_service.domain.exceptions.BusinessRuleException;
import io.github.leonardofrs.funds_service.domain.model.Client;
import io.github.leonardofrs.funds_service.domain.model.Fund;
import io.github.leonardofrs.funds_service.domain.model.Subscription;
import io.github.leonardofrs.funds_service.domain.model.Transaction;
import io.github.leonardofrs.funds_service.domain.repository.CheckSubscriptionRepository;
import io.github.leonardofrs.funds_service.domain.repository.CreateSubscriptionRepository;
import io.github.leonardofrs.funds_service.domain.repository.CreateTransactionRepository;
import io.github.leonardofrs.funds_service.domain.repository.RetrieveClientRepository;
import io.github.leonardofrs.funds_service.domain.repository.RetrieveFundRepository;
import io.github.leonardofrs.funds_service.domain.exceptions.AlreadySubscribedException;
import io.github.leonardofrs.funds_service.domain.repository.TransactionalHandler;
import io.github.leonardofrs.funds_service.domain.repository.UpdateClientRepository;
import java.math.BigDecimal;
import java.util.UUID;

public class DefaultCreateSubscription implements CreateSubscription {

  private final RetrieveClientRepository retrieveClientRepository;
  private final RetrieveFundRepository retrieveFundRepository;
  private final CheckSubscriptionRepository checkSubscriptionRepository;
  private final UpdateClientRepository updateClientRepository;
  private final CreateSubscriptionRepository createSubscriptionRepository;
  private final CreateTransactionRepository createTransactionRepository;
  private final TransactionalHandler transactionalHandler;

  public DefaultCreateSubscription(
      RetrieveClientRepository retrieveClientRepository,
      RetrieveFundRepository retrieveFundRepository,
      CheckSubscriptionRepository checkSubscriptionRepository,
      UpdateClientRepository updateClientRepository,
      CreateSubscriptionRepository createSubscriptionRepository,
      CreateTransactionRepository createTransactionRepository,
      TransactionalHandler transactionalHandler
  ) {
    this.retrieveClientRepository = retrieveClientRepository;
    this.retrieveFundRepository = retrieveFundRepository;
    this.checkSubscriptionRepository = checkSubscriptionRepository;
    this.updateClientRepository = updateClientRepository;
    this.createSubscriptionRepository = createSubscriptionRepository;
    this.createTransactionRepository = createTransactionRepository;
    this.transactionalHandler = transactionalHandler;
  }

  @Override
  public Subscription execute(UUID clientId, CreateSubscriptionData createSubscriptionData) {
    requireNonNull(createSubscriptionData);
    Client client = retrieveClientRepository.execute(clientId);
    Fund fund = retrieveFundRepository.execute(createSubscriptionData.fundId());
    BigDecimal amount = createSubscriptionData.amount();

    try {
      fund.validateAmount(amount);
      if (checkSubscriptionRepository.execute(client.id(), fund.id())) {
        throw new AlreadySubscribedException(
            String.format("Client %s is already linked to fund %s", client.id(), fund.name())
        );
      }

      return transactionalHandler.execute(() -> persistSuccessSubscription(client, fund, amount));
    } catch (BusinessRuleException e) {
      var transaction = Transaction.rejected(
          client.id(),
          fund.id(),
          fund.name(),
          SUBSCRIPTION,
          DEBIT,
          amount,
          client.balance(),
          e.getMessage()
      );
      createTransactionRepository.execute(transaction);
      throw e;
    }
  }

  private Subscription persistSuccessSubscription(Client client, Fund fund, BigDecimal amount) {
    var updatedClient = client.debit(amount);
    var subscription = Subscription.subscribe(client.id(), fund.id(), fund.name(), amount);
    var transaction = Transaction.success(client.id(), fund.id(), fund.name(),
        subscription.id(), SUBSCRIPTION, DEBIT, amount, client.balance(), updatedClient.balance());

    createSubscriptionRepository.execute(subscription);
    updateClientRepository.execute(updatedClient);
    createTransactionRepository.execute(transaction);

    return subscription;
  }
}
