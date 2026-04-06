package io.github.leonardofrs.funds_service.infrastructure.config;

import org.bson.UuidRepresentation;
import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

@Configuration
public class MongoConfig {

  @Bean
  public MongoClientSettingsBuilderCustomizer uuidCustomizer() {
    return builder -> builder.uuidRepresentation(UuidRepresentation.STANDARD);
  }

  @Bean
  public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
    return new MongoTransactionManager(dbFactory);
  }
}
