package pl.lodz.pas.librarianrest.controllers;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@Named
@Stateless
@Path("auth")
public class AuthController {

    @Context
    private SecurityContext context;

    @Path("login")
    @GET
    public Response login() {


        var principal = context.getUserPrincipal();

        if (principal != null) {

            return Response.ok(principal).build();
        }
        return Response.status(UNAUTHORIZED).build();
    }
}
