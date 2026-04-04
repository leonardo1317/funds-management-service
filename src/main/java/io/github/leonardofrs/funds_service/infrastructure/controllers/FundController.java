package io.github.leonardofrs.funds_service.infrastructure.controllers;

import io.github.leonardofrs.funds_service.application.usecases.CreateFund;
import io.github.leonardofrs.funds_service.infrastructure.controllers.contract.CreateFundRequest;
import io.github.leonardofrs.funds_service.domain.model.Fund;
import io.github.leonardofrs.funds_service.infrastructure.mappers.FundMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/funds")
public class FundController {

  private final CreateFund createFund;
  private final FundMapper fundMapper;

  public FundController(
      CreateFund createFund,
      FundMapper fundMapper
  ) {
    this.createFund = createFund;
    this.fundMapper = fundMapper;
  }

  @PostMapping
  public ResponseEntity<Fund> create(
      @RequestBody CreateFundRequest createFundRequest) {
    return ResponseEntity.ok(
        createFund.execute(fundMapper.toFund(createFundRequest)));
  }
}
