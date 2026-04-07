package io.github.leonardofrs.funds_service.application.usecases.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.leonardofrs.funds_service.application.dto.CreateSubscriptionData;
import io.github.leonardofrs.funds_service.application.usecases.SendNotification;
import io.github.leonardofrs.funds_service.domain.constants.ClientStatus;
import io.github.leonardofrs.funds_service.domain.constants.FundStatus;
import io.github.leonardofrs.funds_service.domain.constants.TransactionStatus;
import io.github.leonardofrs.funds_service.domain.exceptions.AlreadySubscribedException;
import io.github.leonardofrs.funds_service.domain.exceptions.BusinessRuleException;
import io.github.leonardofrs.funds_service.domain.gateway.TransactionalHandlerGateway;
import io.github.leonardofrs.funds_service.domain.gateway.client.RetrieveClientGateway;
import io.github.leonardofrs.funds_service.domain.gateway.client.UpdateClientGateway;
import io.github.leonardofrs.funds_service.domain.gateway.fund.RetrieveFundGateway;
import io.github.leonardofrs.funds_service.domain.gateway.subscription.CheckSubscriptionGateway;
import io.github.leonardofrs.funds_service.domain.gateway.subscription.CreateSubscriptionGateway;
import io.github.leonardofrs.funds_service.domain.gateway.transactions.CreateTransactionGateway;
import io.github.leonardofrs.funds_service.domain.models.Client;
import io.github.leonardofrs.funds_service.domain.models.Fund;
import io.github.leonardofrs.funds_service.domain.models.Subscription;
import io.github.leonardofrs.funds_service.domain.models.Transaction;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultCreateSubscriptionTest {

  @Mock
  private RetrieveClientGateway retrieveClientGateway;

  @Mock
  private RetrieveFundGateway retrieveFundGateway;

  @Mock
  private CheckSubscriptionGateway checkSubscriptionGateway;

  @Mock
  private UpdateClientGateway updateClientGateway;

  @Mock
  private CreateSubscriptionGateway createSubscriptionGateway;

  @Mock
  private CreateTransactionGateway createTransactionGateway;

  @Mock
  private TransactionalHandlerGateway transactionalHandlerGateway;

  @Mock
  private SendNotification sendNotification;

  @InjectMocks
  private DefaultCreateSubscription useCase;

  private Client client;
  private Fund fund;
  private static final UUID CLIENT_ID = UUID.randomUUID();
  private static final UUID FUND_ID = UUID.randomUUID();
  private static final BigDecimal AMOUNT = new BigDecimal("100");

  @BeforeEach
  void setUp() {
    client = new Client(
        CLIENT_ID,
        "Leonardo Romero",
        "leo@example.com",
        "+573001234567",
        List.of("email"),
        new BigDecimal("1000"),
        ClientStatus.ACTIVE,
        0L,
        Instant.now(),
        Instant.now()
    );

    fund = new Fund(
        FUND_ID,
        "Equity Fund",
        new BigDecimal("50"),
        "EQUITY",
        FundStatus.OPEN,
        Instant.now(),
        Instant.now()
    );
  }

  private CreateSubscriptionData buildRequest() {
    return new CreateSubscriptionData(FUND_ID, AMOUNT);
  }

  @Nested
  class SuccessFlowTest {

    @Test
    @DisplayName("should_createSubscriptionAndPersistAll_when_requestIsValid")
    void should_createSubscriptionAndPersistAll_when_requestIsValid() {

      when(retrieveClientGateway.execute(CLIENT_ID)).thenReturn(client);
      when(retrieveFundGateway.execute(FUND_ID)).thenReturn(fund);
      when(checkSubscriptionGateway.execute(CLIENT_ID, FUND_ID)).thenReturn(false);

      when(transactionalHandlerGateway.execute(any()))
          .thenAnswer(invocation -> ((Supplier<?>) invocation.getArgument(0)).get());

      Subscription result = useCase.execute(CLIENT_ID, buildRequest());

      assertThat(result).isNotNull();

      verify(createSubscriptionGateway).execute(any(Subscription.class));
      verify(updateClientGateway).execute(any(Client.class), eq(client.version()));
      verify(createTransactionGateway).execute(any(Transaction.class));
      verify(sendNotification).execute(eq(client), eq(fund.name()), eq(AMOUNT));
    }
  }

  @Nested
  class BusinessRuleValidationTest {

    @Test
    @DisplayName("should_throwExceptionAndCreateRejectedTransaction_when_amountIsInvalid")
    void should_throwExceptionAndCreateRejectedTransaction_when_amountIsInvalid() {

      when(retrieveClientGateway.execute(CLIENT_ID)).thenReturn(client);
      when(retrieveFundGateway.execute(FUND_ID)).thenReturn(fund);

      CreateSubscriptionData request =
          new CreateSubscriptionData(FUND_ID, new BigDecimal("10"));

      assertThatThrownBy(() -> useCase.execute(CLIENT_ID, request))
          .isInstanceOf(BusinessRuleException.class);

      verify(createTransactionGateway).execute(argThat(transaction ->
          transaction.status() == TransactionStatus.REJECTED
      ));

      verifyNoInteractions(sendNotification);
    }

    @Test
    @DisplayName("should_throwExceptionAndCreateRejectedTransaction_when_clientAlreadySubscribed")
    void should_throwExceptionAndCreateRejectedTransaction_when_clientAlreadySubscribed() {

      when(retrieveClientGateway.execute(CLIENT_ID)).thenReturn(client);
      when(retrieveFundGateway.execute(FUND_ID)).thenReturn(fund);
      when(checkSubscriptionGateway.execute(CLIENT_ID, FUND_ID)).thenReturn(true);

      assertThatThrownBy(() -> useCase.execute(CLIENT_ID, buildRequest()))
          .isInstanceOf(AlreadySubscribedException.class);

      verify(createTransactionGateway).execute(argThat(tx ->
          tx.status() == TransactionStatus.REJECTED
      ));

      verifyNoInteractions(sendNotification);
    }
  }

  @Nested
  class TransactionalBehaviorTest {

    @Test
    @DisplayName("should_executeLogicInsideTransaction_when_validFlow")
    void should_executeLogicInsideTransaction_when_validFlow() {

      when(retrieveClientGateway.execute(CLIENT_ID)).thenReturn(client);
      when(retrieveFundGateway.execute(FUND_ID)).thenReturn(fund);
      when(checkSubscriptionGateway.execute(CLIENT_ID, FUND_ID)).thenReturn(false);

      when(transactionalHandlerGateway.execute(any()))
          .thenAnswer(invocation -> ((Supplier<?>) invocation.getArgument(0)).get());

      useCase.execute(CLIENT_ID, buildRequest());

      verify(transactionalHandlerGateway).execute(any());
    }
  }

  @Nested
  class DefensiveValidationTest {

    @Test
    @DisplayName("should_throwException_when_requestIsNull")
    void should_throwException_when_requestIsNull() {
      assertThatThrownBy(() -> useCase.execute(CLIENT_ID, null))
          .isInstanceOf(NullPointerException.class);
    }
  }
}