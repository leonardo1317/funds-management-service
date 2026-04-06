package io.github.leonardofrs.funds_service.infrastructure.mappers;

import io.github.leonardofrs.funds_service.domain.models.Transaction;
import io.github.leonardofrs.funds_service.infrastructure.controllers.contract.TransactionResponse;
import io.github.leonardofrs.funds_service.infrastructure.gateway.documents.TransactionDocument;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

  TransactionDocument toTransactionDocument(Transaction transaction);

  Transaction toTransaction(TransactionDocument transactionDocument);

  List<Transaction> toTransactions(List<TransactionDocument> transactionDocuments);

  List<TransactionResponse> toTransactionsResponse(List<Transaction> transactions);
}
