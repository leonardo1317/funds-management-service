package io.github.leonardofrs.funds_service.infrastructure.repository.mongodb;

import io.github.leonardofrs.funds_service.domain.exceptions.FundNotFoundException;
import io.github.leonardofrs.funds_service.domain.model.Fund;
import io.github.leonardofrs.funds_service.domain.repository.RetrieveFundRepository;
import io.github.leonardofrs.funds_service.infrastructure.mappers.FundMapper;
import io.github.leonardofrs.funds_service.infrastructure.repository.mongodb.document.FundDocument;
import java.util.UUID;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DefaultRetrieveFundRepository implements RetrieveFundRepository {

  private final MongoTemplate mongoTemplate;
  private final FundMapper fundMapper;

  public DefaultRetrieveFundRepository(MongoTemplate mongoTemplate, FundMapper fundMapper) {
    this.mongoTemplate = mongoTemplate;
    this.fundMapper = fundMapper;
  }

  @Override
  public Fund execute(UUID fundId) {

    var fundDocument = mongoTemplate.findById(fundId, FundDocument.class);

    if (fundDocument == null) {
      throw new FundNotFoundException(String.format("Fund not found %s", fundId));
    }

    return fundMapper.toFund(fundDocument);
  }
}
