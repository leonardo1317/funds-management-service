package io.github.leonardofrs.funds_service.infrastructure.controllers;

import io.github.leonardofrs.funds_service.application.usecases.RetrieveTransactions;
import io.github.leonardofrs.funds_service.domain.models.Transaction;
import io.github.leonardofrs.funds_service.domain.vo.Page;
import io.github.leonardofrs.funds_service.domain.vo.PageResult;
import io.github.leonardofrs.funds_service.infrastructure.controllers.contract.PageResponse;
import io.github.leonardofrs.funds_service.infrastructure.controllers.contract.TransactionResponse;
import io.github.leonardofrs.funds_service.infrastructure.mappers.TransactionMapper;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/transactions")
public class TransactionController {

  private final RetrieveTransactions retrieveTransactions;
  private final TransactionMapper transactionMapper;

  public TransactionController(
      RetrieveTransactions retrieveTransactions, TransactionMapper transactionMapper
  ) {
    this.retrieveTransactions = retrieveTransactions;
    this.transactionMapper = transactionMapper;
  }

  @GetMapping
  public ResponseEntity<PageResponse<TransactionResponse>> history(
      @RequestHeader("X-Client-Id") UUID clientId,
      @RequestParam(defaultValue = "0")  int offset,
      @RequestParam(defaultValue = "20") int limit) {
    PageResult<Transaction> pageResult = retrieveTransactions.execute(clientId,
        Page.of(offset, limit));
    List<TransactionResponse> transactionsResponse = transactionMapper.toTransactionsResponse(
        pageResult.items());
    return ResponseEntity.ok(
        PageResponse.of(transactionsResponse, offset, limit, pageResult.total()));
  }
}
