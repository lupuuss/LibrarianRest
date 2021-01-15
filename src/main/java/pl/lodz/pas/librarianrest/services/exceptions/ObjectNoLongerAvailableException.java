package pl.lodz.pas.librarianrest.services.exceptions;

public class ObjectNoLongerAvailableException extends ServiceException {
    public ObjectNoLongerAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
