package pl.lodz.pas.librarianrest.controllers;

import pl.lodz.pas.librarianrest.services.ElementsService;
import pl.lodz.pas.librarianrest.services.dto.BookDto;
import pl.lodz.pas.librarianrest.services.dto.ElementDto;
import pl.lodz.pas.librarianrest.services.dto.MagazineDto;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Named
@Stateless
@Path("")
public class ElementsController {

    @Inject
    ElementsService service;

    @GET
    @Path("/element")
    public List<ElementDto> getAllElements() {
        return service.getAllElements();
    }

    @GET
    @Path("/magazine")
    @Produces("application/json")
    public List<MagazineDto> getAllMagazines() {
        return service.getAllMagazines();
    }

    @GET
    @Path("/book")
    public List<BookDto> getAllBooks() {
        return service.getAllBooks();
    }

    @GET
    @Path("/book/{isbn}")
    public Optional<BookDto> getBook(@PathParam("isbn") String isbn) {
        return service.getBook(isbn);
    }

    @GET
    @Path("/magazine/{issn}/{issue}")
    public Optional<MagazineDto> getMagazine(@PathParam("issn") String issn, @PathParam("issue") Integer issue) {
        return service.getMagazine(issn, issue);
    }

    @POST
    @Path("/book")
    public Response addBook(@Valid BookDto bookDto) {

        return addElement(bookDto);
    }

    @POST
    @Path("/magazine")
    public Response addMagazine(@Valid MagazineDto magazineDto) {

        return addElement(magazineDto);
    }

    private Response addElement(ElementDto newElement) {
        var ok = service.addElement(newElement);

        if (ok) {
            return Response.ok().build();
        } else {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new Message("Element could not be added! Probably object already exits."))
                    .build();
        }
    }

    @PUT
    @Path("/magazine/{issn}/{issue}")
    public Response updateMagazine(
            @PathParam("issn") String issn,
            @PathParam("issue") Integer issue,
            @Valid MagazineDto magazineDto
    ) {

        if (!magazineDto.getIssn().equals(issn)) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .build();
        }

        if (magazineDto.getIssue() != issue) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .build();
        }


        return updateElement(magazineDto);
    }

    @PUT
    @Path("/book/{isbn}")
    public Response updateBook(@PathParam("isbn") String isbn, @Valid BookDto bookDto) {

        if (!bookDto.getIsbn().equals(isbn)) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .build();
        }

        bookDto.setIsbn(isbn);

        return updateElement(bookDto);
    }

    private Response updateElement(ElementDto elementDto) {
        var ok = service.updateElement(elementDto);

        if (ok) {
            return Response.ok().build();
        } else {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new Message("Object couldn't be updated! Probably doesn't exits."))
                    .build();
        }
    }
}
