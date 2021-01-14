package pl.lodz.pas.librarianrest.services.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Comparator;

public class BookDto extends ElementDto {

    @Pattern(regexp = "([a-zA-Z0-9]{8})", message = "Invalid isbn! Required 8 alphanumeric chars!")
    private String isbn;

    @NotBlank
    private String author;

    public BookDto(String title, String publisher, String isbn, String author) {
        super(title, publisher);
        this.isbn = isbn;
        this.author = author;
    }

    public BookDto() {
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public int compareTo(ElementDto o) {
        var superResult = super.compareTo(o);

        if (superResult != 0) {
            return superResult;
        }

        var comparator = Comparator.comparing(BookDto::getIsbn);

        return comparator.compare(this, (BookDto) o);
    }
}


