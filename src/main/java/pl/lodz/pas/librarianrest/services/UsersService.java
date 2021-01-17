package pl.lodz.pas.librarianrest.services;

import pl.lodz.pas.librarianrest.repository.exceptions.ObjectAlreadyExistsException;
import pl.lodz.pas.librarianrest.repository.exceptions.ObjectNotFoundException;
import pl.lodz.pas.librarianrest.repository.user.User;
import pl.lodz.pas.librarianrest.repository.user.UsersRepository;
import pl.lodz.pas.librarianrest.services.dto.NewUserDto;
import pl.lodz.pas.librarianrest.services.dto.UserDto;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestScoped
public class UsersService {

    @Inject
    private UsersRepository repository;

    @Inject
    private DtoMapper mapper;

    public List<UserDto> getAllUsers() {
        return repository.findAllUsers()
                .stream()
                .map(user -> mapper.map(user))
                .collect(Collectors.toList());
    }

    public boolean addUser(NewUserDto user) {

        var newUser = mapper.map(user);

        try {
            repository.addUser(newUser);
            return true;
        } catch (ObjectAlreadyExistsException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int updateUsersActive(List<String> toUpdate, boolean active) {

        int i = 0;

        for (var user : toUpdate) {
            var foundUser = repository.findUserByLogin(user);

            if (foundUser.isEmpty()) continue;

            var userToUpdate = foundUser.get();

            userToUpdate.setActive(active);

            try {
                repository.updateUser(userToUpdate);
                i++;
            } catch (ObjectNotFoundException e) {
                e.printStackTrace();
            }
        }

        return i;
    }

    public List<UserDto> getUsersByLoginContains(String query) {

        if (query == null || query.equals("")) {
            return getAllUsers();
        }

        return repository.findUserByLoginContains(query)
                .stream()
                .map(user -> mapper.map(user))
                .collect(Collectors.toList());
    }

    public Optional<UserDto> getUserByLogin(String loginToEdit) {
        return repository.findUserByLogin(loginToEdit)
                .map(user -> mapper.map(user));
    }

    public boolean updateUserByLogin(UserDto userDto) {

        var login = userDto.getLogin();

        var optUser = repository.findUserByLogin(login);

        if (optUser.isEmpty()) {
            return false;
        }

        var user = mapper.map(userDto, optUser.get().getPassword());

        try {
            repository.updateUser(user);
            return true;
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
