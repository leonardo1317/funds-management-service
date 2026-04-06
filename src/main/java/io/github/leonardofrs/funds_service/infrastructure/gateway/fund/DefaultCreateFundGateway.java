package io.github.leonardofrs.funds_service.infrastructure.gateway.fund;

import io.github.leonardofrs.funds_service.domain.models.Fund;
import io.github.leonardofrs.funds_service.domain.gateway.fund.CreateFundGateway;
import io.github.leonardofrs.funds_service.infrastructure.mappers.FundMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DefaultCreateFundGateway implements CreateFundGateway {

  private final MongoTemplate mongoTemplate;
  private final FundMapper mapper;

  public DefaultCreateFundGateway(
      MongoTemplate mongoTemplate,
      FundMapper mapper
  ) {
    this.mongoTemplate = mongoTemplate;
    this.mapper = mapper;
  }

  @Override
  public Fund execute(Fund fund) {
    var fundDocument = mapper.toFundDocument(fund);
    return mapper.toFund(mongoTemplate.insert(fundDocument));
  }
}
