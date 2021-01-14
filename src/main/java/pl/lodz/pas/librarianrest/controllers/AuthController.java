package pl.lodz.pas.librarianrest.controllers;

import pl.lodz.pas.librarianrest.security.TokenRefresh;
import pl.lodz.pas.librarianrest.services.UsersService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@Named
@Stateless
@Path("auth")
public class AuthController {

    @Context
    private SecurityContext context;

    @Inject
    private UsersService service;

    @Inject
    private TokenRefresh tokenRefresh;

    @Path("login")
    @GET
    public Response login() {

        var principal = context.getUserPrincipal();

        if (principal != null) {

            var login = principal.getName();

            return Response.ok(service.getUserByLogin(login)).build();
        }
        return Response.status(UNAUTHORIZED).build();
    }

    @Path("token")
    @GET
    public Response token() {

        if (context.getUserPrincipal() == null) return Response.status(UNAUTHORIZED).build();

        var login = context.getUserPrincipal().getName();

        var newToken = tokenRefresh.refreshToken(login);

        if (newToken == null) return Response.status(UNAUTHORIZED).build();

        return Response.ok().header("Authorization", "Bearer " + newToken).build();
    }
}
