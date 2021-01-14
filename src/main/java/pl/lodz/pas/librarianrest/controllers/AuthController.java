package pl.lodz.pas.librarianrest.controllers;

import pl.lodz.pas.librarianrest.security.AuthService;
import pl.lodz.pas.librarianrest.security.objects.Credentials;
import pl.lodz.pas.librarianrest.services.UsersService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

    @Inject
    private UsersService service;

    @Inject
    private AuthService authService;

    @Path("login")
    @POST
    public Response login(@Valid Credentials credentials) {

        var token = authService.login(credentials);

        if (token == null) return Response.status(UNAUTHORIZED).build();

        return Response
                .ok(service.getUserByLogin(credentials.getLogin()))
                .header("Authorization", "Bearer " + token)
                .build();
    }

    @Path("token")
    @GET
    public Response token() {

        if (context.getUserPrincipal() == null) return Response.status(UNAUTHORIZED).build();

        var login = context.getUserPrincipal().getName();

        var newToken = authService.refreshToken(login);

        if (newToken == null) return Response.status(UNAUTHORIZED).build();

        return Response.ok().header("Authorization", "Bearer " + newToken).build();
    }
}
