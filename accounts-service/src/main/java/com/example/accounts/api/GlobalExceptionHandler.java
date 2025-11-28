package com.example.accounts.api;

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
