package com.johanwedin.urlshortener.db.sentinels;

public class CollisionException extends DatabaseException{

    public CollisionException() {
        super();
    }

    public CollisionException(String message) {
        super(message);
    }

    public CollisionException(String message, Throwable cause) {
        super(message, cause);
    }

    public CollisionException(Throwable cause) {
        super(cause);
    }

    protected CollisionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
