package io.github.leonardofrs.funds_service.domain.models;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import io.github.leonardofrs.funds_service.domain.constants.MovementType;
import io.github.leonardofrs.funds_service.domain.constants.TransactionStatus;
import io.github.leonardofrs.funds_service.domain.constants.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionTest {

  private static final UUID CLIENT_ID = UUID.randomUUID();
  private static final UUID FUND_ID = UUID.randomUUID();
  private static final UUID SUBSCRIPTION_ID = UUID.randomUUID();
  private static final String FUND_NAME = "Equity Fund";
  private static final BigDecimal VALID_AMOUNT = new BigDecimal("100");
  private static final BigDecimal BALANCE_BEFORE = new BigDecimal("1000");
  private static final BigDecimal BALANCE_AFTER = new BigDecimal("900");

  private Transaction buildTransaction(BigDecimal amount,
      BigDecimal before,
      BigDecimal after) {
    return new Transaction(
        UUID.randomUUID(),
        CLIENT_ID,
        FUND_ID,
        FUND_NAME,
        SUBSCRIPTION_ID,
        TransactionType.CANCELLATION,
        MovementType.DEBIT,
        amount,
        before,
        after,
        TransactionStatus.SUCCESS,
        null,
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
      Transaction transaction = new Transaction(
          null,
          CLIENT_ID,
          FUND_ID,
          FUND_NAME,
          null,
          TransactionType.CANCELLATION,
          MovementType.DEBIT,
          VALID_AMOUNT,
          BALANCE_BEFORE,
          BALANCE_AFTER,
          TransactionStatus.SUCCESS,
          null,
          null
      );

      assertThat(transaction.id()).isNotNull();
      assertThat(transaction.createdAt()).isNotNull();
    }

    @Test
    @DisplayName("should_throwException_when_amountIsNull")
    void should_throwException_when_amountIsNull() {
      assertThatThrownBy(() ->
          buildTransaction(null, BALANCE_BEFORE, BALANCE_AFTER)
      ).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("should_throwException_when_amountIsZeroOrNegative")
    void should_throwException_when_amountIsZeroOrNegative() {
      assertThatThrownBy(() ->
          buildTransaction(BigDecimal.ZERO, BALANCE_BEFORE, BALANCE_AFTER)
      ).isInstanceOf(IllegalArgumentException.class);

      assertThatThrownBy(() ->
          buildTransaction(new BigDecimal("-1"), BALANCE_BEFORE, BALANCE_AFTER)
      ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("should_throwException_when_balanceBeforeIsNegative")
    void should_throwException_when_balanceBeforeIsNegative() {
      assertThatThrownBy(() ->
          buildTransaction(VALID_AMOUNT, new BigDecimal("-1"), BALANCE_AFTER)
      ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("should_throwException_when_balanceAfterIsNegative")
    void should_throwException_when_balanceAfterIsNegative() {
      assertThatThrownBy(() ->
          buildTransaction(VALID_AMOUNT, BALANCE_BEFORE, new BigDecimal("-1"))
      ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("should_allowZeroBalance_when_balanceIsZero")
    void should_allowZeroBalance_when_balanceIsZero() {
      Transaction transaction = buildTransaction(
          VALID_AMOUNT,
          BigDecimal.ZERO,
          BigDecimal.ZERO
      );

      assertBigDecimalEquals(BigDecimal.ZERO, transaction.balanceBefore());
      assertBigDecimalEquals(BigDecimal.ZERO, transaction.balanceAfter());
    }

    @Test
    @DisplayName("should_throwException_when_requiredFieldsAreNull")
    void should_throwException_when_requiredFieldsAreNull() {
      assertThatThrownBy(() ->
          new Transaction(
              null,
              null,
              FUND_ID,
              FUND_NAME,
              null,
              TransactionType.CANCELLATION,
              MovementType.DEBIT,
              VALID_AMOUNT,
              BALANCE_BEFORE,
              BALANCE_AFTER,
              TransactionStatus.SUCCESS,
              null,
              null
          )
      ).isInstanceOf(NullPointerException.class);
    }
  }

  @Nested
  class SuccessFactoryTest {

    @Test
    @DisplayName("should_createSuccessTransaction_when_validDataProvided")
    void should_createSuccessTransaction_when_validDataProvided() {
      Transaction transaction = Transaction.success(
          CLIENT_ID,
          FUND_ID,
          FUND_NAME,
          SUBSCRIPTION_ID,
          TransactionType.CANCELLATION,
          MovementType.DEBIT,
          VALID_AMOUNT,
          BALANCE_BEFORE,
          BALANCE_AFTER
      );

      assertThat(transaction.status()).isEqualTo(TransactionStatus.SUCCESS);
      assertThat(transaction.errorMessage()).isNull();

      assertBigDecimalEquals(VALID_AMOUNT, transaction.amount());
      assertBigDecimalEquals(BALANCE_BEFORE, transaction.balanceBefore());
      assertBigDecimalEquals(BALANCE_AFTER, transaction.balanceAfter());
    }

    @Test
    @DisplayName("should_throwException_when_amountIsInvalid_inSuccessFactory")
    void should_throwException_when_amountIsInvalid_inSuccessFactory() {
      assertThatThrownBy(() ->
          Transaction.success(
              CLIENT_ID,
              FUND_ID,
              FUND_NAME,
              SUBSCRIPTION_ID,
              TransactionType.CANCELLATION,
              MovementType.DEBIT,
              BigDecimal.ZERO,
              BALANCE_BEFORE,
              BALANCE_AFTER
          )
      ).isInstanceOf(IllegalArgumentException.class);
    }
  }

  @Nested
  class RejectedFactoryTest {

    @Test
    @DisplayName("should_createRejectedTransaction_when_validDataProvided")
    void should_createRejectedTransaction_when_validDataProvided() {
      Transaction transaction = Transaction.rejected(
          CLIENT_ID,
          FUND_ID,
          FUND_NAME,
          TransactionType.CANCELLATION,
          MovementType.DEBIT,
          VALID_AMOUNT,
          BALANCE_BEFORE,
          "error"
      );

      assertThat(transaction.status()).isEqualTo(TransactionStatus.REJECTED);
      assertThat(transaction.errorMessage()).isEqualTo("error");

      assertBigDecimalEquals(BALANCE_BEFORE, transaction.balanceBefore());
      assertBigDecimalEquals(BALANCE_BEFORE, transaction.balanceAfter());
    }

    @Test
    @DisplayName("should_keepSameBalance_when_transactionIsRejected")
    void should_keepSameBalance_when_transactionIsRejected() {
      Transaction transaction = Transaction.rejected(
          CLIENT_ID,
          FUND_ID,
          FUND_NAME,
          TransactionType.CANCELLATION,
          MovementType.DEBIT,
          VALID_AMOUNT,
          BALANCE_BEFORE,
          "error"
      );

      assertBigDecimalEquals(transaction.balanceBefore(), transaction.balanceAfter());
    }
  }

  @Nested
  class ImmutabilityTest {

    @Test
    @DisplayName("should_beImmutable_when_transactionIsCreated")
    void should_beImmutable_when_transactionIsCreated() {
      Transaction transaction = buildTransaction(
          VALID_AMOUNT,
          BALANCE_BEFORE,
          BALANCE_AFTER
      );

      assertThat(transaction).isNotNull();
      assertThat(transaction.id()).isNotNull();
    }
  }
}