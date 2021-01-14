package pl.lodz.pas.librarianrest.security.objects;

import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.validation.constraints.NotBlank;

public class Credentials {

    @NotBlank
    private String login;

    @NotBlank
    private String password;

    public Credentials() {

    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Credential toJaxRs() {
        return new UsernamePasswordCredential(login, password);
    }
}
