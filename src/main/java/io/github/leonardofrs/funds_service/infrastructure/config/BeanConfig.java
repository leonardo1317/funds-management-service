package io.github.leonardofrs.funds_service.infrastructure.config;

import io.github.leonardofrs.funds_service.application.usecases.CancelSubscription;
import io.github.leonardofrs.funds_service.application.usecases.CreateClient;
import io.github.leonardofrs.funds_service.application.usecases.CreateFund;
import io.github.leonardofrs.funds_service.application.usecases.CreateSubscription;
import io.github.leonardofrs.funds_service.application.usecases.RetrieveTransactionHistory;
import io.github.leonardofrs.funds_service.application.usecases.impl.DefaultCancelSubscription;
import io.github.leonardofrs.funds_service.application.usecases.impl.DefaultCreateClient;
import io.github.leonardofrs.funds_service.application.usecases.impl.DefaultCreateFund;
import io.github.leonardofrs.funds_service.application.usecases.impl.DefaultCreateSubscription;
import io.github.leonardofrs.funds_service.application.usecases.impl.DefaultRetrieveTransactionHistory;
import io.github.leonardofrs.funds_service.domain.repository.CheckSubscriptionRepository;
import io.github.leonardofrs.funds_service.domain.repository.CreateClientRepository;
import io.github.leonardofrs.funds_service.domain.repository.CreateFundRepository;
import io.github.leonardofrs.funds_service.domain.repository.CreateSubscriptionRepository;
import io.github.leonardofrs.funds_service.domain.repository.CreateTransactionRepository;
import io.github.leonardofrs.funds_service.domain.repository.RetrieveClientRepository;
import io.github.leonardofrs.funds_service.domain.repository.RetrieveFundRepository;
import io.github.leonardofrs.funds_service.domain.repository.RetrieveSubscriptionRepository;
import io.github.leonardofrs.funds_service.domain.repository.RetrieveTransactionHistoryRepository;
import io.github.leonardofrs.funds_service.domain.repository.TransactionalHandler;
import io.github.leonardofrs.funds_service.domain.repository.UpdateClientRepository;
import io.github.leonardofrs.funds_service.domain.repository.CancelSubscriptionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

@Configuration
public class BeanConfig {

  @Bean
  public CreateSubscription createSubscription(
      RetrieveClientRepository retrieveClientRepository,
      RetrieveFundRepository retrieveFundRepository,
      CheckSubscriptionRepository checkSubscriptionRepository,
      UpdateClientRepository updateClientRepository,
      CreateSubscriptionRepository createSubscriptionRepository,
      CreateTransactionRepository createTransactionRepository,
      TransactionalHandler transactionalHandler
  ) {
    return new DefaultCreateSubscription(
        retrieveClientRepository,
        retrieveFundRepository,
        checkSubscriptionRepository,
        updateClientRepository,
        createSubscriptionRepository,
        createTransactionRepository,
        transactionalHandler);
  }

  @Bean
  public CancelSubscription cancelSubscription(
      RetrieveSubscriptionRepository retrieveSubscriptionRepository,
      RetrieveClientRepository retrieveClientRepository,
      UpdateClientRepository updateClientRepository,
      CancelSubscriptionRepository cancelSubscriptionRepository,
      CreateTransactionRepository createTransactionRepository,
      TransactionalHandler transactionalHandler
  ) {
    return new DefaultCancelSubscription(
        retrieveSubscriptionRepository,
        retrieveClientRepository,
        updateClientRepository,
        cancelSubscriptionRepository,
        createTransactionRepository,
        transactionalHandler);
  }

  @Bean
  public RetrieveTransactionHistory retrieveTransactionHistory(
      RetrieveTransactionHistoryRepository retrieveTransactionHistoryRepository) {
    return new DefaultRetrieveTransactionHistory(retrieveTransactionHistoryRepository);
  }

  @Bean
  public CreateClient createClient(CreateClientRepository createClientRepository) {
    return new DefaultCreateClient(createClientRepository);
  }

  @Bean
  public CreateFund createFund(CreateFundRepository createFundRepository) {
    return new DefaultCreateFund(createFundRepository);
  }

  @Bean
  public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
    return new MongoTransactionManager(dbFactory);
  }
}
