package pl.lodz.pas.librarianrest.services;

import pl.lodz.pas.librarianrest.DateProvider;
import pl.lodz.pas.librarianrest.repository.books.BooksRepository;
import pl.lodz.pas.librarianrest.repository.books.MagazinesRepository;
import pl.lodz.pas.librarianrest.repository.books.data.BookCopy;
import pl.lodz.pas.librarianrest.repository.books.data.ElementCopy;
import pl.lodz.pas.librarianrest.repository.books.data.MagazineCopy;
import pl.lodz.pas.librarianrest.repository.events.EventsRepository;
import pl.lodz.pas.librarianrest.repository.events.data.ElementLock;
import pl.lodz.pas.librarianrest.repository.events.data.LendingEvent;
import pl.lodz.pas.librarianrest.repository.exceptions.InconsistencyFoundException;
import pl.lodz.pas.librarianrest.repository.exceptions.RepositoryException;
import pl.lodz.pas.librarianrest.repository.user.UsersRepository;
import pl.lodz.pas.librarianrest.services.dto.*;
import pl.lodz.pas.librarianrest.services.exceptions.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequestScoped
public class LendingsService {

    @Inject
    private DtoMapper mapper;

    @Inject
    private DateProvider dateProvider;

    @Inject
    private BooksRepository booksRepository;

    @Inject
    private MagazinesRepository magazinesRepository;

    @Inject
    private EventsRepository eventsRepository;

    @Inject
    private UsersRepository usersRepository;

    private final long reservationTimeInMinutes = 30;

    public List<LendEventDto> getLendingsForUser(String user) {

        var events = eventsRepository.findLendingEventsByUserLogin(user);

        return completeEventsDto(events);
    }

    public List<LendEventDto> getAllLendings() {

        var events = eventsRepository.findAllLendingEvents();

        return completeEventsDto(events);
    }

    public List<LendEventDto> getFilteredLendings(String loginQuery, String elementRefQuery) {

        List<LendingEvent> events;

        if ((loginQuery != null && !loginQuery.isBlank())) {
            events = eventsRepository.findLendingEventsByUserLoginContains(loginQuery);
        } else {
            events = eventsRepository.findAllLendingEvents();
        }

        if ((elementRefQuery == null || elementRefQuery.isBlank())) {
            return completeEventsDto(events);
        }

        var bookCopies = booksRepository.findBookCopiesByIsbnContains(elementRefQuery)
                .stream()
                .map(ElementCopy::getUuid);

        var magazinesCopies = magazinesRepository.findMagazineCopiesByIssnContains(elementRefQuery)
                .stream().map(ElementCopy::getUuid);

        var allUuids = Stream.concat(bookCopies, magazinesCopies)
                .collect(Collectors.toList());

        events = events.stream()
                .filter(event -> allUuids.contains(event.getElementUuid()))
                .collect(Collectors.toList());

        return completeEventsDto(events);
    }

    private List<LendEventDto> completeEventsDto(List<LendingEvent> events) {

        List<LendEventDto> eventDtos = new ArrayList<>();

        for (var event : events) {

            var copy = getElementCopyDtoByUuid(event.getElementUuid());

            var lendDate = Timestamp.valueOf(event.getDate());

            Date returnDate = null;

            if (event.getReturnUuid().isPresent()) {
                var returnEvent =
                        eventsRepository.findReturnEventByUuid(event.getReturnUuid().get());

                returnDate = Timestamp.valueOf(returnEvent.orElseThrow().getDate());
            }

            eventDtos.add(new LendEventDto(
                    event.getUuid().toString(),
                    copy,
                    lendDate,
                    returnDate,
                    event.getCustomerLogin()
            ));
        }

        return eventDtos;
    }

    private ElementCopyDto getElementCopyDtoByUuid(UUID uuid) {

        var bookCopy = booksRepository.findBookCopyByUuid(uuid);

        if (bookCopy.isPresent()) {

            var book = booksRepository.findBookByUuid(bookCopy.get().getElementUuid())
                    .orElseThrow();

            return new ElementCopyDto(
                    bookCopy.get().getUuid().toString(),
                    bookCopy.get().getNumber(),
                    mapper.map(book),
                    StateUtils.mapState(bookCopy.get().getState())
            );
        }

        var magazineCopy = magazinesRepository.findMagazineCopyByUuid(uuid);

        if (magazineCopy.isPresent()) {
            var magazine = magazinesRepository.findMagazineByUuid(magazineCopy.get().getElementUuid())
                    .orElseThrow();

            return new ElementCopyDto(
                    magazineCopy.get().getUuid().toString(),
                    magazineCopy.get().getNumber(),
                    mapper.map(magazine),
                    StateUtils.mapState(magazineCopy.get().getState())
            );

        }

        return null;
    }

    public ElementLockDto lockBook(String isbn, String userLogin, ElementCopyDto.State state) throws ServiceException {

        List<BookCopy> copies = booksRepository.findBookCopiesByIsbnAndState(isbn, StateUtils.mapState(state));

        var user = usersRepository.findUserByLogin(userLogin);

        if (user.isEmpty() || !user.get().isActive()) {
            throw new UserInactiveException();
        }

        var optReservedBook = copies.stream()
                .filter(copy -> eventsRepository.isElementAvailable(copy.getUuid()))
                .findAny();

        if (optReservedBook.isEmpty()) {
            throw new ObjectNotFoundException("Element not found!", null);
        }

        var reservedCopy = optReservedBook.get();

        ElementLock lock = getLockForElementCopy(userLogin, reservedCopy);

        if (lock == null) throw new ObjectNoLongerAvailableException("Element is no longer available!", null);

        var book = booksRepository.findBookByUuid(reservedCopy.getElementUuid()).orElseThrow();
        var bookDto = mapper.map(book);

        var result = new ElementCopyDto(
                reservedCopy.getUuid().toString(),
                reservedCopy.getNumber(),
                bookDto,
                StateUtils.mapState(reservedCopy.getState())
        );

        return new ElementLockDto(result, lock.getUserLogin(), lock.getUntil());
    }

    public Optional<ElementLockDto> lockMagazine(String issn, int issue, String userLogin, ElementCopyDto.State state) throws ServiceException {

        List<MagazineCopy> copies = magazinesRepository.findMagazineCopiesByIssnAndIssueAndState(issn, issue, StateUtils.mapState(state));

        var user = usersRepository.findUserByLogin(userLogin);

        if (user.isEmpty() || !user.get().isActive()) {
            throw new UserInactiveException();
        }

        var optReservedBook = copies.stream()
                .filter(copy -> eventsRepository.isElementAvailable(copy.getUuid()))
                .findAny();

        if (optReservedBook.isEmpty()) {
            throw new ObjectNotFoundException("Element not found!", null);
        }

        var reservedCopy = optReservedBook.get();
        ElementLock lock = getLockForElementCopy(userLogin, reservedCopy);

        if (lock == null) throw new ObjectNoLongerAvailableException("Element is no longer available!", null);

        var magazine = magazinesRepository.findMagazineByUuid(reservedCopy.getElementUuid()).orElseThrow();
        var magazineDto = mapper.map(magazine);

        var result = new ElementCopyDto(
                reservedCopy.getUuid().toString(),
                reservedCopy.getNumber(),
                magazineDto,
                StateUtils.mapState(reservedCopy.getState())
        );

        return Optional.of(new ElementLockDto(result, lock.getUserLogin(), lock.getUntil()));
    }

    private ElementLock getLockForElementCopy(String userLogin, ElementCopy<?> copy) {
        ElementLock lock;

        try {
            lock = new ElementLock(
                    copy.getUuid(),
                    userLogin,
                    dateProvider.now().plusMinutes(reservationTimeInMinutes)
            );

            eventsRepository.saveElementLock(lock);

            return lock;

        } catch (InconsistencyFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void unlockCopy(String user, String id) {

        var uuid = UUID.fromString(id);

        eventsRepository.deleteElementLock(uuid, user);
    }

    public void removeNotReturnedLendings(List<String> ids) {

       for (var id : ids) {
           eventsRepository.deleteLendingEventByUuid(UUID.fromString(id));
       }
    }

    private Optional<ElementCopy<?>> getElementCopyByDto(ElementCopyDto copy) {

        if (copy == null) {
            return Optional.empty();
        }

        if (copy.getElement() instanceof BookDto) {
            return booksRepository.findBookCopyByIsbnAndNumber(
                    ((BookDto) copy.getElement()).getIsbn(),
                    copy.getNumber()
            ).map(c -> c);
        } else if (copy.getElement() instanceof MagazineDto) {

            return magazinesRepository.findMagazineCopyByIssnAndIssueAndNumber(
                    ((MagazineDto) copy.getElement()).getIssn(),
                    ((MagazineDto) copy.getElement()).getIssue(),
                    copy.getNumber()
            ).map(c -> c);

        } else {
            throw new IllegalStateException("Unsupported element type!");
        }
    }

    public void returnLendings(List<String> ids) {

        var uuids = ids
                .stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());

        for (var uuid : uuids) {

            var lendingEventOpt = eventsRepository.findLendingEventByUuid(uuid);

            if (lendingEventOpt.isEmpty()) {
                continue;
            }

            var lendingEvent = lendingEventOpt.get();

            try {
                eventsRepository.addReturnEvent(
                        lendingEvent.getUuid(),
                        dateProvider.now(),
                        lendingEvent.getCustomerLogin(),
                        lendingEvent.getElementUuid()
                );

            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
    }

    public void lendCopiesByIds(String userLogin, List<String> ids) throws ServiceException {


        var user = usersRepository.findUserByLogin(userLogin);

        if (user.isEmpty() || !user.get().isActive()) {
            throw new UserInactiveException();
        }

        var elementsAvailable = ids
                .stream()
                .map(id -> eventsRepository.getCopyLock(UUID.fromString(id)))
                .allMatch(lock -> lock.map(l -> l.getUntil().isAfter(dateProvider.now())).orElse(false));


        if (elementsAvailable) {
            throw new ObjectNoLongerAvailableException("One of the locks expired!", null);
        }

        for (var id : ids) {

            UUID uuid = UUID.fromString(id);

            eventsRepository.deleteElementLock(uuid, userLogin);

            try {
                eventsRepository.addEvent(new LendingEvent(dateProvider.now(), userLogin, uuid));
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
    }
}
