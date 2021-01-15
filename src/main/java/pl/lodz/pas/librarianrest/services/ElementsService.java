package pl.lodz.pas.librarianrest.services;

import pl.lodz.pas.librarianrest.repository.books.BooksRepository;
import pl.lodz.pas.librarianrest.repository.books.MagazinesRepository;
import pl.lodz.pas.librarianrest.repository.books.data.Book;
import pl.lodz.pas.librarianrest.repository.books.data.BookCopy;
import pl.lodz.pas.librarianrest.repository.books.data.MagazineCopy;
import pl.lodz.pas.librarianrest.repository.events.EventsRepository;
import pl.lodz.pas.librarianrest.repository.exceptions.ObjectAlreadyExistsException;
import pl.lodz.pas.librarianrest.repository.exceptions.ObjectNotFoundException;
import pl.lodz.pas.librarianrest.repository.exceptions.RepositoryException;
import pl.lodz.pas.librarianrest.services.dto.BookDto;
import pl.lodz.pas.librarianrest.services.dto.ElementCopyDto;
import pl.lodz.pas.librarianrest.services.dto.ElementDto;
import pl.lodz.pas.librarianrest.services.dto.MagazineDto;
import pl.lodz.pas.librarianrest.services.pages.Page;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.lang.Math.min;

@RequestScoped
public class ElementsService {

    @Inject
    private DtoMapper mapper;

    @Inject
    private BooksRepository booksRepository;

    @Inject
    private MagazinesRepository magazinesRepository;

    @Inject
    private EventsRepository eventsRepository;

    public boolean addBookCopy(String isbn, ElementCopyDto.State state) {
        try {

            var number = booksRepository.getNextCopyNumberByIsbn(isbn);

            var book = booksRepository.findBookByIsbn(isbn);

            if (book.isEmpty()) {
                return false;
            }

            booksRepository.addBookCopy(new BookCopy(
                    book.get().getUuid(),
                    number,
                    StateUtils.mapState(state)
            ));

            return true;
        } catch (RepositoryException e) {

            e.printStackTrace();
            return false;
        }
    }

    public boolean addMagazineCopy(String issn, int issue, ElementCopyDto.State state) {
        try {

            var number = magazinesRepository.getNextCopyNumberByIssnAndIssue(issn,issue);

            var magazine = magazinesRepository.findMagazineByIssnAndIssue(issn,issue);

            if (magazine.isEmpty()) {
                return false;
            }

            magazinesRepository.addMagazineCopy(new MagazineCopy(
                    magazine.get().getUuid(),
                    number,
                    StateUtils.mapState(state)
            ));

            return true;
        } catch (RepositoryException e) {

            e.printStackTrace();
            return false;
        }

    }

    public List<MagazineDto> getAllMagazines() {
        return magazinesRepository.findAllMagazines()
                .stream()
                .map(magazine -> mapper.map(magazine))
                .collect(Collectors.toList());
    }

    public List<BookDto> getAllBooks() {
        return booksRepository.findAllBooks()
                .stream()
                .map(book -> mapper.map(book))
                .collect(Collectors.toList());
    }

    public boolean degradeBookCopy(String isbn, int number) {

        var toUpdate = booksRepository.findBookCopyByIsbnAndNumber(
                isbn, number
        );

        if (toUpdate.isEmpty()) {
            return false;
        }

        var bookCopy = toUpdate.get();

        var currentState = bookCopy.getState();
        bookCopy.setState(BookCopy.State.degrade(currentState));

        try {
            booksRepository.updateBookCopy(bookCopy);
            return true;
        } catch (RepositoryException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean degradeMagazineCopy(String issn, int issue, int number) {
        var toUpdate = magazinesRepository.findMagazineCopyByIssnAndIssueAndNumber(
                issn,
                issue,
                number
        );

        if (toUpdate.isEmpty()) {
            return false;
        }

        var magazineCopy = toUpdate.get();

        var currentState = magazineCopy.getState();
        magazineCopy.setState(MagazineCopy.State.degrade(currentState));

        try {
            magazinesRepository.updateMagazineCopy(magazineCopy);
            return true;
        } catch (RepositoryException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBookCopy(String isbn, int number) {

        var toRemove = booksRepository.findBookCopyByIsbnAndNumber(
                isbn,
                number
        );

        if (toRemove.isEmpty()) {
            return true;
        }

        try {
            booksRepository.deleteBookCopy(toRemove.get());
            eventsRepository.clearDanglingReferencesFor(toRemove.get().getUuid());
            return true;
        } catch (RepositoryException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMagazineCopy(String issn, int issue, int number) {

        var toRemove = magazinesRepository.findMagazineCopyByIssnAndIssueAndNumber(
                issn,
                issue,
                number
        );

        if (toRemove.isEmpty()) {
            return true;
        }

        try {
            magazinesRepository.deleteMagazineCopy(toRemove.get());
            eventsRepository.clearDanglingReferencesFor(toRemove.get().getUuid());
            return true;
        } catch (RepositoryException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<ElementDto, Long> getAvailableCopiesCount() {

        var books = booksRepository.findAllBooks();
        var booksMap = new TreeMap<ElementDto, Long>();

        for (var book : books) {
            var copies = booksRepository.findBookCopiesByIsbnAndNotDamaged(book.getIsbn());

            var amount = copies.stream()
                    .filter(copy -> eventsRepository.isElementAvailable(copy.getUuid()))
                    .count();

            booksMap.put(mapper.map(book), amount);
        }

        var magazines = magazinesRepository.findAllMagazines();

        for (var magazine : magazines) {
            var copies =
                    magazinesRepository.findMagazineCopiesByIssnAndIssueAndNotDamaged(magazine.getIssn(), magazine.getIssue());

            var amount = copies.stream()
                    .filter(copy -> eventsRepository.isElementAvailable(copy.getUuid()))
                    .count();

            booksMap.put(mapper.map(magazine), amount);
        }

        return booksMap;

    }

    public Optional<BookDto> getBook(String isbn) {

        return booksRepository.findBookByIsbn(isbn)
                .map(book -> mapper.map(book));
    }

    public Optional<MagazineDto> getMagazine(String ref, Integer issue) {
        return magazinesRepository
                .findMagazineByIssnAndIssue(ref, issue)
                .map(magazine -> mapper.map(magazine));
    }

    public boolean addBook(BookDto book) {

        try{
            booksRepository.addBook(new Book(
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher()
            ));
            return true;
        } catch (ObjectAlreadyExistsException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addMagazine(MagazineDto magazine){
        try {
            magazinesRepository.addMagazine(mapper.map(magazine));
            return true;
        } catch (ObjectAlreadyExistsException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addElement(ElementDto elementDto) {

        if (elementDto instanceof BookDto) {

            var book = (BookDto) elementDto;
            return addBook(book);

        } else if (elementDto instanceof MagazineDto) {

            var magazine = (MagazineDto) elementDto;
            return addMagazine(magazine);

        }
        return false;

    }

    private List<ElementCopyDto> booksToCopies(List<BookCopy> bookCopies) {

        var copies = new ArrayList<ElementCopyDto>();

        for (var x : bookCopies) {

            var book = booksRepository.findBookByUuid(x.getElementUuid()).orElseThrow();

            var bookDto = mapper.map(book);

            copies.add(new ElementCopyDto(x.getUuid().toString(), x.getNumber(), bookDto, StateUtils.mapState(x.getState())));
        }

        return copies;
    }

    private List<ElementCopyDto> magazinesToCopies(List<MagazineCopy> magazineCopies) {

        var copies = new ArrayList<ElementCopyDto>();

        for (var x : magazineCopies) {
            var magazine = magazinesRepository.findMagazineByUuid(x.getElementUuid()).orElseThrow();
            var magazineDto = mapper.map(magazine);

            copies.add(new ElementCopyDto(x.getUuid().toString(), x.getNumber(), magazineDto, StateUtils.mapState(x.getState())));
        }

        return copies;
    }

    public Page<ElementCopyDto> getCopiesPageByIssnIsbnContains(String query, int pageSize, int pageNumber) {

        int bookCopiesCount = booksRepository.countBookCopiesByIsbnContains(query);

        int firstItemNumber = pageNumber * pageSize;

        int booksNumber = min(pageSize, bookCopiesCount - firstItemNumber);

        var copies = new ArrayList<ElementCopyDto>();

        if (booksNumber > 0) {
            var books = booksRepository.findBookCopiesByIsbnContains(query, pageSize, firstItemNumber);

            copies.addAll(booksToCopies(books));
        }

        if (copies.size() == pageSize) {
            return new Page<>(pageNumber, copies);
        }

        int firstMagazineItem;

        if (booksNumber <= 0) {
            firstMagazineItem = abs(booksNumber);
        } else {
            firstMagazineItem = 0;
        }

        var magazinesLimit = pageSize - copies.size();

        var magazines = magazinesRepository.findMagazineCopiesByIssnContains(query, magazinesLimit, firstMagazineItem);

        copies.addAll(magazinesToCopies(magazines));

        return new Page<>(pageNumber, copies);
    }

    public boolean updateElement(ElementDto elementDto) {

        if (elementDto instanceof BookDto) {
            return updateBook((BookDto) elementDto);
        } else if (elementDto instanceof MagazineDto) {
            return updateMagazine((MagazineDto) elementDto);
        } else {
            throw new IllegalStateException("Unsupported element type!");
        }
    }

    private boolean updateMagazine(MagazineDto elementDto) {

        var magazineOpt = magazinesRepository.findMagazineByIssnAndIssue(
                elementDto.getIssn(),
                elementDto.getIssue()
        );

        if (magazineOpt.isEmpty()) {
            return false;
        }

        var updatedMagazine = magazineOpt.get();

        updatedMagazine.setPublisher(elementDto.getPublisher());
        updatedMagazine.setTitle(elementDto.getTitle());

        try {
            magazinesRepository.updateMagazine(updatedMagazine);
            return true;
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateBook(BookDto elementDto) {
        var bookOpt = booksRepository.findBookByIsbn(elementDto.getIsbn());

        if (bookOpt.isEmpty()){
            return false;
        }

        var updatedBook = bookOpt.get();

        updatedBook.setAuthor(elementDto.getAuthor());
        updatedBook.setTitle(elementDto.getTitle());
        updatedBook.setPublisher(elementDto.getPublisher());

        try {
            booksRepository.updateBook(updatedBook);
            return true;
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ElementDto> getAllElements() {
        var magazines = getAllMagazines();
        var books = getAllBooks();

        var elements = new ArrayList<ElementDto>(magazines);

        elements.addAll(books);

        return elements;
    }
}
