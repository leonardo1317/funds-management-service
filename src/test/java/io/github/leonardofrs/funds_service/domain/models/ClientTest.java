package io.github.leonardofrs.funds_service.domain.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.leonardofrs.funds_service.domain.constants.ClientStatus;
import io.github.leonardofrs.funds_service.domain.exceptions.InsufficientBalanceException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
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
class ClientTest {

  private static final String VALID_NAME = "Leonardo Romero";
  private static final String VALID_EMAIL = "leo@example.com";
  private static final String VALID_PHONE = "+573001234567";
  private static final BigDecimal INITIAL_BALANCE = new BigDecimal("1000");

  private Client buildClient(BigDecimal balance) {
    return new Client(
        UUID.randomUUID(),
        VALID_NAME,
        VALID_EMAIL,
        VALID_PHONE,
        List.of("email"),
        balance,
        ClientStatus.ACTIVE,
        0L,
        Instant.now(),
        Instant.now()
    );
  }

  private static void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual) {
    assertThat(actual).usingComparator(BigDecimal::compareTo).isEqualTo(expected);
  }

  @Nested
  class ConstructorTest {

    @Test
    @DisplayName("should_initializeDefaults_when_optionalFieldsAreNull")
    void should_initializeDefaults_when_optionalFieldsAreNull() {
      Client client = new Client(
          null,
          VALID_NAME,
          VALID_EMAIL,
          VALID_PHONE,
          null,
          null,
          null,
          null,
          null,
          null
      );

      assertThat(client.id()).isNotNull();
      assertBigDecimalEquals(new BigDecimal("500000"), client.balance());
      assertThat(client.status()).isEqualTo(ClientStatus.ACTIVE);
      assertThat(client.version()).isZero();
      assertThat(client.notificationChannels()).isEmpty();
      assertThat(client.createdAt()).isNotNull();
      assertThat(client.updatedAt()).isNotNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("should_throwException_when_fullNameIsInvalid")
    void should_throwException_when_fullNameIsInvalid(String invalid) {
      assertThatThrownBy(() ->
          new Client(null, invalid, VALID_EMAIL, VALID_PHONE, null, null, null, null, null, null)
      ).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("should_throwException_when_emailIsInvalid")
    void should_throwException_when_emailIsInvalid(String invalid) {
      assertThatThrownBy(() ->
          new Client(null, VALID_NAME, invalid, VALID_PHONE, null, null, null, null, null, null)
      ).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("should_throwException_when_phoneIsInvalid")
    void should_throwException_when_phoneIsInvalid(String invalid) {
      assertThatThrownBy(() ->
          new Client(null, VALID_NAME, VALID_EMAIL, invalid, null, null, null, null, null, null)
      ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("should_createDefensiveCopy_when_notificationChannelsProvided")
    void should_createDefensiveCopy_when_notificationChannelsProvided() {
      List<String> channels = List.of("email", "sms");

      Client client = new Client(
          null, VALID_NAME, VALID_EMAIL, VALID_PHONE,
          channels, null, null, null, null, null
      );

      assertThatThrownBy(() -> client.notificationChannels().add("push"))
          .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("should_throwException_when_balanceIsNegative")
    void should_throwException_when_balanceIsNegative() {
      assertThatThrownBy(() ->
          new Client(
              null,
              VALID_NAME,
              VALID_EMAIL,
              VALID_PHONE,
              null,
              new BigDecimal("-1"),
              null,
              null,
              null,
              null
          )
      ).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("balance");
    }

    @Test
    @DisplayName("should_allowZeroBalance_when_balanceIsZero")
    void should_allowZeroBalance_when_balanceIsZero() {
      Client client = buildClient(BigDecimal.ZERO);

      assertBigDecimalEquals(BigDecimal.ZERO, client.balance());
    }
  }

  @Nested
  class CreditTest {

    @Test
    @DisplayName("should_increaseBalanceAndVersion_when_creditIsValid")
    void should_increaseBalanceAndVersion_when_creditIsValid() {
      Client client = buildClient(INITIAL_BALANCE);

      Client result = client.credit(new BigDecimal("500"));

      assertBigDecimalEquals(new BigDecimal("1500"), result.balance());
      assertThat(result.version()).isEqualTo(client.version() + 1);

      assertThat(result).isNotSameAs(client);
      assertBigDecimalEquals(INITIAL_BALANCE, client.balance());

      assertThat(result.updatedAt()).isAfterOrEqualTo(client.createdAt());
    }

    @Test
    @DisplayName("should_throwException_when_creditAmountIsZeroOrNegative")
    void should_throwException_when_creditAmountIsZeroOrNegative() {
      Client client = buildClient(INITIAL_BALANCE);

      assertThatThrownBy(() -> client.credit(BigDecimal.ZERO))
          .isInstanceOf(IllegalArgumentException.class);

      assertThatThrownBy(() -> client.credit(new BigDecimal("-1")))
          .isInstanceOf(IllegalArgumentException.class);
    }
  }

  @Nested
  class DebitTest {

    @Test
    @DisplayName("should_decreaseBalanceAndIncreaseVersion_when_debitIsValid")
    void should_decreaseBalanceAndIncreaseVersion_when_debitIsValid() {
      Client client = buildClient(INITIAL_BALANCE);

      Client result = client.debit(new BigDecimal("400"));

      assertBigDecimalEquals(new BigDecimal("600"), result.balance());
      assertThat(result.version()).isEqualTo(client.version() + 1);

      assertThat(result).isNotSameAs(client);
      assertBigDecimalEquals(INITIAL_BALANCE, client.balance());

      assertThat(result.updatedAt()).isAfterOrEqualTo(client.createdAt());
    }

    @Test
    @DisplayName("should_throwInsufficientBalanceException_when_debitAmountIsGreaterThanBalance")
    void should_throwInsufficientBalanceException_when_debitAmountIsGreaterThanBalance() {
      Client client = buildClient(INITIAL_BALANCE);

      assertThatThrownBy(() -> client.debit(new BigDecimal("2000")))
          .isInstanceOf(InsufficientBalanceException.class);
    }

    @Test
    @DisplayName("should_allowDebit_when_amountEqualsBalance")
    void should_allowDebit_when_amountEqualsBalance() {
      Client client = buildClient(INITIAL_BALANCE);

      Client result = client.debit(INITIAL_BALANCE);

      assertBigDecimalEquals(BigDecimal.ZERO, result.balance());
    }

    @Test
    @DisplayName("should_throwException_when_debitAmountIsZeroOrNegative")
    void should_throwException_when_debitAmountIsZeroOrNegative() {
      Client client = buildClient(INITIAL_BALANCE);

      assertThatThrownBy(() -> client.debit(BigDecimal.ZERO))
          .isInstanceOf(IllegalArgumentException.class);

      assertThatThrownBy(() -> client.debit(new BigDecimal("-1")))
          .isInstanceOf(IllegalArgumentException.class);
    }
  }

  @Nested
  class ImmutabilityTest {

    @Test
    @DisplayName("should_notMutateOriginalInstance_when_operationsAreExecuted")
    void should_notMutateOriginalInstance_when_operationsAreExecuted() {
      Client client = buildClient(INITIAL_BALANCE);

      Client result = client.credit(new BigDecimal("100"));

      assertThat(client).isNotSameAs(result);
      assertBigDecimalEquals(INITIAL_BALANCE, client.balance());
    }
  }
}
