package pl.lodz.pas.librarianrest.security;

import pl.lodz.pas.librarianrest.repository.user.User;
import pl.lodz.pas.librarianrest.repository.user.UsersRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;

import static javax.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;
import static javax.security.enterprise.identitystore.CredentialValidationResult.NOT_VALIDATED_RESULT;

@RequestScoped
public class AuthenticationIdentityStore implements IdentityStore {

    @Inject
    private UsersRepository repository;

    @Override
    public CredentialValidationResult validate(Credential credential) {

        if (!(credential instanceof UsernamePasswordCredential)) {
            return NOT_VALIDATED_RESULT;
        }

        UsernamePasswordCredential usernamePassword = (UsernamePasswordCredential) credential;

        var user = repository.findUserByLogin(usernamePassword.getCaller());

        if (!user.map(User::isActive).orElse(false)) return INVALID_RESULT;

        var password = user.get().getPassword();


        if (password.equals(usernamePassword.getPasswordAsString())) {

            return new CredentialValidationResult(usernamePassword.getCaller());

        } else {

            return INVALID_RESULT;
        }
    }
}