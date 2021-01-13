package pl.lodz.pas.librarianrest.services.exceptions;

public class ServiceException extends Exception {

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
