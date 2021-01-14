package pl.lodz.pas.librarianrest.security;

import pl.lodz.pas.librarianrest.repository.user.UsersRepository;

import javax.inject.Inject;
import java.util.Set;

public class TokenRefresh {

    @Inject
    private UsersRepository repository;

    @Inject
    private TokenProvider tokenProvider;

    public String refreshToken(String login) {

        var userOpt = repository.findUserByLogin(login);

        if (userOpt.isEmpty()) return null;

        var user = userOpt.get();

        if (!user.isActive()) return null;

        var authorities = Set.of(user.getType().name());

        return tokenProvider.createToken(login, authorities);
    }
}
