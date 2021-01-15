package pl.lodz.pas.librarianrest.services.exceptions;

public class ObjectAlreadyExistsException extends ServiceException {
    public ObjectAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
