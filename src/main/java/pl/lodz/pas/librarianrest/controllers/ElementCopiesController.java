package pl.lodz.pas.librarianrest.controllers;

import pl.lodz.pas.librarianrest.controllers.objects.BookCopyRequest;
import pl.lodz.pas.librarianrest.controllers.objects.MagazineCopyRequest;
import pl.lodz.pas.librarianrest.services.ElementsService;
import pl.lodz.pas.librarianrest.services.dto.ElementCopyDto;
import pl.lodz.pas.librarianrest.services.dto.ElementDto;
import pl.lodz.pas.librarianrest.services.pages.Page;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Map;

@Stateless
@Named
@Path("copy")
public class ElementCopiesController {

    @Inject
    private ElementsService service;

    @GET
    public Page<ElementCopyDto> getAllCopies(
            @QueryParam("query") @DefaultValue("") String query,
            @QueryParam("pageSize") @DefaultValue("10") int pageSize,
            @QueryParam("pageNumber") @DefaultValue("0") int pageNumber
    ) {

        return service.getCopiesPageByIssnIsbnContains(query, pageSize, pageNumber);
    }

    @GET
    @Path("available")
    public Map<ElementDto, Long> getAvailableCopiesCount() {
        return service.getAvailableCopiesCount();
    }

    @POST
    @Path("book")
    public Response addBookCopy(@Valid BookCopyRequest bookRequest) {

        if (service.addBookCopy(bookRequest.getIsbn(), bookRequest.getState())) {
            return Response.ok().build();
        }

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new Message("Element copy could not be added! Maybe element not exists?"))
                .build();
    }

    @POST
    @Path("magazine")
    public Response addMagazineCopy(@Valid MagazineCopyRequest magazineRequest) {

        if (service.addMagazineCopy(magazineRequest.getIssn(), magazineRequest.getIssue(), magazineRequest.getState())) {
            return Response.ok().build();
        }

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new Message("Element copy could not be added! Maybe element not exists?"))
                .build();
    }

    @DELETE
    @Path("{isbn}/{number}")
    public Response deleteBook(@PathParam("isbn") String isbn, @PathParam("number") int number) {

        if (service.deleteBookCopy(isbn, number)) {
            return Response.ok().build();
        }
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new Message("This copy could not be removed! Maybe in use?"))
                .build();
    }

    @DELETE
    @Path("{issn}/{issue}/{number}")
    public Response deleteMagazine(
            @PathParam("issn") String issn,
            @PathParam("issue") int issue,
            @PathParam("number") int number
    ) {

        if (service.deleteMagazineCopy(issn, issue, number)) {
            return Response.ok().build();
        }
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new Message("This copy could not be removed! Maybe in use?"))
                .build();
    }

    @PATCH
    @Path("degrade/{isbn}/{number}")
    public Response patchBook(@PathParam("isbn") String isbn, @PathParam("number") int number) {

        if (service.degradeBookCopy(isbn, number)) {
            return Response.ok().build();
        }
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new Message("This copy could not be degraded! Probably not exists."))
                .build();
    }

    @PATCH
    @Path("degrade/{issn}/{issue}/{number}")
    public Response patchMagazine(
            @PathParam("issn") String issn,
            @PathParam("issue") int issue,
            @PathParam("number") int number
    ) {

        if (service.degradeMagazineCopy(issn, issue, number)) {
            return Response.ok().build();
        }

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new Message("This copy could not be degraded! Probably not exists."))
                .build();
    }
}
