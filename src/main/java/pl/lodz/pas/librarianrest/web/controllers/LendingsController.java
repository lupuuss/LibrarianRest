package pl.lodz.pas.librarianrest.web.controllers;

import pl.lodz.pas.librarianrest.Utils;
import pl.lodz.pas.librarianrest.web.controllers.objects.MultipleOperationsResult;
import pl.lodz.pas.librarianrest.services.LendingsService;
import pl.lodz.pas.librarianrest.services.dto.LendEventDto;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

@Named
@Stateless
@Path("")
public class LendingsController {

    @Inject
    private LendingsService service;

    @GET
    @Path("lending")
    public List<LendEventDto> getAllLending() {

        return service.getAllLendings();
    }

    @POST
    @Path("lending/return")
    public Response returnLendings(@NotNull List<String> ids) {

        if (ids.contains(null) || !ids.stream().allMatch(Utils::isValidUuid)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }


        var count = service.returnLendings(ids);

        return Response.ok(new MultipleOperationsResult(count)).build();
    }

    @DELETE
    @Path("lending/{id}")
    public Response deleteNotReturnedLendings(@NotNull @PathParam("id") String id) {

        if (!Utils.isValidUuid(id)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        var i = service.removeNotReturnedLendings(Collections.singletonList(id));

        return Response.ok(new MultipleOperationsResult(i)).build();
    }
}
