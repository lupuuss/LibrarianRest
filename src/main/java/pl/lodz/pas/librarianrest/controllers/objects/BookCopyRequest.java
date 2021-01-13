package pl.lodz.pas.librarianrest.controllers.objects;

import pl.lodz.pas.librarianrest.services.dto.ElementCopyDto;

public class BookCopyRequest {
    String isbn;
    ElementCopyDto.State state;

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
