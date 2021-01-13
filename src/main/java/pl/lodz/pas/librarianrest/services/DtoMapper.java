package pl.lodz.pas.librarianrest.services;

import pl.lodz.pas.librarianrest.repository.books.data.Book;
import pl.lodz.pas.librarianrest.repository.books.data.Magazine;
import pl.lodz.pas.librarianrest.services.dto.BookDto;
import pl.lodz.pas.librarianrest.services.dto.MagazineDto;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class DtoMapper {

    MagazineDto map(Magazine magazine) {
        return new MagazineDto(
                magazine.getTitle(),
                magazine.getPublisher(),
                magazine.getIssn(),
                magazine.getIssue()
        );
    }

    Magazine map(MagazineDto magazine) {
        return new Magazine(
                magazine.getPublisher(),
                magazine.getTitle(),
                magazine.getIssn(),
                magazine.getIssue()
        );
    }

    BookDto map(Book book) {
        return new BookDto(book.getTitle(), book.getPublisher(), book.getIsbn(), book.getAuthor());
    }

    Book map(BookDto book) {
        return new Book(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher());
    }
}
