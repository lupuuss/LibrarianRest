package pl.lodz.pas.librarianrest.security;

import pl.lodz.pas.librarianrest.repository.user.User;
import pl.lodz.pas.librarianrest.repository.user.UsersRepository;

import javax.inject.Inject;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import java.util.Collections;
import java.util.Set;

import static javax.security.enterprise.identitystore.IdentityStore.ValidationType.PROVIDE_GROUPS;

public class AuthorizationIdentityStore implements IdentityStore {

    @Inject
    private UsersRepository repository;

    @Override
    public Set<String> getCallerGroups(CredentialValidationResult validationResult) {

        var login = validationResult.getCallerPrincipal().getName();

        return repository.findUserByLogin(login)
                .map(User::getType)
                .map(Enum::name)
                .map(Set::of)
                .orElse(Collections.emptySet());
    }

    @Override
    public Set<ValidationType> validationTypes() {
        return Set.of(PROVIDE_GROUPS);
    }
}
