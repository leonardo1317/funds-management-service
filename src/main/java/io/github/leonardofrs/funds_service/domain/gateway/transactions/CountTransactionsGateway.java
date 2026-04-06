package io.github.leonardofrs.funds_service.domain.gateway.transactions;

import java.util.UUID;

public interface CountTransactionsGateway {

  long execute(UUID clientId);
}
