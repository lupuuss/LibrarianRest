package pl.lodz.pas.librarianrest.controllers;

import pl.lodz.pas.librarianrest.controllers.objects.BookCopyRequest;
import pl.lodz.pas.librarianrest.controllers.objects.MagazineCopyRequest;
import pl.lodz.pas.librarianrest.services.LendingsService;
import pl.lodz.pas.librarianrest.services.UsersService;
import pl.lodz.pas.librarianrest.services.dto.ElementLockDto;
import pl.lodz.pas.librarianrest.services.exceptions.ObjectNoLongerAvailableException;
import pl.lodz.pas.librarianrest.services.exceptions.ObjectNotFoundException;
import pl.lodz.pas.librarianrest.services.exceptions.ServiceException;
import pl.lodz.pas.librarianrest.services.exceptions.UserInactiveException;

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
    public Response lendElements(List<String> ids) throws ServiceException {

        var principle = context.getUserPrincipal();

        if (principle == null) return Response.status(Response.Status.UNAUTHORIZED).build();


        lendingsService.lendCopiesByIds(principle.getName(), ids);

        return Response.ok().build();
    }

    @POST
    @Path("lock/book")
    public Response lockBook(@Valid BookCopyRequest request) throws ServiceException {

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
    public Response lockMagazine(@Valid MagazineCopyRequest request) throws ServiceException {

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
}
