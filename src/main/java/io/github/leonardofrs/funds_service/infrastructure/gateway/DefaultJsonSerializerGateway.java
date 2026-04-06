package io.github.leonardofrs.funds_service.infrastructure.gateway;

import io.github.leonardofrs.funds_service.domain.gateway.JsonSerializerGateway;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class DefaultJsonSerializerGateway implements JsonSerializerGateway {

  private final ObjectMapper objectMapper;

  public DefaultJsonSerializerGateway(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public String serialize(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (Exception e) {
      throw new RuntimeException("Failed to serialize object: " + e.getMessage(), e);
    }
  }

  @Override
  public <T> T deserialize(String json, Class<T> type) {
    try {
      return objectMapper.readValue(json, type);
    } catch (Exception e) {
      throw new RuntimeException("Failed to deserialize JSON: " + e.getMessage(), e);
    }
  }
}
