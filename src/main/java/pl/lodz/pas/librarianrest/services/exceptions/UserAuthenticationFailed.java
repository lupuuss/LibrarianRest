package pl.lodz.pas.librarianrest.services.exceptions;

public class UserAuthenticationFailed extends ServiceException {
    public UserAuthenticationFailed() {
        super("Passed login and password doesn't match!", null);
    }
}
