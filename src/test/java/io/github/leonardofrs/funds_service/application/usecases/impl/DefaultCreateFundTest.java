package io.github.leonardofrs.funds_service.application.usecases.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.leonardofrs.funds_service.domain.constants.FundStatus;
import io.github.leonardofrs.funds_service.domain.gateway.fund.CreateFundGateway;
import io.github.leonardofrs.funds_service.domain.models.Fund;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultCreateFundTest {

  @Mock
  private CreateFundGateway createFundGateway;

  @InjectMocks
  private DefaultCreateFund useCase;

  private Fund fund;

  @BeforeEach
  void setUp() {
    fund = new Fund(
        UUID.randomUUID(),
        "FPV_TEST",
        new BigDecimal("100000"),
        "FUND_CATEGORY",
        FundStatus.OPEN,
        Instant.now(),
        Instant.now()
    );
  }

  @Test
  @DisplayName("should_delegateToGatewayAndReturnFund_when_fundIsValid")
  void should_delegateToGatewayAndReturnFund_when_fundIsValid() {

    when(createFundGateway.execute(fund)).thenReturn(fund);

    Fund result = useCase.execute(fund);

    assertThat(result).isSameAs(fund);
    verify(createFundGateway).execute(fund);
  }

  @Test
  @DisplayName("should_throwException_when_fundIsNull")
  void should_throwException_when_fundIsNull() {

    assertThatThrownBy(() -> useCase.execute(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("fund is required");

    verifyNoInteractions(createFundGateway);
  }

  @Test
  @DisplayName("should_notModifyFundInstance_when_executingUseCase")
  void should_notModifyFundInstance_when_executingUseCase() {

    when(createFundGateway.execute(fund)).thenReturn(fund);

    useCase.execute(fund);

    assertThat(fund.name()).isEqualTo("FPV_TEST");
    assertThat(fund.status()).isEqualTo(FundStatus.OPEN);
  }
  
  @Test
  @DisplayName("should_propagateException_when_gatewayFails")
  void should_propagateException_when_gatewayFails() {

    when(createFundGateway.execute(fund))
        .thenThrow(new RuntimeException("db error"));

    assertThatThrownBy(() -> useCase.execute(fund))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("db error");

    verify(createFundGateway).execute(fund);
  }
}