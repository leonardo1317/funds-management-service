package io.github.leonardofrs.funds_service.infrastructure.mappers;

import io.github.leonardofrs.funds_service.infrastructure.controllers.contract.CreateFundRequest;
import io.github.leonardofrs.funds_service.domain.model.Fund;
import io.github.leonardofrs.funds_service.infrastructure.repository.mongodb.document.FundDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FundMapper {

  Fund toFund(CreateFundRequest createFundRequest);

  FundDocument toFundDocument(Fund fund);

  Fund toFund(FundDocument fundDocument);
}
