package io.github.leonardofrs.funds_service.infrastructure.gateway.client;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.mongodb.client.result.UpdateResult;
import io.github.leonardofrs.funds_service.infrastructure.exceptions.OptimisticLockingException;
import io.github.leonardofrs.funds_service.domain.models.Client;
import io.github.leonardofrs.funds_service.domain.gateway.client.UpdateClientGateway;
import io.github.leonardofrs.funds_service.infrastructure.gateway.documents.ClientDocument;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
public class DefaultUpdateClientGateway implements UpdateClientGateway {

  private final MongoTemplate mongoTemplate;

  public DefaultUpdateClientGateway(
      MongoTemplate mongoTemplate
  ) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public Client execute(Client client) {
    var previousVersion = client.version() - 1;

    Query query = new Query(where("_id")
        .is(client.id())
        .and("version")
        .is(previousVersion));

    Update update = new Update()
        .set("balance", client.balance())
        .set("version", client.version())
        .set("updatedAt", client.updatedAt());

    UpdateResult result = mongoTemplate.updateFirst(query, update, ClientDocument.class);

    if (result.getModifiedCount() == 0) {
      throw new OptimisticLockingException(
          "client '" + client.id() + "' was modified by another request, please retry");
    }

    return client;
  }
}
