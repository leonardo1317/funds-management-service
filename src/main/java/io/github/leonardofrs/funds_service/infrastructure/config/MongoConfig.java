package io.github.leonardofrs.funds_service.infrastructure.config;

import com.mongodb.ConnectionString;
import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

@Configuration
public class MongoConfig {

  @Value("${spring.data.mongodb.uri}")
  String uri;

  @Bean
  public MongoClientSettingsBuilderCustomizer uuidCustomizer() {
    return builder -> builder
        .applyConnectionString(new ConnectionString(uri))
        .uuidRepresentation(UuidRepresentation.STANDARD);
  }

  @Bean
  public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
    return new MongoTransactionManager(dbFactory);
  }
}
