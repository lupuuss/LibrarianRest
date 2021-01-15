package pl.lodz.pas.librarianrest.services.exceptions;

public class ObjectNotFoundException extends ServiceException {
    public ObjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
