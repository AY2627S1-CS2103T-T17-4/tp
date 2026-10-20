package seedu.hobbyhub.model.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.testutil.Assert.assertThrows;
import static seedu.hobbyhub.testutil.TypicalSessions.CHESS_MEETUP;
import static seedu.hobbyhub.testutil.TypicalSessions.CYCLING_TRIP;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.model.session.exceptions.DuplicateSessionException;
import seedu.hobbyhub.model.session.exceptions.SessionNotFoundException;
import seedu.hobbyhub.testutil.SessionBuilder;

public class UniqueSessionListTest {

    private final UniqueSessionList uniqueSessionList = new UniqueSessionList();

    @Test
    public void contains_nullSession_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueSessionList.contains(null));
    }

    @Test
    public void contains_sessionNotInList_returnsFalse() {
        assertFalse(uniqueSessionList.contains(CHESS_MEETUP));
    }

    @Test
    public void contains_sessionInList_returnsTrue() {
        uniqueSessionList.add(CHESS_MEETUP);
        assertTrue(uniqueSessionList.contains(CHESS_MEETUP));
    }

    @Test
    public void contains_sessionWithSameIdentityFieldsInList_returnsTrue() {
        uniqueSessionList.add(CHESS_MEETUP);
        Session editedChess = new SessionBuilder(CHESS_MEETUP).withDate("2026-12-01").build();
        assertTrue(uniqueSessionList.contains(editedChess));
    }

    @Test
    public void add_nullSession_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueSessionList.add(null));
    }

    @Test
    public void add_duplicateSession_throwsDuplicateSessionException() {
        uniqueSessionList.add(CHESS_MEETUP);
        assertThrows(DuplicateSessionException.class, () -> uniqueSessionList.add(CHESS_MEETUP));
    }

    @Test
    public void setSession_nullTargetSession_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueSessionList.setSession(null, CHESS_MEETUP));
    }

    @Test
    public void setSession_nullEditedSession_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueSessionList.setSession(CHESS_MEETUP, null));
    }

    @Test
    public void setSession_targetSessionNotInList_throwsSessionNotFoundException() {
        assertThrows(SessionNotFoundException.class, () -> uniqueSessionList.setSession(CHESS_MEETUP, CHESS_MEETUP));
    }

    @Test
    public void setSession_editedSessionIsSameSession_success() {
        uniqueSessionList.add(CHESS_MEETUP);
        uniqueSessionList.setSession(CHESS_MEETUP, CHESS_MEETUP);
        UniqueSessionList expectedUniqueSessionList = new UniqueSessionList();
        expectedUniqueSessionList.add(CHESS_MEETUP);
        assertEquals(expectedUniqueSessionList, uniqueSessionList);
    }

    @Test
    public void setSession_editedSessionHasSameIdentity_success() {
        uniqueSessionList.add(CHESS_MEETUP);
        Session editedChess = new SessionBuilder(CHESS_MEETUP).withDate("2026-12-01").build();
        uniqueSessionList.setSession(CHESS_MEETUP, editedChess);
        UniqueSessionList expectedUniqueSessionList = new UniqueSessionList();
        expectedUniqueSessionList.add(editedChess);
        assertEquals(expectedUniqueSessionList, uniqueSessionList);
    }

    @Test
    public void setSession_editedSessionHasDifferentIdentity_success() {
        uniqueSessionList.add(CHESS_MEETUP);
        uniqueSessionList.setSession(CHESS_MEETUP, CYCLING_TRIP);
        UniqueSessionList expectedUniqueSessionList = new UniqueSessionList();
        expectedUniqueSessionList.add(CYCLING_TRIP);
        assertEquals(expectedUniqueSessionList, uniqueSessionList);
    }

    @Test
    public void setSession_editedSessionHasNonUniqueIdentity_throwsDuplicateSessionException() {
        uniqueSessionList.add(CHESS_MEETUP);
        uniqueSessionList.add(CYCLING_TRIP);
        assertThrows(DuplicateSessionException.class, () -> uniqueSessionList.setSession(CHESS_MEETUP, CYCLING_TRIP));
    }

    @Test
    public void remove_nullSession_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueSessionList.remove(null));
    }

    @Test
    public void remove_sessionDoesNotExist_throwsSessionNotFoundException() {
        assertThrows(SessionNotFoundException.class, () -> uniqueSessionList.remove(CHESS_MEETUP));
    }

    @Test
    public void remove_existingSession_removesSession() {
        uniqueSessionList.add(CHESS_MEETUP);
        uniqueSessionList.remove(CHESS_MEETUP);
        UniqueSessionList expectedUniqueSessionList = new UniqueSessionList();
        assertEquals(expectedUniqueSessionList, uniqueSessionList);
    }

    @Test
    public void setSessions_nullUniqueSessionList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueSessionList.setSessions((UniqueSessionList) null));
    }

    @Test
    public void setSessions_uniqueSessionList_replacesOwnListWithProvidedUniqueSessionList() {
        uniqueSessionList.add(CHESS_MEETUP);
        UniqueSessionList expectedUniqueSessionList = new UniqueSessionList();
        expectedUniqueSessionList.add(CYCLING_TRIP);
        uniqueSessionList.setSessions(expectedUniqueSessionList);
        assertEquals(expectedUniqueSessionList, uniqueSessionList);
    }

    @Test
    public void setSessions_nullList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueSessionList.setSessions((List<Session>) null));
    }

    @Test
    public void setSessions_list_replacesOwnListWithProvidedList() {
        uniqueSessionList.add(CHESS_MEETUP);
        List<Session> sessionList = List.of(CYCLING_TRIP);
        uniqueSessionList.setSessions(sessionList);
        UniqueSessionList expectedUniqueSessionList = new UniqueSessionList();
        expectedUniqueSessionList.add(CYCLING_TRIP);
        assertEquals(expectedUniqueSessionList, uniqueSessionList);
    }

    @Test
    public void setSessions_listWithDuplicateSessions_throwsDuplicateSessionException() {
        List<Session> listWithDuplicateSessions = List.of(CHESS_MEETUP, CHESS_MEETUP);
        assertThrows(DuplicateSessionException.class, () -> uniqueSessionList.setSessions(listWithDuplicateSessions));
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, ()
            -> uniqueSessionList.asUnmodifiableObservableList().remove(0));
    }

    @Test
    public void iteratorAndEqualityMethods() {
        uniqueSessionList.add(CHESS_MEETUP);

        UniqueSessionList sameSessions = new UniqueSessionList();
        sameSessions.add(CHESS_MEETUP);

        assertEquals(CHESS_MEETUP, uniqueSessionList.iterator().next());
        assertEquals(uniqueSessionList, uniqueSessionList);
        assertEquals(uniqueSessionList, sameSessions);
        assertEquals(uniqueSessionList.hashCode(), sameSessions.hashCode());
        assertFalse(uniqueSessionList.equals(null));
        assertFalse(uniqueSessionList.equals(CHESS_MEETUP));
    }

    @Test
    public void toStringMethod() {
        assertEquals(uniqueSessionList.asUnmodifiableObservableList().toString(), uniqueSessionList.toString());
    }
}
