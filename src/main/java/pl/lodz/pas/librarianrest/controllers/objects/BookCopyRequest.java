package pl.lodz.pas.librarianrest.controllers.objects;

import pl.lodz.pas.librarianrest.services.dto.ElementCopyDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class BookCopyRequest {

    @NotBlank
    private String isbn;

    @NotNull
    private ElementCopyDto.State state;

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public ElementCopyDto.State getState() {
        return state;
    }

    public void setState(ElementCopyDto.State state) {
        this.state = state;
    }
}
