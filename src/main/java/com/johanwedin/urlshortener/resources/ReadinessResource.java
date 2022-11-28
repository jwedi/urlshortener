package com.johanwedin.urlshortener.resources;

import com.datastax.oss.driver.api.core.CqlSession;
import com.google.inject.Inject;
import com.johanwedin.urlshortener.models.UrlMapping;
import ru.vyarus.dropwizard.guice.module.installer.feature.web.AdminContext;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@AdminContext
public class ReadinessResource {

    private CqlSession session;
    @Inject
    public ReadinessResource(CqlSession session) {
        this.session = session;
    }

    @GET
    @Path("/ready")
    public Response get() {
        // TODO ping db
        return Response.ok().build();
    }
}
