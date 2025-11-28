package com.example.common.dto;

import java.time.Instant;

public class ErrorResponse {

    private final Instant timestamp;
    private final String code;
    private final String message;
    private final String path;

    public ErrorResponse(Instant timestamp, String code, String message, String path) {
        this.timestamp = timestamp;
        this.code = code;
        this.message = message;
        this.path = path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}
