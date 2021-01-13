package pl.lodz.pas.librarianrest.services;

import pl.lodz.pas.librarianrest.repository.books.data.Book;
import pl.lodz.pas.librarianrest.repository.books.data.Magazine;
import pl.lodz.pas.librarianrest.repository.user.User;
import pl.lodz.pas.librarianrest.services.dto.BookDto;
import pl.lodz.pas.librarianrest.services.dto.MagazineDto;
import pl.lodz.pas.librarianrest.services.dto.UserDto;

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

    public UserDto map(User user) {
        return  new UserDto(
                user.getLogin(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                mapType(user.getType()),
                user.isActive()
        );
    }

    public User map(UserDto user) {
        return new User(
                user.getLogin(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                mapType(user.getType()), user.isActive()
        );
    }

    private User.Type mapType(UserDto.Type type) {
        return User.Type.valueOf(type.name());
    }

    private UserDto.Type mapType(User.Type type) {
        return UserDto.Type.valueOf(type.name());
    }

}
