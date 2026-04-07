package io.github.leonardofrs.funds_service.domain.gateway;

public interface ObjectSerializerGateway {

  String serialize(Object object);

  <T> T deserialize(String json, Class<T> type);
}
