package io.github.leonardofrs.funds_service.infrastructure.controllers;

import static io.github.leonardofrs.funds_service.infrastructure.controllers.constants.Services.FUND;

import io.github.leonardofrs.funds_service.application.usecases.CreateFund;
import io.github.leonardofrs.funds_service.infrastructure.idempotency.IdempotencyHandler;
import io.github.leonardofrs.funds_service.infrastructure.controllers.contract.CreateFundRequest;
import io.github.leonardofrs.funds_service.domain.models.Fund;
import io.github.leonardofrs.funds_service.infrastructure.controllers.contract.FundResponse;
import io.github.leonardofrs.funds_service.infrastructure.mappers.FundMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/funds")
public class FundController {

  private final CreateFund createFund;
  private final IdempotencyHandler idempotencyHandler;
  private final FundMapper fundMapper;

  public FundController(
      CreateFund createFund, IdempotencyHandler idempotencyHandler,
      FundMapper fundMapper
  ) {
    this.createFund = createFund;
    this.idempotencyHandler = idempotencyHandler;
    this.fundMapper = fundMapper;
  }

  @PostMapping
  public ResponseEntity<FundResponse> create(
      @RequestHeader("idempotency-Key") String idempotencyKey,
      @RequestBody CreateFundRequest createFundRequest) {
    Fund fund = fundMapper.toFund(createFundRequest);

    FundResponse fundResponse = idempotencyHandler.execute(
        idempotencyKey,
        FUND.name(),
        () -> fundMapper.toFundResponse(createFund.execute(fund)),
        FundResponse.class
    );

    return ResponseEntity.ok(fundResponse);
  }
}
