package io.github.leonardofrs.funds_service.infrastructure.controllers.contract;

import java.util.List;

public record CreateClientRequest(String fullName,
                                  String email,
                                  String phone,
                                  List<String> notificationChannels) {

}
