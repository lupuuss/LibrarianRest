package pl.lodz.pas.librarianrest.controllers;

import pl.lodz.pas.librarianrest.services.LendingsService;
import pl.lodz.pas.librarianrest.services.dto.LendEventDto;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
}
