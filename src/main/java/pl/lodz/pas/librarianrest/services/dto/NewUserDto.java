package pl.lodz.pas.librarianrest.services.dto;

import javax.validation.constraints.NotEmpty;

public class NewUserDto extends UserDto {

    @NotEmpty
    private String password;

    public NewUserDto() {

    }

    public NewUserDto(
            String login,
            String password,
            String firstName,
            String lastName,
            String email,
            Type type,
            boolean active
    ) {
        super(login, firstName, lastName, email, type, active);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
