package pl.lodz.pas.librarianrest.controllers;

import pl.lodz.pas.librarianrest.services.UsersService;
import pl.lodz.pas.librarianrest.services.dto.NewUserDto;
import pl.lodz.pas.librarianrest.services.dto.UserDto;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@Named
@Path("")
public class UsersController {

    @Inject
    private UsersService service;

    @Context
    SecurityContext context;

    @GET
    @Path("/self")
    public Response getSelfUserInfo() {

        var principle = context.getUserPrincipal();

        if (principle == null) return Response.status(Response.Status.UNAUTHORIZED).build();

        var user = service.getUserByLogin(principle.getName());

        return Response.ok(user).build();
    }

    @GET
    @Path("/user")
    public List<UserDto> getUsers(@QueryParam("query") String query) {
        return service.getUsersByLoginContains(query);
    }

    @GET
    @Path("/user/{login}")
    public Optional<UserDto> getUser(@PathParam("login") String login) {
        return service.getUserByLogin(login);
    }

    @PUT
    @Path("/user/{login}")
    public Response updateUser(@PathParam("login") String login, @Valid NewUserDto newUser) {

        if (newUser.getLogin() != null && !newUser.getLogin().equals(login)) {

            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(new Message("Login cannot be changed!"))
                    .build();
        }

        newUser.setLogin(login);

        if (service.updateUserByLogin(newUser)) {
            return Response
                    .ok()
                    .build();
        }

        return Response
                .status(Response.Status.BAD_REQUEST)
                .build();
    }

    @POST
    @Path("/user")
    public Response addUser(@Valid NewUserDto newUser) {

        if (service.addUser(newUser)) {
            return Response
                    .ok()
                    .build();
        }

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new Message("User couldn't be added! Probably already exists."))
                .build();
    }

    @PATCH
    @Path("/users/activate")
    public Response activateUsers(@NotNull List<String> usersToActivate) {

        var i = service.updateUsersActive(usersToActivate, true);

        return Response.ok().entity(new Message("Updated objects: " + i)).build();
    }

    @PATCH
    @Path("/users/deactivate")
    public Response deactivateUsers(@NotNull List<String> usersToActivate) {
        var i = service.updateUsersActive(usersToActivate, false);

        return Response.ok().entity(new Message("Updated objects: " + i)).build();
    }
}
