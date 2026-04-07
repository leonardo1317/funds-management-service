package io.github.leonardofrs.funds_service.application.usecases;

import io.github.leonardofrs.funds_service.domain.models.Client;
import java.math.BigDecimal;

public interface SendNotification {

  void execute(Client client, String fundName, BigDecimal amount);
}
