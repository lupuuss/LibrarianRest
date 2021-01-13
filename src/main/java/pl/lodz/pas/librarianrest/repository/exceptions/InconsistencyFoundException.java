package pl.lodz.pas.librarianrest.repository.exceptions;

public class InconsistencyFoundException extends RepositoryException {
    public InconsistencyFoundException(String message) {
        super(message, null);
    }
}
