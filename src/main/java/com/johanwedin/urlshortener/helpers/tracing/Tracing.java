package com.johanwedin.urlshortener.helpers.tracing;

import io.opentelemetry.api.trace.Span;

public class Tracing {

    public static String getTraceId() {
        return Span.current().getSpanContext().getTraceId();
    }


}
