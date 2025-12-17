package org.LunaTelecom.http;

import io.javalin.http.HttpStatus;

import java.util.UUID;

public class ErrorResponse {
    public String message;
    public HttpStatus status;
    public String requestId;
    public String content;
    public ErrorResponse(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
        this.requestId = UUID.randomUUID().toString();
    }
    public ErrorResponse(String message, HttpStatus status, String content) {
        this.message = message;
        this.status = status;
        this.requestId = UUID.randomUUID().toString();
        this.content = content;
    }
}
