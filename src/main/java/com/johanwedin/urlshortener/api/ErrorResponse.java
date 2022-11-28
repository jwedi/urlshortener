package com.johanwedin.urlshortener.api;

public class ErrorResponse extends ResponseBase {
    public ErrorResponse(String error, String traceId) {
        super(error, traceId);
    }
}
