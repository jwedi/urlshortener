package com.johanwedin.urlshortener.models.sentinels;

public class InternalException extends ApplicationException {
    private static final String defaultMessage = "Internal error";
    private static final int statusCode = 500;

    public InternalException() {
        super(defaultMessage, statusCode);
    }

    public InternalException(String message) {
        super(message, statusCode);
    }

    public InternalException(String message, Throwable cause) {
        super(message, cause, statusCode);
    }

    public InternalException(Throwable cause) {
        super(defaultMessage, cause, statusCode);
    }

}
