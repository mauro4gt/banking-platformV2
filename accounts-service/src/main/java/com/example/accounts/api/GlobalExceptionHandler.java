package com.example.accounts.api;

import com.example.common.dto.ErrorResponse;
import com.example.common.exception.BusinessException;
import com.example.common.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNotFound(NotFoundException ex,
                                                              ServerWebExchange exchange) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                "NOT_FOUND",
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(body));
    }

    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBusiness(BusinessException ex,
                                                              ServerWebExchange exchange) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                "BUSINESS_ERROR",
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDataIntegrity(DataIntegrityViolationException ex,
                                                                   ServerWebExchange exchange) {
        log.error("Data integrity violation", ex);

        String message = "Data integrity violation";

        // Detectamos el constraint específico de número de cuenta único
        if (ex.getMostSpecificCause() != null &&
                ex.getMostSpecificCause().getMessage() != null &&
                ex.getMostSpecificCause().getMessage().contains("ukdbfiubqahb32ns85k023gr6nn")) {
            message = "Account number already exists";
        }

        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                "DATA_INTEGRITY_ERROR",
                message,
                exchange.getRequest().getPath().value()
        );
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(body));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleServerWebInput(ServerWebInputException ex,
                                                                    ServerWebExchange exchange) {
        log.error("Invalid request body", ex);
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                "INVALID_REQUEST",
                "Request body is invalid or malformed",
                exchange.getRequest().getPath().value()
        );
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneric(Exception ex,
                                                             ServerWebExchange exchange) {
        log.error("Unexpected error", ex);
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                "INTERNAL_ERROR",
                "Unexpected error",
                exchange.getRequest().getPath().value()
        );
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body));
    }
}
