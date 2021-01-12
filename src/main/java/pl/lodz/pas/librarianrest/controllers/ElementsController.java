package pl.lodz.pas.librarianrest.controllers;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Named
@Stateless
@Produces("text/html")
@Path("/element")
public class ElementsController {

    @GET
    public String getAllElements() {
        return "HelloWorld";
    }
}
