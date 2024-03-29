package pl.lodz.pas.librarianrest.repository.user;

import pl.lodz.pas.librarianrest.producer.annotations.UsersRepositoryInitializer;
import pl.lodz.pas.librarianrest.repository.exceptions.ObjectAlreadyExistsException;
import pl.lodz.pas.librarianrest.repository.exceptions.ObjectNotFoundException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@ApplicationScoped
public class LocalUsersRepository implements UsersRepository {

    @Inject
    @UsersRepositoryInitializer
    private Consumer<List<User>> usersInitializer;

    private final List<User> users;

    public LocalUsersRepository() {
        this.users = Collections.synchronizedList(new ArrayList<>());
    }

    @PostConstruct
    private void initializeUsers() {

        if (usersInitializer != null) {
            usersInitializer.accept(users);
        }
    }

    @Override
    public Optional<User> findUserByLogin(String login) {
        return users.stream()
                .filter(user -> user.getLogin().equals(login))
                .findFirst()
                .map(User::copy);
    }

    @Override
    public void addUser(User user) throws ObjectAlreadyExistsException {

        var inBaseUser = findUserByLogin(user.getLogin());

        if (inBaseUser.isPresent() &&
                inBaseUser.get().getLogin().equals(user.getLogin())) {
            throw new ObjectAlreadyExistsException(User.class.getSimpleName(), user.getLogin());
        }

        users.add(user.copy());
    }

    @Override
    public void updateUser(User updatedUser) throws ObjectNotFoundException {
        var inBaseUser = findUserByLogin(updatedUser.getLogin());

        if (inBaseUser.isEmpty()) {
            throw new ObjectNotFoundException(User.class.getSimpleName(), updatedUser.getLogin());
        }

        inBaseUser.ifPresent(user -> {
            users.removeIf(u -> u.getLogin().equals(user.getLogin()));
            users.add(updatedUser.copy());
        });
    }

    @Override
    public List<User> findAllUsers() {
        return users.stream()
                .map(User::copy)
                .sorted(Comparator.comparing(User::getLogin))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findUserByLoginContains(String query) {
        return users.stream()
                .map(User::copy)
                .filter(user -> user.getLogin().contains(query))
                .sorted(Comparator.comparing(User::getLogin))
                .collect(Collectors.toList());
    }
}
