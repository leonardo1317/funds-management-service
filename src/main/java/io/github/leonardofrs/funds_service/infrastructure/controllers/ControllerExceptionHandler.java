package io.github.leonardofrs.funds_service.infrastructure.controllers;

import static java.util.Objects.requireNonNullElse;

import io.github.leonardofrs.funds_service.domain.exceptions.AlreadySubscribedException;
import io.github.leonardofrs.funds_service.domain.exceptions.BusinessRuleException;
import io.github.leonardofrs.funds_service.domain.exceptions.ClientNotFoundException;
import io.github.leonardofrs.funds_service.domain.exceptions.ConflictException;
import io.github.leonardofrs.funds_service.domain.exceptions.EmailAlreadyExistsException;
import io.github.leonardofrs.funds_service.domain.exceptions.FundNotFoundException;
import io.github.leonardofrs.funds_service.domain.exceptions.IdempotencyConsistencyException;
import io.github.leonardofrs.funds_service.domain.exceptions.IdempotencyInProgressException;
import io.github.leonardofrs.funds_service.domain.exceptions.IdempotencyInfrastructureException;
import io.github.leonardofrs.funds_service.domain.exceptions.IllegalSubscriptionStateException;
import io.github.leonardofrs.funds_service.domain.exceptions.InsufficientBalanceException;
import io.github.leonardofrs.funds_service.domain.exceptions.MinimumAmountException;
import io.github.leonardofrs.funds_service.domain.exceptions.PhoneAlreadyExistsException;
import io.github.leonardofrs.funds_service.domain.exceptions.SubscriptionNotFoundException;
import io.github.leonardofrs.funds_service.infrastructure.controllers.contract.ApiError;
import io.github.leonardofrs.funds_service.infrastructure.exceptions.OptimisticLockingException;
import io.github.leonardofrs.funds_service.infrastructure.exceptions.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

@RestControllerAdvice
public class ControllerExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);

  @ExceptionHandler(BusinessRuleException.class)
  public ResponseEntity<ApiError> handleBusinessRule(BusinessRuleException ex,
      HttpServletRequest request) {

    var error = buildError(
        "BUSINESS_RULE_VIOLATION",
        ex.getMessage(),
        HttpStatus.BAD_REQUEST,
        request.getRequestURI()
    );
    return ResponseEntity.status(error.status()).body(error);
  }

  @ExceptionHandler(MinimumAmountException.class)
  public ResponseEntity<ApiError> handleMinimumAmount(MinimumAmountException ex,
      HttpServletRequest request) {

    var error = buildError(
        "MINIMUM_AMOUNT_NOT_REACHED",
        ex.getMessage(),
        HttpStatus.BAD_REQUEST,
        request.getRequestURI()
    );
    return ResponseEntity.status(error.status()).body(error);
  }

  @ExceptionHandler(InsufficientBalanceException.class)
  public ResponseEntity<ApiError> handleInsufficientBalance(InsufficientBalanceException ex,
      HttpServletRequest request) {

    var error = buildError(
        "INSUFFICIENT_BALANCE",
        ex.getMessage(),
        HttpStatus.CONFLICT,
        request.getRequestURI()
    );

    return ResponseEntity.status(error.status()).body(error);
  }

  @ExceptionHandler(SubscriptionNotFoundException.class)
  public ResponseEntity<ApiError> handleSubscriptionNotFound(SubscriptionNotFoundException ex,
      HttpServletRequest request) {

    var error = buildError(
        "SUBSCRIPTION_NOT_FOUND",
        ex.getMessage(),
        HttpStatus.NOT_FOUND,
        request.getRequestURI()
    );
    return ResponseEntity.status(error.status()).body(error);
  }

  @ExceptionHandler(ClientNotFoundException.class)
  public ResponseEntity<ApiError> handleClientNotFound(ClientNotFoundException ex,
      HttpServletRequest request) {

    var error = buildError(
        "CLIENT_NOT_FOUND",
        ex.getMessage(),
        HttpStatus.NOT_FOUND,
        request.getRequestURI()
    );
    return ResponseEntity.status(error.status()).body(error);
  }

  @ExceptionHandler(FundNotFoundException.class)
  public ResponseEntity<ApiError> handleFundNotFound(FundNotFoundException ex,
      HttpServletRequest request) {

    var error = buildError(
        "FUND_NOT_FOUND",
        ex.getMessage(),
        HttpStatus.NOT_FOUND,
        request.getRequestURI()
    );
    return ResponseEntity.status(error.status()).body(error);
  }

  @ExceptionHandler(AlreadySubscribedException.class)
  public ResponseEntity<ApiError> handleAlreadySubscribed(AlreadySubscribedException ex,
      HttpServletRequest request) {

    var error = buildError(
        "ALREADY_SUBSCRIBED",
        ex.getMessage(),
        HttpStatus.CONFLICT,
        request.getRequestURI()
    );

    return ResponseEntity.status(error.status()).body(error);
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ApiError> handleConflict(ConflictException ex,
      HttpServletRequest request) {

    var error = buildError(
        "CONFLICT",
        ex.getMessage(),
        HttpStatus.CONFLICT,
        request.getRequestURI()
    );
    return ResponseEntity.status(error.status()).body(error);
  }

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<ApiError> handleEmailAlreadyExists(EmailAlreadyExistsException ex,
      HttpServletRequest request) {

    var error = buildError(
        "EMAIL_ALREADY_EXISTS",
        ex.getMessage(),
        HttpStatus.CONFLICT,
        request.getRequestURI()
    );
    return ResponseEntity.status(error.status()).body(error);
  }

  @ExceptionHandler(PhoneAlreadyExistsException.class)
  public ResponseEntity<ApiError> handlePhoneAlreadyExists(PhoneAlreadyExistsException ex,
      HttpServletRequest request) {

    var error = buildError(
        "PHONE_ALREADY_EXISTS",
        ex.getMessage(),
        HttpStatus.CONFLICT,
        request.getRequestURI()
    );
    return ResponseEntity.status(error.status()).body(error);
  }

  @ExceptionHandler(IllegalSubscriptionStateException.class)
  public ResponseEntity<ApiError> handleIllegalSubscriptionState(
      IllegalSubscriptionStateException ex,
      HttpServletRequest request) {

    var error = buildError(
        "ILLEGAL_SUBSCRIPTION_STATE",
        ex.getMessage(),
        HttpStatus.BAD_REQUEST,
        request.getRequestURI()
    );
    return ResponseEntity.status(error.status()).body(error);
  }

  @ExceptionHandler(OptimisticLockingException.class)
  public ResponseEntity<ApiError> handleOptimisticLocking(OptimisticLockingException ex,
      HttpServletRequest request) {

    var error = buildError(
        "OPTIMISTIC_LOCKING_FAILED",
        ex.getMessage(),
        HttpStatus.CONFLICT,
        request.getRequestURI()
    );
    return ResponseEntity.status(error.status()).body(error);
  }

  @ExceptionHandler(PersistenceException.class)
  public ResponseEntity<ApiError> handlePersistence(PersistenceException ex,
      HttpServletRequest request) {

    var error = buildError(
        "PERSISTENCE_ERROR",
        ex.getMessage(),
        HttpStatus.INTERNAL_SERVER_ERROR,
        request.getRequestURI()
    );
    return ResponseEntity.status(error.status()).body(error);
  }

  @ExceptionHandler(IdempotencyInProgressException.class)
  public ResponseEntity<ApiError> handleIdempotencyInProgress(IdempotencyInProgressException ex,
      HttpServletRequest request) {

    var error = buildError(
        "IDEMPOTENCY_IN_PROGRESS",
        "An operation with the same key is already being processed. Please wait.",
        HttpStatus.CONFLICT,
        request.getRequestURI()
    );

    return ResponseEntity.status(error.status()).body(error);
  }

  @ExceptionHandler(IdempotencyInfrastructureException.class)
  public ResponseEntity<ApiError> handleIdempotencyInfrastructure(
      IdempotencyInfrastructureException ex,
      HttpServletRequest request) {

    var error = buildError(
        "IDEMPOTENCY_INFRASTRUCTURE_ERROR",
        "Business operation succeeded but state confirmation failed. Please verify the transaction status before retrying.",
        HttpStatus.INTERNAL_SERVER_ERROR,
        request.getRequestURI()
    );

    return ResponseEntity.status(error.status()).body(error);
  }

  @ExceptionHandler(IdempotencyConsistencyException.class)
  public ResponseEntity<ApiError> handleIdempotencyConsistency(IdempotencyConsistencyException ex,
      HttpServletRequest request) {

    var error = buildError(
        "IDEMPOTENCY_CONSISTENCY_ERROR",
        "System encountered a data consistency issue",
        HttpStatus.INTERNAL_SERVER_ERROR,
        request.getRequestURI()
    );

    return ResponseEntity
        .status(error.status())
        .body(error);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ApiError> handleNotFound(NoSuchElementException ex,
      HttpServletRequest request) {

    var error = buildError(
        "RESOURCE_NOT_FOUND",
        ex.getMessage(),
        HttpStatus.NOT_FOUND,
        request.getRequestURI()
    );
    return ResponseEntity.status(error.status()).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex,
      HttpServletRequest request) {
    var mensaje = ex.getBindingResult().getFieldErrors().stream()
        .findFirst()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .orElse("INVALID_DATA");

    var error = buildError(
        "VALIDATION_ERROR",
        mensaje,
        HttpStatus.BAD_REQUEST,
        request.getRequestURI()
    );

    return ResponseEntity.status(error.status()).body(error);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex,
      HttpServletRequest request) {

    var error = buildError(
        "INVALID_PARAMETERS",
        ex.getMessage(),
        HttpStatus.BAD_REQUEST,
        request.getRequestURI()
    );

    return ResponseEntity.status(error.status()).body(error);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiError> handleJsonParseError(HttpMessageNotReadableException ex,
      HttpServletRequest request) {
    Throwable cause = ex.getCause();
    var title = "INVALID_JSON";
    var httpStatus = HttpStatus.BAD_REQUEST;
    if (cause instanceof UnrecognizedPropertyException unrecognized) {
      String field = unrecognized.getPropertyName();
      String mensaje = String.format("unrecognized property '%s' in the request", field);
      var error = buildError(title, mensaje, httpStatus,
          request.getRequestURI());
      return ResponseEntity.status(error.status()).body(error);
    }

    var error = buildError(title, "invalid JSON format", httpStatus,
        request.getRequestURI());
    return ResponseEntity.status(error.status()).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGeneralException(Exception ex, HttpServletRequest request) {
    var error = buildError(
        "INTERNAL_SERVER_ERROR",
        requireNonNullElse(ex.getMessage(), "an unexpected error occurred"),
        HttpStatus.INTERNAL_SERVER_ERROR,
        request.getRequestURI()
    );
    log.error(error.message(), ex);
    return ResponseEntity.status(error.status()).body(error);
  }

  private ApiError buildError(String title, String detail, HttpStatus status, String path) {
    return new ApiError(title, detail, status.value(), path);
  }
}
