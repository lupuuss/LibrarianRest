package pl.lodz.pas.librarianrest.web.mapper;

import javax.json.bind.JsonbException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class JsonbExceptionMapper implements ExceptionMapper<ProcessingException> {
    @Override
    public Response toResponse(ProcessingException exception) {

        if (exception.getCause() instanceof JsonbException) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        throw exception;
    }
}
