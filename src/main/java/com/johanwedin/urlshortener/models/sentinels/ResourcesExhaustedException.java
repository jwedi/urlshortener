package com.johanwedin.urlshortener.models.sentinels;

public class ResourcesExhaustedException extends  ApplicationException{
    private static final String defaultMessage = "Resources Exhausted";
    private static final int statusCode = 503;
    public ResourcesExhaustedException() {
        super(defaultMessage, statusCode);
    }

    public ResourcesExhaustedException(String message) {
        super(message, statusCode);
    }

    public ResourcesExhaustedException(String message, Throwable cause) {
        super(message, cause, statusCode);
    }

    public ResourcesExhaustedException(Throwable cause) {
        super(defaultMessage, cause, statusCode);
    }
}
