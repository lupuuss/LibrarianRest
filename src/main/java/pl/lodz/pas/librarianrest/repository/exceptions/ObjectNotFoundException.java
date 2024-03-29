package pl.lodz.pas.librarianrest.repository.exceptions;

public class ObjectNotFoundException extends RepositoryException {
    public ObjectNotFoundException(String type, String identifier) {
        super("Object of type '" + type + "' with identifier '" + identifier + "' doesn't exist!", null);
    }
}
