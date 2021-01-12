package pl.lodz.pas.librarianrest;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Named
@Stateless
@Path("/element")
public class ElementsController {

    @GET
    public String getAllElements() {
        return "HelloWorld";
    }
}
