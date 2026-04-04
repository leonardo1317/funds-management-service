package io.github.leonardofrs.funds_service.infrastructure.mappers;

import io.github.leonardofrs.funds_service.domain.model.Transaction;
import io.github.leonardofrs.funds_service.infrastructure.repository.mongodb.document.TransactionDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

  TransactionDocument toTransactionDocument(Transaction transaction);

  Transaction toTransaction(TransactionDocument transactionDocument);
}
