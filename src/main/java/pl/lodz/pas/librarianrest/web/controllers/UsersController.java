package pl.lodz.pas.librarianrest.web.controllers;

import pl.lodz.pas.librarianrest.web.controllers.objects.MultipleOperationsResult;
import pl.lodz.pas.librarianrest.web.controllers.objects.Message;
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

@Stateless
@Named
@Path("")
public class UsersController {

    @Inject
    private UsersService service;

    @Context
    private SecurityContext context;

    @GET
    @Path("/user")
    public List<UserDto> getUsers(@QueryParam("query") String query) {
        return service.getUsersByLoginContains(query);
    }

    @GET
    @Path("/user/{login}")
    public Response getUser(@PathParam("login") String login) {
        var user = service.getUserByLogin(login);

        if (user.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(user.get()).build();
    }

    @PUT
    @Path("/user/{login}")
    public Response updateUser(@PathParam("login") String login, @NotNull @Valid NewUserDto newUser) {

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
    public Response addUser(@NotNull @Valid NewUserDto newUser) {

        if (service.addUser(newUser)) {
            return Response
                    .ok()
                    .build();
        }

        return Response
                .status(Response.Status.CONFLICT)
                .entity(new Message("User couldn't be added! Probably already exists."))
                .build();
    }

    @PATCH
    @Path("/users/activate")
    public Response activateUsers(@NotNull List<String> usersToActivate) {

        if (usersToActivate.contains(null)) return Response.status(Response.Status.BAD_REQUEST).build();

        var i = service.updateUsersActive(usersToActivate, true);

        return Response.ok().entity(new MultipleOperationsResult(i)).build();
    }

    @PATCH
    @Path("/users/deactivate")
    public Response deactivateUsers(@NotNull List<String> usersToDeactivate) {

        if (usersToDeactivate.contains(null)) return Response.status(Response.Status.BAD_REQUEST).build();

        var i = service.updateUsersActive(usersToDeactivate, false);

        return Response.ok().entity(new MultipleOperationsResult(i)).build();
    }
}
