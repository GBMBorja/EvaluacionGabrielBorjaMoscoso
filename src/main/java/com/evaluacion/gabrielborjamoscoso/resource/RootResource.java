package com.evaluacion.gabrielborjamoscoso.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/")
public class RootResource {

    @GET
    public Response root() {
        return Response.seeOther(URI.create("/swagger-ui")).build();
    }

    @GET
    @Path("q/swagger-ui")
    public Response qSwaggerRedirect() {
        return Response.seeOther(URI.create("/swagger-ui")).build();
    }
}

