package io.github.leonardofrs.funds_service.infrastructure.controllers;

import io.github.leonardofrs.funds_service.application.usecases.RetrieveTransactionHistory;
import io.github.leonardofrs.funds_service.domain.model.Transaction;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/clients")
public class TransactionController {

  private final RetrieveTransactionHistory retrieveTransactionHistory;

  public TransactionController(
      RetrieveTransactionHistory retrieveTransactionHistory
  ) {
    this.retrieveTransactionHistory = retrieveTransactionHistory;
  }

  @GetMapping("/{clientId}/transactions")
  public List<Transaction> history(@PathVariable UUID clientId) {
    return retrieveTransactionHistory.execute(clientId);
  }
}
