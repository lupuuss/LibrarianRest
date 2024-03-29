package pl.lodz.pas.librarianrest.repository.events;

import pl.lodz.pas.librarianrest.repository.events.data.ElementLock;
import pl.lodz.pas.librarianrest.repository.events.data.Event;
import pl.lodz.pas.librarianrest.repository.events.data.LendingEvent;
import pl.lodz.pas.librarianrest.repository.events.data.ReturnEvent;
import pl.lodz.pas.librarianrest.repository.exceptions.InconsistencyFoundException;
import pl.lodz.pas.librarianrest.repository.exceptions.RepositoryException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventsRepository {

    void addEvent(Event event) throws RepositoryException;

    Boolean isElementAvailable(UUID uuid);

    List<LendingEvent> findLendingEventsByUserLogin(String userLogin);

    Optional<ReturnEvent> findReturnEventByUuid(UUID uuid);

    void saveElementLock(ElementLock lock) throws InconsistencyFoundException;

    void deleteElementLock(UUID uuid, String user);

    List<LendingEvent> findAllLendingEvents();

    List<LendingEvent> findLendingEventsByUserLoginContains(String loginQuery);

    void deleteLendingEventByElementCopyUuidDate(UUID uuid, LocalDateTime date);

    void deleteDanglingEventByDateAndUser(String login, LocalDateTime date);

    Optional<LendingEvent> findLendingEventByElementCopyUuidDate(UUID uuid, LocalDateTime date);

    void addReturnEvent(UUID lendEventUuid, LocalDateTime now, String customerLogin, UUID elementUuid) throws InconsistencyFoundException;

    void clearDanglingReferencesFor(UUID uuid);

    Optional<ElementLock> getCopyLock(UUID uuid);

    boolean deleteLendingEventByUuid(UUID uuid);

    Optional<LendingEvent> findLendingEventByUuid(UUID uuid);
}