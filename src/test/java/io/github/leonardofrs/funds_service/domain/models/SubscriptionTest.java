package io.github.leonardofrs.funds_service.domain.models;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import io.github.leonardofrs.funds_service.domain.constants.SubscriptionStatus;
import io.github.leonardofrs.funds_service.domain.exceptions.IllegalSubscriptionStateException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionTest {

  private static final UUID CLIENT_ID = UUID.randomUUID();
  private static final UUID FUND_ID = UUID.randomUUID();
  private static final String FUND_NAME = "Equity Fund";
  private static final BigDecimal VALID_AMOUNT = new BigDecimal("1000");

  private Subscription buildSubscription(SubscriptionStatus status) {
    return new Subscription(
        UUID.randomUUID(),
        CLIENT_ID,
        FUND_ID,
        FUND_NAME,
        VALID_AMOUNT,
        status,
        "",
        Instant.now(),
        Instant.now()
    );
  }

  @Nested
  class ConstructorTest {

    @Test
    @DisplayName("should_initializeDefaults_when_optionalFieldsAreNull")
    void should_initializeDefaults_when_optionalFieldsAreNull() {
      Subscription subscription = new Subscription(
          null,
          CLIENT_ID,
          FUND_ID,
          FUND_NAME,
          VALID_AMOUNT,
          null,
          null,
          null,
          null
      );

      assertThat(subscription.id()).isNotNull();
      assertThat(subscription.status()).isEqualTo(SubscriptionStatus.ACTIVE);
      assertThat(subscription.cancellationReason()).isEmpty();
      assertThat(subscription.createdAt()).isNotNull();
      assertThat(subscription.updatedAt()).isNotNull();
    }

    @Test
    @DisplayName("should_throwException_when_clientIdIsNull")
    void should_throwException_when_clientIdIsNull() {
      assertThatThrownBy(() ->
          new Subscription(null, null, FUND_ID, FUND_NAME, VALID_AMOUNT, null, null, null, null)
      ).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("should_throwException_when_fundIdIsNull")
    void should_throwException_when_fundIdIsNull() {
      assertThatThrownBy(() ->
          new Subscription(null, CLIENT_ID, null, FUND_NAME, VALID_AMOUNT, null, null, null, null)
      ).isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("should_throwException_when_fundNameIsInvalid")
    void should_throwException_when_fundNameIsInvalid(String invalid) {
      assertThatThrownBy(() ->
          new Subscription(null, CLIENT_ID, FUND_ID, invalid, VALID_AMOUNT, null, null, null, null)
      ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("should_throwException_when_amountIsNull")
    void should_throwException_when_amountIsNull() {
      assertThatThrownBy(() ->
          new Subscription(null, CLIENT_ID, FUND_ID, FUND_NAME, null, null, null, null, null)
      ).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("should_throwException_when_amountIsZeroOrNegative")
    void should_throwException_when_amountIsZeroOrNegative() {
      assertThatThrownBy(() ->
          new Subscription(null, CLIENT_ID, FUND_ID, FUND_NAME, BigDecimal.ZERO, null, null, null,
              null)
      ).isInstanceOf(IllegalArgumentException.class);

      assertThatThrownBy(() ->
          new Subscription(null, CLIENT_ID, FUND_ID, FUND_NAME, new BigDecimal("-1"), null, null,
              null, null)
      ).isInstanceOf(IllegalArgumentException.class);
    }
  }

  @Nested
  class SubscribeFactoryTest {

    @Test
    @DisplayName("should_createActiveSubscription_when_subscribeIsCalled")
    void should_createActiveSubscription_when_subscribeIsCalled() {
      Subscription subscription = Subscription.subscribe(
          CLIENT_ID,
          FUND_ID,
          FUND_NAME,
          VALID_AMOUNT
      );

      assertThat(subscription.id()).isNotNull();
      assertThat(subscription.status()).isEqualTo(SubscriptionStatus.ACTIVE);
      assertThat(subscription.cancellationReason()).isEmpty();
      assertThat(subscription.createdAt()).isNotNull();
      assertThat(subscription.updatedAt()).isNotNull();
    }

    @Test
    @DisplayName("should_throwException_when_amountIsInvalid")
    void should_throwException_when_amountIsInvalid() {
      assertThatThrownBy(() ->
          Subscription.subscribe(CLIENT_ID, FUND_ID, FUND_NAME, BigDecimal.ZERO)
      ).isInstanceOf(IllegalArgumentException.class);
    }
  }

  @Nested
  class CancelTest {

    @Test
    @DisplayName("should_cancelSubscription_when_statusIsActive")
    void should_cancelSubscription_when_statusIsActive() {
      Subscription subscription = buildSubscription(SubscriptionStatus.ACTIVE);

      Subscription result = subscription.cancel("user request");

      assertThat(result.status()).isEqualTo(SubscriptionStatus.CANCELLED);
      assertThat(result.cancellationReason()).isEqualTo("user request");

      assertThat(result).isNotSameAs(subscription);
      assertThat(subscription.status()).isEqualTo(SubscriptionStatus.ACTIVE);

      assertThat(result.updatedAt()).isAfterOrEqualTo(subscription.createdAt());
    }

    @Test
    @DisplayName("should_throwException_when_subscriptionIsAlreadyCancelled")
    void should_throwException_when_subscriptionIsAlreadyCancelled() {
      Subscription subscription = buildSubscription(SubscriptionStatus.CANCELLED);

      assertThatThrownBy(() ->
          subscription.cancel("any reason")
      ).isInstanceOf(IllegalSubscriptionStateException.class);
    }

    @Test
    @DisplayName("should_allowEmptyCancellationReason_when_cancelIsCalled")
    void should_allowEmptyCancellationReason_when_cancelIsCalled() {
      Subscription subscription = buildSubscription(SubscriptionStatus.ACTIVE);

      Subscription result = subscription.cancel("");

      assertThat(result.cancellationReason()).isEmpty();
    }

    @Test
    @DisplayName("should_allowNullCancellationReason_when_cancelIsCalled")
    void should_allowNullCancellationReason_when_cancelIsCalled() {
      Subscription subscription = buildSubscription(SubscriptionStatus.ACTIVE);

      Subscription result = subscription.cancel(null);

      assertThat(result.cancellationReason()).isEmpty();
    }
  }

  @Nested
  class ImmutabilityTest {

    @Test
    @DisplayName("should_notMutateOriginalInstance_when_cancelIsExecuted")
    void should_notMutateOriginalInstance_when_cancelIsExecuted() {
      Subscription subscription = buildSubscription(SubscriptionStatus.ACTIVE);

      Subscription result = subscription.cancel("reason");

      assertThat(subscription).isNotSameAs(result);
      assertThat(subscription.status()).isEqualTo(SubscriptionStatus.ACTIVE);
    }
  }
}
