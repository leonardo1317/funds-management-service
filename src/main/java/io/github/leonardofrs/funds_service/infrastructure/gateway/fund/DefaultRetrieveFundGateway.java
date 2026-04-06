package io.github.leonardofrs.funds_service.infrastructure.gateway.fund;

import io.github.leonardofrs.funds_service.domain.exceptions.FundNotFoundException;
import io.github.leonardofrs.funds_service.domain.models.Fund;
import io.github.leonardofrs.funds_service.domain.gateway.fund.RetrieveFundGateway;
import io.github.leonardofrs.funds_service.infrastructure.mappers.FundMapper;
import io.github.leonardofrs.funds_service.infrastructure.gateway.documents.FundDocument;
import java.util.UUID;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DefaultRetrieveFundGateway implements RetrieveFundGateway {

  private final MongoTemplate mongoTemplate;
  private final FundMapper mapper;

  public DefaultRetrieveFundGateway(MongoTemplate mongoTemplate, FundMapper mapper) {
    this.mongoTemplate = mongoTemplate;
    this.mapper = mapper;
  }

  @Override
  public Fund execute(UUID fundId) {

    var fundDocument = mongoTemplate.findById(fundId, FundDocument.class);

    if (fundDocument == null) {
      throw new FundNotFoundException(String.format("Fund not found %s", fundId));
    }

    return mapper.toFund(fundDocument);
  }
}
