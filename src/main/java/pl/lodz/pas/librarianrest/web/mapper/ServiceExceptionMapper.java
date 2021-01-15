package pl.lodz.pas.librarianrest.web.mapper;

import pl.lodz.pas.librarianrest.services.exceptions.*;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ServiceExceptionMapper implements ExceptionMapper<ServiceException> {

    @Override
    public Response toResponse(ServiceException ex) {

        if (ex instanceof UserInactiveException) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else if (ex instanceof ObjectNotFoundException) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else if (ex instanceof ObjectNoLongerAvailableException || ex instanceof ObjectAlreadyExistsException) {
            return Response.status(Response.Status.CONFLICT).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
