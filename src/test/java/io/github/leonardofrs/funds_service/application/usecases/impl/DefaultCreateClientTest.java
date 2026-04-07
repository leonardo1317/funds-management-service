package io.github.leonardofrs.funds_service.application.usecases.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.leonardofrs.funds_service.domain.constants.ClientStatus;
import io.github.leonardofrs.funds_service.domain.gateway.client.CreateClientGateway;
import io.github.leonardofrs.funds_service.domain.models.Client;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultCreateClientTest {

  private static final UUID CLIENT_ID = UUID.randomUUID();

  @Mock
  private CreateClientGateway createClientGateway;

  @InjectMocks
  private DefaultCreateClient useCase;

  private Client client;

  @BeforeEach
  void setUp() {
    client = new Client(
        CLIENT_ID,
        "Leonardo",
        "leo@test.com",
        "+573001234567",
        List.of("email"),
        new BigDecimal("1000"),
        ClientStatus.ACTIVE,
        0L,
        Instant.now(),
        Instant.now()
    );
  }

  @Test
  @DisplayName("should_delegateToGatewayAndReturnCreatedClient_when_clientIsValid")
  void should_delegateToGatewayAndReturnCreatedClient_when_clientIsValid() {

    when(createClientGateway.execute(client)).thenReturn(client);

    Client result = useCase.execute(client);

    assertThat(result).isSameAs(client);

    verify(createClientGateway).execute(client);
  }

  @Test
  @DisplayName("should_notModifyClientInstance_when_executingUseCase")
  void should_notModifyClientInstance_when_executingUseCase() {

    when(createClientGateway.execute(client)).thenReturn(client);

    useCase.execute(client);

    assertThat(client.id()).isEqualTo(CLIENT_ID);
    assertThat(client.version()).isZero();
  }

  @Test
  @DisplayName("should_throwException_when_clientIsNull")
  void should_throwException_when_clientIsNull() {

    assertThatThrownBy(() -> useCase.execute(null))
        .isInstanceOf(NullPointerException.class);

    verifyNoInteractions(createClientGateway);
  }

  @Test
  @DisplayName("should_propagateException_when_gatewayThrowsException")
  void should_propagateException_when_gatewayThrowsException() {

    when(createClientGateway.execute(client))
        .thenThrow(new RuntimeException("db error"));

    assertThatThrownBy(() -> useCase.execute(client))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("db error");

    verify(createClientGateway).execute(client);
  }
}