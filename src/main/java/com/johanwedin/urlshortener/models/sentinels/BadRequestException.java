package com.johanwedin.urlshortener.models.sentinels;

public class BadRequestException extends ApplicationException {
    private static final String defaultMessage = "Bad Request";
    private static final int statusCode = 400;

    public BadRequestException(String message) {
        super(message, statusCode);
    }

    public BadRequestException(Throwable cause) {
        super(defaultMessage, cause, statusCode);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause, statusCode);
    }

    public BadRequestException() {
        super(defaultMessage, statusCode);
    }
}
