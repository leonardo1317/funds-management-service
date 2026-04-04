package io.github.leonardofrs.funds_service.infrastructure.controllers;

import io.github.leonardofrs.funds_service.application.usecases.CreateClient;
import io.github.leonardofrs.funds_service.domain.model.Client;
import io.github.leonardofrs.funds_service.infrastructure.controllers.contract.CreateClientRequest;
import io.github.leonardofrs.funds_service.infrastructure.mappers.ClientMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/clients")
public class ClientController {

  private final CreateClient createClient;
  private final ClientMapper clientMapper;

  public ClientController(
      CreateClient createClient,
      ClientMapper clientMapper
  ) {
    this.createClient = createClient;
    this.clientMapper = clientMapper;
  }

  @PostMapping
  public ResponseEntity<Client> create(
      @RequestBody CreateClientRequest createClientRequest) {
    return ResponseEntity.ok(
        createClient.execute(clientMapper.toClient(createClientRequest)));
  }

}
