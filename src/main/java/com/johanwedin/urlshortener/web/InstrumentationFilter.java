package com.johanwedin.urlshortener.web;


import com.google.inject.Inject;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
@WebFilter(urlPatterns = "/*")
public class InstrumentationFilter implements Filter {
    private static final String INSTRUMENTATION_NAME = InstrumentationFilter.class.getName();
    private Tracer tracer;

    @Inject
    public InstrumentationFilter(OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer(INSTRUMENTATION_NAME);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            Span span = tracer.spanBuilder("serverInterceptor").setNoParent().startSpan();
            try (Scope ss = span.makeCurrent()) {
                MDC.put("traceId", span.getSpanContext().getTraceId());
                chain.doFilter(request, response);
                return;
            } finally {
                span.end();
                MDC.remove("traceId");
            }
    }
}