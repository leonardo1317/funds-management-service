package io.github.leonardofrs.funds_service.domain.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

import io.github.leonardofrs.funds_service.domain.constants.FundStatus;
import io.github.leonardofrs.funds_service.domain.exceptions.MinimumAmountException;
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
class FundTest {

  private static final String VALID_NAME = "High Yield Fund";
  private static final String VALID_CATEGORY = "FIXED_INCOME";
  private static final BigDecimal MINIMUM_AMOUNT = new BigDecimal("1000");

  private Fund buildFund(BigDecimal minimumAmount) {
    return new Fund(
        UUID.randomUUID(),
        VALID_NAME,
        minimumAmount,
        VALID_CATEGORY,
        FundStatus.OPEN,
        Instant.now(),
        Instant.now()
    );
  }

  @Nested
  class ConstructorTest {

    @Test
    @DisplayName("should_initializeDefaults_when_optionalFieldsAreNull")
    void should_initializeDefaults_when_optionalFieldsAreNull() {
      Fund fund = new Fund(
          null,
          VALID_NAME,
          MINIMUM_AMOUNT,
          VALID_CATEGORY,
          null,
          null,
          null
      );

      assertThat(fund.id()).isNotNull();
      assertThat(fund.status()).isEqualTo(FundStatus.OPEN);
      assertThat(fund.createdAt()).isNotNull();
      assertThat(fund.updatedAt()).isNotNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("should_throwException_when_nameIsInvalid")
    void should_throwException_when_nameIsInvalid(String invalid) {
      assertThatThrownBy(() ->
          new Fund(null, invalid, MINIMUM_AMOUNT, VALID_CATEGORY, null, null, null)
      ).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("should_throwException_when_categoryIsInvalid")
    void should_throwException_when_categoryIsInvalid(String invalid) {
      assertThatThrownBy(() ->
          new Fund(null, VALID_NAME, MINIMUM_AMOUNT, invalid, null, null, null)
      ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("should_allowNullMinimumAmount_when_notValidatedInConstructor")
    void should_allowNullMinimumAmount_when_notValidatedInConstructor() {
      Fund fund = new Fund(
          null,
          VALID_NAME,
          null,
          VALID_CATEGORY,
          null,
          null,
          null
      );

      assertThat(fund.minimumAmount()).isNull();
    }
  }

  @Nested
  class ValidateAmountTest {

    @Test
    @DisplayName("should_notThrowException_when_amountIsGreaterThanMinimum")
    void should_notThrowException_when_amountIsGreaterThanMinimum() {
      Fund fund = buildFund(MINIMUM_AMOUNT);

      assertThatCode(() ->
          fund.validateAmount(new BigDecimal("1500"))
      ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should_notThrowException_when_amountEqualsMinimum")
    void should_notThrowException_when_amountEqualsMinimum() {
      Fund fund = buildFund(MINIMUM_AMOUNT);

      assertThatCode(() ->
          fund.validateAmount(MINIMUM_AMOUNT)
      ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should_throwMinimumAmountException_when_amountIsLessThanMinimum")
    void should_throwMinimumAmountException_when_amountIsLessThanMinimum() {
      Fund fund = buildFund(MINIMUM_AMOUNT);

      assertThatThrownBy(() ->
          fund.validateAmount(new BigDecimal("999"))
      ).isInstanceOf(MinimumAmountException.class)
          .hasMessageContaining(VALID_NAME);
    }

    @Test
    @DisplayName("should_throwMinimumAmountException_when_amountIsNull")
    void should_throwMinimumAmountException_when_amountIsNull() {
      Fund fund = buildFund(MINIMUM_AMOUNT);

      assertThatThrownBy(() ->
          fund.validateAmount(null)
      ).isInstanceOf(MinimumAmountException.class);
    }

    @Test
    @DisplayName("should_handleDifferentScalesCorrectly_when_comparingAmounts")
    void should_handleDifferentScalesCorrectly_when_comparingAmounts() {
      Fund fund = buildFund(new BigDecimal("1000.00"));

      assertThatCode(() ->
          fund.validateAmount(new BigDecimal("1000"))
      ).doesNotThrowAnyException();
    }
  }
}