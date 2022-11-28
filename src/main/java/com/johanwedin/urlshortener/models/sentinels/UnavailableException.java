package com.johanwedin.urlshortener.models.sentinels;

public class UnavailableException extends ApplicationException {
    private static final String defaultMessage = "Unavailable";
    private static final int statusCode = 503;
    public UnavailableException() {
        super(defaultMessage, statusCode);
    }

    public UnavailableException(String message) {
        super(message, statusCode);
    }

    public UnavailableException(String message, Throwable cause) {
        super(message, cause, statusCode);
    }

    public UnavailableException(Throwable cause) {
        super(defaultMessage, cause, statusCode);
    }
}
