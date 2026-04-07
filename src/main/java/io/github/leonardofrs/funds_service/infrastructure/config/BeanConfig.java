package io.github.leonardofrs.funds_service.infrastructure.config;

import io.github.leonardofrs.funds_service.application.usecases.CancelSubscription;
import io.github.leonardofrs.funds_service.application.usecases.CreateClient;
import io.github.leonardofrs.funds_service.application.usecases.CreateFund;
import io.github.leonardofrs.funds_service.application.usecases.CreateSubscription;
import io.github.leonardofrs.funds_service.application.usecases.IdempotencyHandler;
import io.github.leonardofrs.funds_service.application.usecases.RetrieveTransactions;
import io.github.leonardofrs.funds_service.application.usecases.SendNotification;
import io.github.leonardofrs.funds_service.application.usecases.impl.DefaultCancelSubscription;
import io.github.leonardofrs.funds_service.application.usecases.impl.DefaultCreateClient;
import io.github.leonardofrs.funds_service.application.usecases.impl.DefaultCreateFund;
import io.github.leonardofrs.funds_service.application.usecases.impl.DefaultCreateSubscription;
import io.github.leonardofrs.funds_service.application.usecases.impl.DefaultIdempotencyHandler;
import io.github.leonardofrs.funds_service.application.usecases.impl.DefaultRetrieveTransactions;
import io.github.leonardofrs.funds_service.application.usecases.impl.DefaultSendNotification;
import io.github.leonardofrs.funds_service.domain.gateway.Idempotency.CreateIdempotencyGateway;
import io.github.leonardofrs.funds_service.domain.gateway.Idempotency.RetrieveIdempotencyGateway;
import io.github.leonardofrs.funds_service.domain.gateway.Idempotency.UpdateIdempotencyGateway;
import io.github.leonardofrs.funds_service.domain.gateway.ObjectSerializerGateway;
import io.github.leonardofrs.funds_service.domain.gateway.subscription.CheckSubscriptionGateway;
import io.github.leonardofrs.funds_service.domain.gateway.transactions.CountTransactionsGateway;
import io.github.leonardofrs.funds_service.domain.gateway.client.CreateClientGateway;
import io.github.leonardofrs.funds_service.domain.gateway.fund.CreateFundGateway;
import io.github.leonardofrs.funds_service.domain.gateway.subscription.CreateSubscriptionGateway;
import io.github.leonardofrs.funds_service.domain.gateway.transactions.CreateTransactionGateway;
import io.github.leonardofrs.funds_service.domain.gateway.notification.SendNotificationGateway;
import io.github.leonardofrs.funds_service.domain.gateway.client.RetrieveClientGateway;
import io.github.leonardofrs.funds_service.domain.gateway.fund.RetrieveFundGateway;
import io.github.leonardofrs.funds_service.domain.gateway.subscription.RetrieveSubscriptionGateway;
import io.github.leonardofrs.funds_service.domain.gateway.transactions.RetrieveTransactionsGateway;
import io.github.leonardofrs.funds_service.domain.gateway.TransactionalHandlerGateway;
import io.github.leonardofrs.funds_service.domain.gateway.client.UpdateClientGateway;
import io.github.leonardofrs.funds_service.domain.gateway.subscription.CancelSubscriptionGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

  @Bean
  public CreateSubscription createSubscription(
      RetrieveClientGateway retrieveClientGateway,
      RetrieveFundGateway retrieveFundGateway,
      CheckSubscriptionGateway checkSubscriptionGateway,
      UpdateClientGateway updateClientGateway,
      CreateSubscriptionGateway createSubscriptionGateway,
      CreateTransactionGateway createTransactionGateway,
      TransactionalHandlerGateway transactionalHandlerGateway,
      SendNotification sendNotification
  ) {
    return new DefaultCreateSubscription(
        retrieveClientGateway,
        retrieveFundGateway,
        checkSubscriptionGateway,
        updateClientGateway,
        createSubscriptionGateway,
        createTransactionGateway,
        transactionalHandlerGateway,
        sendNotification
    );
  }

  @Bean
  public CancelSubscription cancelSubscription(
      RetrieveSubscriptionGateway retrieveSubscriptionGateway,
      RetrieveClientGateway retrieveClientGateway,
      UpdateClientGateway updateClientGateway,
      CancelSubscriptionGateway cancelSubscriptionGateway,
      CreateTransactionGateway createTransactionGateway,
      TransactionalHandlerGateway transactionalHandlerGateway,
      SendNotification sendNotification
  ) {
    return new DefaultCancelSubscription(
        retrieveSubscriptionGateway,
        retrieveClientGateway,
        updateClientGateway,
        cancelSubscriptionGateway,
        createTransactionGateway,
        transactionalHandlerGateway,
        sendNotification
    );
  }

  @Bean
  public RetrieveTransactions retrieveTransactionHistory(
      RetrieveTransactionsGateway retrieveTransactionsGateway,
      CountTransactionsGateway countTransactionsGateway) {
    return new DefaultRetrieveTransactions(retrieveTransactionsGateway,
        countTransactionsGateway);
  }

  @Bean
  public CreateClient createClient(CreateClientGateway createClientGateway) {
    return new DefaultCreateClient(createClientGateway);
  }

  @Bean
  public CreateFund createFund(CreateFundGateway createFundGateway) {
    return new DefaultCreateFund(createFundGateway);
  }

  @Bean
  public SendNotification sendNotification(SendNotificationGateway sendNotificationGateway) {
    return new DefaultSendNotification(sendNotificationGateway);
  }

  @Bean
  public IdempotencyHandler idempotencyHandler(CreateIdempotencyGateway createIdempotencyGateway,
      RetrieveIdempotencyGateway retrieveIdempotencyGateway,
      UpdateIdempotencyGateway updateIdempotencyGateway,
      ObjectSerializerGateway objectSerializerGateway) {
    return new DefaultIdempotencyHandler(createIdempotencyGateway, retrieveIdempotencyGateway,
        updateIdempotencyGateway, objectSerializerGateway);
  }
}
