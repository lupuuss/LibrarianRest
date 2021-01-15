package pl.lodz.pas.librarianrest.services.exceptions;

public class OperationFailedException extends ServiceException {
    public OperationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
