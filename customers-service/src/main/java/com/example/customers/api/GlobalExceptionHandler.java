package com.example.customers.api;

import com.example.common.dto.ErrorResponse;
import com.example.common.exception.BusinessException;
import com.example.common.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNotFound(NotFoundException ex,
                                                              ServerWebExchange exchange) {
        return Mono.just(build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex, exchange));
    }

    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBusiness(BusinessException ex,
                                                              ServerWebExchange exchange) {
        return Mono.just(build(HttpStatus.BAD_REQUEST, "BUSINESS_ERROR", ex, exchange));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneric(Exception ex,
                                                             ServerWebExchange exchange) {
        log.error("Unexpected error", ex);
        return Mono.just(build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", ex, exchange));
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String code,
                                                Exception ex, ServerWebExchange exchange) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                code,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
        return ResponseEntity.status(status).body(body);
    }
}
