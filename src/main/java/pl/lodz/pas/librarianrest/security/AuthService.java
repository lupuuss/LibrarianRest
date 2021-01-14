package pl.lodz.pas.librarianrest.security;

import pl.lodz.pas.librarianrest.repository.user.User;
import pl.lodz.pas.librarianrest.repository.user.UsersRepository;
import pl.lodz.pas.librarianrest.security.objects.Credentials;
import pl.lodz.pas.librarianrest.services.DtoMapper;

import javax.inject.Inject;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.ws.rs.core.Response;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthService {


    @Inject
    @SuppressWarnings("CdiInjectionPointsInspection")
    private IdentityStoreHandler identity;

    @Inject
    private UsersRepository repository;

    @Inject
    private DtoMapper mapper;

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

    public String login(Credentials credentials) {

        var validationResult = identity.validate(credentials.toJaxRs());

        if (validationResult.getStatus() != CredentialValidationResult.Status.VALID) {
            return null;
        }

        var user = repository.findUserByLogin(credentials.getLogin());

        if (!user.map(User::isActive).orElse(false)) {
            return null;
        }

        return tokenProvider.createToken(
                validationResult.getCallerPrincipal().getName(),
                validationResult.getCallerGroups()
        );
    }
}
