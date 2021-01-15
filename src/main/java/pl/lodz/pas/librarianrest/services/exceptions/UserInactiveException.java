package pl.lodz.pas.librarianrest.services.exceptions;

public class UserInactiveException extends ServiceException {

    public UserInactiveException() {
        super("User is inactive and cannot perform any action!", null);
    }
}
