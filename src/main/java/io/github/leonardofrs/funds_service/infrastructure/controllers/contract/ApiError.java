package io.github.leonardofrs.funds_service.infrastructure.controllers.contract;

public record ApiError(String title,
                       String message,
                       int status,
                       String path) {

}
