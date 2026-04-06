package io.github.leonardofrs.funds_service.application.usecases;

import io.github.leonardofrs.funds_service.domain.models.Transaction;
import io.github.leonardofrs.funds_service.domain.vo.Page;
import io.github.leonardofrs.funds_service.domain.vo.PageResult;
import java.util.UUID;

public interface RetrieveTransactions {

  PageResult<Transaction> execute(UUID clientId, Page page);
}
