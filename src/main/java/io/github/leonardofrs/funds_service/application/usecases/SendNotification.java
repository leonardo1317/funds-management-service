package io.github.leonardofrs.funds_service.application.usecases;

import io.github.leonardofrs.funds_service.domain.models.Client;
import io.github.leonardofrs.funds_service.domain.models.Fund;
import java.math.BigDecimal;

public interface SendNotification {

  void execute(Client client, Fund fund, BigDecimal amount);
}
