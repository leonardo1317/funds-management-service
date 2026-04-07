package io.github.leonardofrs.funds_service.application.usecases.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.leonardofrs.funds_service.application.dto.CancelSubscriptionData;
import io.github.leonardofrs.funds_service.application.usecases.SendNotification;
import io.github.leonardofrs.funds_service.domain.constants.ClientStatus;
import io.github.leonardofrs.funds_service.domain.constants.MovementType;
import io.github.leonardofrs.funds_service.domain.constants.SubscriptionStatus;
import io.github.leonardofrs.funds_service.domain.constants.TransactionStatus;
import io.github.leonardofrs.funds_service.domain.exceptions.BusinessRuleException;
import io.github.leonardofrs.funds_service.domain.gateway.TransactionalHandlerGateway;
import io.github.leonardofrs.funds_service.domain.gateway.client.RetrieveClientGateway;
import io.github.leonardofrs.funds_service.domain.gateway.client.UpdateClientGateway;
import io.github.leonardofrs.funds_service.domain.gateway.subscription.CancelSubscriptionGateway;
import io.github.leonardofrs.funds_service.domain.gateway.subscription.RetrieveSubscriptionGateway;
import io.github.leonardofrs.funds_service.domain.gateway.transactions.CreateTransactionGateway;
import io.github.leonardofrs.funds_service.domain.models.Client;
import io.github.leonardofrs.funds_service.domain.models.Subscription;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultCancelSubscriptionTest {

  private static final UUID CLIENT_ID = UUID.randomUUID();
  private static final UUID SUBSCRIPTION_ID = UUID.randomUUID();
  private static final UUID FUND_ID = UUID.randomUUID();
  private static final String FUND_NAME = "FPV_TEST";
  private static final BigDecimal AMOUNT = new BigDecimal("1000");

  @Mock
  private RetrieveSubscriptionGateway retrieveSubscriptionGateway;

  @Mock
  private RetrieveClientGateway retrieveClientGateway;

  @Mock
  private UpdateClientGateway updateClientGateway;

  @Mock
  private CancelSubscriptionGateway cancelSubscriptionGateway;

  @Mock
  private CreateTransactionGateway createTransactionGateway;

  @Mock
  private TransactionalHandlerGateway transactionalHandlerGateway;

  @Mock
  private SendNotification sendNotification;

  @InjectMocks
  private DefaultCancelSubscription useCase;

  private Client client;
  private Subscription subscription;

  @BeforeEach
  void setUp() {
    client = new Client(
        CLIENT_ID,
        "Leonardo",
        "leo@test.com",
        "+573001234567",
        List.of("email"),
        new BigDecimal("5000"),
        ClientStatus.ACTIVE,
        0L,
        Instant.now(),
        Instant.now()
    );

    subscription = new Subscription(
        SUBSCRIPTION_ID,
        CLIENT_ID,
        FUND_ID,
        FUND_NAME,
        AMOUNT,
        SubscriptionStatus.ACTIVE,
        "",
        Instant.now(),
        Instant.now()
    );
  }

  @Test
  @DisplayName("should_cancelSubscriptionAndCreateTransaction_when_requestIsValid")
  void should_cancelSubscriptionAndCreateTransaction_when_requestIsValid() {

    CancelSubscriptionData request = new CancelSubscriptionData("user request");

    when(retrieveSubscriptionGateway.execute(CLIENT_ID, SUBSCRIPTION_ID))
        .thenReturn(subscription);
    when(retrieveClientGateway.execute(CLIENT_ID))
        .thenReturn(client);

    when(transactionalHandlerGateway.execute(any()))
        .thenAnswer(invocation -> {
          Supplier<?> supplier = invocation.getArgument(0);
          return supplier.get();
        });

    Subscription result = useCase.execute(CLIENT_ID, SUBSCRIPTION_ID, request);

    assertThat(result.status()).isEqualTo(SubscriptionStatus.CANCELLED);
    assertThat(result.cancellationReason()).isEqualTo("user request");

    verify(cancelSubscriptionGateway).execute(any(), eq(SubscriptionStatus.ACTIVE));
    verify(updateClientGateway).execute(any(), eq(client.version()));
    verify(createTransactionGateway).execute(argThat(tx ->
        tx.status() == TransactionStatus.SUCCESS &&
            tx.movementType() == MovementType.CREDIT
    ));
    verify(sendNotification).execute(client, FUND_NAME, AMOUNT);
  }

  @Test
  @DisplayName("should_createRejectedTransactionAndThrowException_when_subscriptionAlreadyCancelled")
  void should_createRejectedTransactionAndThrowException_when_subscriptionAlreadyCancelled() {

    Subscription cancelled = subscription.cancel("already");

    CancelSubscriptionData request = new CancelSubscriptionData("retry");

    when(retrieveSubscriptionGateway.execute(CLIENT_ID, SUBSCRIPTION_ID))
        .thenReturn(cancelled);
    when(retrieveClientGateway.execute(CLIENT_ID))
        .thenReturn(client);

    when(transactionalHandlerGateway.execute(any()))
        .thenAnswer(invocation -> {
          Supplier<?> supplier = invocation.getArgument(0);
          return supplier.get();
        });

    assertThatThrownBy(() -> useCase.execute(CLIENT_ID, SUBSCRIPTION_ID, request))
        .isInstanceOf(BusinessRuleException.class);

    verify(createTransactionGateway).execute(argThat(tx ->
        tx.status() == TransactionStatus.REJECTED &&
            tx.movementType() == MovementType.CREDIT
    ));

    verifyNoInteractions(sendNotification);
  }

  @Test
  @DisplayName("should_throwException_when_cancelSubscriptionDataIsNull")
  void should_throwException_when_cancelSubscriptionDataIsNull() {

    assertThatThrownBy(() ->
        useCase.execute(CLIENT_ID, SUBSCRIPTION_ID, null)
    ).isInstanceOf(NullPointerException.class);
  }

  @Test
  @DisplayName("should_executeWithinTransaction_when_cancelIsSuccessful")
  void should_executeWithinTransaction_when_cancelIsSuccessful() {

    CancelSubscriptionData request = new CancelSubscriptionData("ok");

    when(retrieveSubscriptionGateway.execute(CLIENT_ID, SUBSCRIPTION_ID))
        .thenReturn(subscription);
    when(retrieveClientGateway.execute(CLIENT_ID))
        .thenReturn(client);

    when(transactionalHandlerGateway.execute(any()))
        .thenAnswer(invocation -> {
          Supplier<?> supplier = invocation.getArgument(0);
          return supplier.get();
        });

    useCase.execute(CLIENT_ID, SUBSCRIPTION_ID, request);

    verify(transactionalHandlerGateway).execute(any());
  }
  
  @Test
  @DisplayName("should_increaseClientBalance_when_subscriptionIsCancelled")
  void should_increaseClientBalance_when_subscriptionIsCancelled() {

    CancelSubscriptionData request = new CancelSubscriptionData("ok");

    when(retrieveSubscriptionGateway.execute(CLIENT_ID, SUBSCRIPTION_ID))
        .thenReturn(subscription);
    when(retrieveClientGateway.execute(CLIENT_ID))
        .thenReturn(client);

    when(transactionalHandlerGateway.execute(any()))
        .thenAnswer(invocation -> {
          Supplier<?> supplier = invocation.getArgument(0);
          return supplier.get();
        });

    useCase.execute(CLIENT_ID, SUBSCRIPTION_ID, request);

    verify(updateClientGateway).execute(argThat(updated ->
        updated.balance().compareTo(client.balance().add(AMOUNT)) == 0
    ), eq(client.version()));
  }
}
