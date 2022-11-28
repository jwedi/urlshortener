package com.johanwedin.urlshortener.models.sentinels;

public class NotFoundException extends ApplicationException{
    private static final String defaultMessage = "Not Found";
    private static final int statusCode = 404;

    public NotFoundException() {
        super(defaultMessage, statusCode);
    }

    public NotFoundException(String message) {
        super(message, statusCode);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause, statusCode);
    }

    public NotFoundException(Throwable cause) {
        super(defaultMessage, cause, statusCode);
    }
}
