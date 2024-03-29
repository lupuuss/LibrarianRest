package pl.lodz.pas.librarianrest.web.controllers;

import pl.lodz.pas.librarianrest.Utils;
import pl.lodz.pas.librarianrest.web.controllers.objects.BookCopyRequest;
import pl.lodz.pas.librarianrest.web.controllers.objects.MagazineCopyRequest;
import pl.lodz.pas.librarianrest.services.LendingsService;
import pl.lodz.pas.librarianrest.services.UsersService;
import pl.lodz.pas.librarianrest.services.exceptions.ServiceException;

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

@Named
@Stateless
@Path("self")
public class SelfController {

    @Inject
    private UsersService usersService;

    @Inject
    private LendingsService lendingsService;

    @Context
    private SecurityContext context;

    @GET
    @Path("user")
    public Response getUser() {

        var principle = context.getUserPrincipal();

        if (principle == null) return Response.status(Response.Status.UNAUTHORIZED).build();

        var user = usersService.getUserByLogin(principle.getName());

        return Response.ok(user).build();
    }

    @GET
    @Path("lending")
    public Response getLendings() {
        var principle = context.getUserPrincipal();

        if (principle == null) return Response.status(Response.Status.UNAUTHORIZED).build();

        var lendings = lendingsService.getLendingsForUser(principle.getName());

        return Response
                .ok(lendings)
                .build();
    }

    @POST
    @Path("lending")
    public Response lendElements(@NotNull List<String> ids) throws ServiceException {

        var principle = context.getUserPrincipal();

        if (principle == null) return Response.status(Response.Status.UNAUTHORIZED).build();

        if (ids.contains(null) || !ids.stream().allMatch(Utils::isValidUuid)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        var lendings = lendingsService.lendCopiesByIds(principle.getName(), ids);

        return Response.ok(lendings).build();
    }

    @POST
    @Path("lock/book")
    public Response lockBook(@NotNull @Valid BookCopyRequest request) throws ServiceException {

        var principle = context.getUserPrincipal();

        if (principle == null) return Response.status(Response.Status.UNAUTHORIZED).build();

        var lock = lendingsService.lockBook(
                request.getIsbn(),
                principle.getName(),
                request.getState()
        );
        return Response.ok(lock).build();
    }

    @POST
    @Path("lock/magazine")
    public Response lockMagazine(@NotNull @Valid MagazineCopyRequest request) throws ServiceException {

        var principle = context.getUserPrincipal();

        if (principle == null) return Response.status(Response.Status.UNAUTHORIZED).build();

        var lock = lendingsService.lockMagazine(
                request.getIssn(),
                request.getIssue(),
                principle.getName(),
                request.getState()
        );

        return Response.ok(lock).build();
    }

    @DELETE
    @Path("lock/book/{id}")
    public Response unlockBook(@PathParam("id") String id) {

        return unlockElement(id);
    }

    @DELETE
    @Path("lock/magazine/{id}")
    public Response unlockMagazine(@PathParam("id") String id) {
        return unlockElement(id);
    }

    private Response unlockElement(String id) {

        var principle = context.getUserPrincipal();

        if (!Utils.isValidUuid(id)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (principle == null) return Response.status(Response.Status.UNAUTHORIZED).build();

        lendingsService.unlockCopy(principle.getName(), id);

        return Response.ok().build();
    }
}
