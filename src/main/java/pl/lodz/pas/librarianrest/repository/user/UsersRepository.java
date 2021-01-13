package pl.lodz.pas.librarianrest.repository.user;

import pl.lodz.pas.librarianrest.repository.exceptions.ObjectAlreadyExistsException;
import pl.lodz.pas.librarianrest.repository.exceptions.ObjectNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UsersRepository {

    Optional<User> findUserByLogin(String login);

    void addUser(User user) throws ObjectAlreadyExistsException;

    void updateUser(User user) throws ObjectNotFoundException;

    List<User> findAllUsers();

    List<User> findUserByLoginContains(String query);

}
