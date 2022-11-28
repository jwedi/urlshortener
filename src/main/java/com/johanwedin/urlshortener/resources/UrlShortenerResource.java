package com.johanwedin.urlshortener.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.ResponseMetered;
import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import com.johanwedin.urlshortener.api.CreateShortenedUrlRequest;
import com.johanwedin.urlshortener.api.CreateShortenedUrlResponse;
import com.johanwedin.urlshortener.api.UrlMappingDTO;
import com.johanwedin.urlshortener.controller.UrlController;
import com.johanwedin.urlshortener.helpers.tracing.Tracing;
import com.johanwedin.urlshortener.models.UrlMapping;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class UrlShortenerResource {
    private static final String INSTRUMENTATION_NAME = UrlShortenerResource.class.getName();
    private Tracer tracer;
    @Inject
    private final UrlController urlController;

    @Inject
    public UrlShortenerResource(UrlController urlController, OpenTelemetry openTelemetry) {
        this.urlController = urlController;
        this.tracer = openTelemetry.getTracer(INSTRUMENTATION_NAME);
    }

    @GET
    @Timed
    public String ping() {
        return "pong";
    }

    @GET
    @Metered
    @ResponseMetered
    @ExceptionMetered
    @Path("/{urlId}")
    public Response get(@PathParam("urlId") String urlId) {
        UrlMapping urlMapping = this.urlController.getByUrlId(urlId);
        return Response.seeOther(URI.create(urlMapping.getOriginalUrl())).build();
    }

    @POST
    @Path("/url")
    @Metered
    @ResponseMetered
    @ExceptionMetered
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(CreateShortenedUrlRequest request) {
        UrlMapping urlMapping = this.urlController.createUrlMapping(request.getOriginalUrl());
        return Response.ok(new CreateShortenedUrlResponse(Tracing.getTraceId(), new UrlMappingDTO(urlMapping.getUrlId(), urlMapping.getOriginalUrl()))).build();
    }
}