package com.johanwedin.urlshortener.resources;


import com.johanwedin.urlshortener.api.ErrorResponse;
import com.johanwedin.urlshortener.helpers.tracing.Tracing;
import com.johanwedin.urlshortener.models.sentinels.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ApplicationExceptionMapper implements ExceptionMapper<RuntimeException> {

    private final Logger logger = LoggerFactory.getLogger(ApplicationExceptionMapper.class);

    @Override
    public Response toResponse(RuntimeException e) {
        // Only ApplicationExceptions are considered safe to pass through to clients
        if (!(e instanceof ApplicationException)) {
            logger.error("encountered non ApplicationException", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new ErrorResponse("Internal Server Error", Tracing.getTraceId()))
                    .build();
        }
        return applicationExceptionMapping((ApplicationException) e);
    }

    private Response applicationExceptionMapping(ApplicationException e) {
        int statusCode = e.getResponse().getStatus();
        if (statusCode == 0) {
            logger.error("encountered unmapped error", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(new ErrorResponse("Internal Server Error", Tracing.getTraceId()))
                    .build();
        }
        if (e.getResponse().getStatus() >= 500) {
            logger.error("encountered severe error", e);
        }
        return Response.status(Response.Status.fromStatusCode(e.getResponse().getStatus()))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new ErrorResponse(e.getMessage(), Tracing.getTraceId()))
                .build();
    }

}
