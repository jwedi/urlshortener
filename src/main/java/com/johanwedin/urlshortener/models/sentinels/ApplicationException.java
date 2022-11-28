package com.johanwedin.urlshortener.models.sentinels;

import javax.ws.rs.WebApplicationException;

public class ApplicationException extends WebApplicationException {

    public ApplicationException(String message, int status) {
        super(message, status);
    }

    public ApplicationException(Throwable cause, int status) {
        super(cause, status);
    }

    public ApplicationException(String message, Throwable cause, int status) {
        super(message, cause, status);
    }

    public ApplicationException(int status) {
        super(status);
    }
}
