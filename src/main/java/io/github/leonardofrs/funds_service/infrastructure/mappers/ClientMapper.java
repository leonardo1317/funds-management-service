package io.github.leonardofrs.funds_service.infrastructure.mappers;

import io.github.leonardofrs.funds_service.domain.models.Client;
import io.github.leonardofrs.funds_service.infrastructure.controllers.contract.ClientResponse;
import io.github.leonardofrs.funds_service.infrastructure.controllers.contract.CreateClientRequest;
import io.github.leonardofrs.funds_service.infrastructure.gateway.documents.ClientDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {

  Client toClient(CreateClientRequest createClientRequest);

  ClientDocument toClientDocument(Client client);

  Client toClient(ClientDocument clientDocument);

  ClientResponse toClientResponse(Client client);
}
