package io.github.leonardofrs.funds_service.infrastructure.controllers;

import static io.github.leonardofrs.funds_service.infrastructure.controllers.constants.Services.CLIENT;

import io.github.leonardofrs.funds_service.application.usecases.CreateClient;
import io.github.leonardofrs.funds_service.infrastructure.idempotency.IdempotencyHandler;
import io.github.leonardofrs.funds_service.domain.models.Client;
import io.github.leonardofrs.funds_service.infrastructure.controllers.contract.ClientResponse;
import io.github.leonardofrs.funds_service.infrastructure.controllers.contract.CreateClientRequest;
import io.github.leonardofrs.funds_service.infrastructure.mappers.ClientMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/clients")
public class ClientController {

  private final CreateClient createClient;
  private final IdempotencyHandler idempotencyHandler;
  private final ClientMapper clientMapper;

  public ClientController(
      CreateClient createClient, IdempotencyHandler idempotencyHandler,
      ClientMapper clientMapper
  ) {
    this.createClient = createClient;
    this.idempotencyHandler = idempotencyHandler;
    this.clientMapper = clientMapper;
  }

  @PostMapping
  public ResponseEntity<ClientResponse> create(
      @RequestHeader("idempotency-Key") String idempotencyKey,
      @RequestBody CreateClientRequest createClientRequest) {
    Client client = clientMapper.toClient(createClientRequest);
    ClientResponse clientResponse = idempotencyHandler.execute(
        idempotencyKey,
        CLIENT.name(),
        () -> clientMapper.toClientResponse(createClient.execute(client)),
        ClientResponse.class
    );
    return ResponseEntity.ok(clientResponse);
  }

}
