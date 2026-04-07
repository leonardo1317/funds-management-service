package io.github.leonardofrs.funds_service.application.usecases.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import io.github.leonardofrs.funds_service.domain.constants.ClientStatus;
import io.github.leonardofrs.funds_service.domain.gateway.notification.SendNotificationGateway;
import io.github.leonardofrs.funds_service.domain.models.Client;
import io.github.leonardofrs.funds_service.domain.vo.Notification;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultSendNotificationTest {

  @Mock
  private SendNotificationGateway sendNotificationGateway;

  @InjectMocks
  private DefaultSendNotification useCase;

  private Client client;

  @BeforeEach
  void setUp() {
    client = new Client(
        UUID.randomUUID(),
        "Leonardo",
        "leo@test.com",
        "+573001234567",
        List.of("email", "sms"),
        new BigDecimal("1000"),
        ClientStatus.ACTIVE,
        0L,
        Instant.now(),
        Instant.now()
    );
  }

  @Test
  @DisplayName("should_sendNotificationForEachValidChannel")
  void should_sendNotificationForEachValidChannel() {

    useCase.execute(client, "FPV_TEST", new BigDecimal("1000"));

    verify(sendNotificationGateway, times(2)).execute(any(Notification.class));
  }

  @Test
  @DisplayName("should_buildCorrectNotificationBody")
  void should_buildCorrectNotificationBody() {

    ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);

    useCase.execute(client, "FPV_TEST", new BigDecimal("1000"));

    verify(sendNotificationGateway, atLeastOnce()).execute(captor.capture());

    Notification notification = captor.getValue();

   assertThat(notification.body())
        .contains("Leonardo")
        .contains("1000.00")
        .contains("FPV_TEST");
  }

  @Test
  @DisplayName("should_throwException_when_clientIsNull")
  void should_throwException_when_clientIsNull() {
    assertThatThrownBy(() -> useCase.execute(null, "FPV_TEST", new BigDecimal("1000")))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("client");
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"  "})
  void should_throwException_whenFundNameIsBlank(String invalidFundName) {
    assertThatThrownBy(() -> useCase.execute(client, invalidFundName, new BigDecimal("1000")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("fundName");
  }

  @Test
  @DisplayName("should_throwException_when_amountIsNull")
  void should_throwException_when_amountIsNull() {
    assertThatThrownBy(() -> useCase.execute(client, "FPV_TEST", null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("amount");
  }

  @Test
  @DisplayName("should_ignoreInvalidChannelAndContinue")
  void should_ignoreInvalidChannelAndContinue() {

    Client clientWithInvalidChannel = new Client(
        client.id(),
        client.fullName(),
        client.email(),
        client.phone(),
        List.of("email", "invalid_channel"),
        client.balance(),
        client.status(),
        client.version(),
        client.createdAt(),
        client.updatedAt()
    );

    useCase.execute(clientWithInvalidChannel, "FPV_TEST", new BigDecimal("1000"));

    verify(sendNotificationGateway, times(1)).execute(any(Notification.class));
  }

  @Test
  @DisplayName("should_continueSending_when_oneChannelFails")
  void should_continueSending_when_oneChannelFails() {

    doThrow(new RuntimeException("sms failed"))
        .when(sendNotificationGateway).execute(any(Notification.class));

    useCase.execute(client, "FPV_TEST", new BigDecimal("1000"));

    verify(sendNotificationGateway, times(2)).execute(any(Notification.class));
  }

  @Test
  @DisplayName("should_notSendNotification_when_noChannels")
  void should_notSendNotification_when_noChannels() {

    Client clientWithoutChannels = new Client(
        client.id(),
        client.fullName(),
        client.email(),
        client.phone(),
        List.of(),
        client.balance(),
        client.status(),
        client.version(),
        client.createdAt(),
        client.updatedAt()
    );

    useCase.execute(clientWithoutChannels, "FPV_TEST", new BigDecimal("1000"));

    verifyNoInteractions(sendNotificationGateway);
  }
}