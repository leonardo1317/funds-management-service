package io.github.leonardofrs.funds_service.infrastructure.repository.mongodb;

import io.github.leonardofrs.funds_service.domain.model.Fund;
import io.github.leonardofrs.funds_service.domain.repository.CreateFundRepository;
import io.github.leonardofrs.funds_service.infrastructure.mappers.FundMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DefaultCreateFundRepository implements CreateFundRepository {

  private final MongoTemplate mongoTemplate;
  private final FundMapper fundMapper;

  public DefaultCreateFundRepository(
      MongoTemplate mongoTemplate,
      FundMapper fundMapper
  ) {
    this.mongoTemplate = mongoTemplate;
    this.fundMapper = fundMapper;
  }

  @Override
  public Fund execute(Fund fund) {
    var fundDocument = fundMapper.toFundDocument(fund);
    return fundMapper.toFund(mongoTemplate.save(fundDocument));
  }
}
