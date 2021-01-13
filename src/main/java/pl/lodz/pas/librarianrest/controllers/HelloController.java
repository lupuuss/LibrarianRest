package pl.lodz.pas.librarianrest.controllers;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Named
@Stateless
@Path("")
public class HelloController {


    public static class HelloObject {
        public String getMessage() {
            return "How do you do, fellow students?";
        }
    }

    @GET
    public HelloObject hello() {
        return new HelloObject();
    }
}
