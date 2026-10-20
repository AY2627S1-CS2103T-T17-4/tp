package seedu.hobbyhub.model.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.testutil.Assert.assertThrows;
import static seedu.hobbyhub.testutil.TypicalMembers.ALICE;
import static seedu.hobbyhub.testutil.TypicalMembers.BENSON;
import static seedu.hobbyhub.testutil.TypicalMembers.CARL;
import static seedu.hobbyhub.testutil.TypicalSessions.CHESS_MEETUP;
import static seedu.hobbyhub.testutil.TypicalSessions.CYCLING_TRIP;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.model.member.Email;
import seedu.hobbyhub.testutil.SessionBuilder;

public class SessionTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        SessionName name = new SessionName("Golf Meet");
        LocalDate date = LocalDate.of(2026, 10, 24);
        Set<Email> members = new HashSet<>();
        assertThrows(NullPointerException.class, () -> new Session(null, date, members));
        assertThrows(NullPointerException.class, () -> new Session(name, null, members));
        assertThrows(NullPointerException.class, () -> new Session(name, date, null));

        Set<Email> membersWithNull = new HashSet<>();
        membersWithNull.add(null);
        assertThrows(NullPointerException.class, () -> new Session(name, date, membersWithNull));
    }

    @Test
    public void constructor_changeInputSet_sessionUnchanged() {
        Set<Email> members = new HashSet<>();
        members.add(ALICE.getEmail());
        Session session = new Session(new SessionName("Golf Meet"), LocalDate.of(2026, 10, 24), members);
        members.add(BENSON.getEmail());
        assertFalse(session.hasMember(BENSON.getEmail()));
    }

    @Test
    public void getMembers_modifySet_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> CHESS_MEETUP.getMembers().add(CARL.getEmail()));
    }

    @Test
    public void hasMember() {
        assertThrows(NullPointerException.class, () -> CHESS_MEETUP.hasMember(null));
        assertTrue(CHESS_MEETUP.hasMember(ALICE.getEmail()));
        assertFalse(CHESS_MEETUP.hasMember(CARL.getEmail()));
    }

    @Test
    public void withMember_newMember_returnsSessionWithMember() {
        Session updated = CHESS_MEETUP.withMember(CARL.getEmail());
        assertTrue(updated.hasMember(CARL.getEmail()));
        assertTrue(updated.hasMember(ALICE.getEmail()));
        assertEquals(CHESS_MEETUP.getName(), updated.getName());
        assertEquals(CHESS_MEETUP.getDate(), updated.getDate());

        // the original session is not changed
        assertFalse(CHESS_MEETUP.hasMember(CARL.getEmail()));
    }

    @Test
    public void withoutMember_existingMember_returnsSessionWithoutMember() {
        Session updated = CHESS_MEETUP.withoutMember(ALICE.getEmail());
        assertFalse(updated.hasMember(ALICE.getEmail()));
        assertTrue(updated.hasMember(BENSON.getEmail()));

        // the original session is not changed
        assertTrue(CHESS_MEETUP.hasMember(ALICE.getEmail()));
    }

    @Test
    public void withMember_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> CHESS_MEETUP.withMember(null));
        assertThrows(NullPointerException.class, () -> CHESS_MEETUP.withoutMember(null));
    }

    @Test
    public void isSameSession() {
        // same object -> returns true
        assertTrue(CHESS_MEETUP.isSameSession(CHESS_MEETUP));

        // null -> returns false
        assertFalse(CHESS_MEETUP.isSameSession(null));

        // same name, all other attributes different -> returns true
        Session editedChess = new SessionBuilder(CHESS_MEETUP).withDate("2026-12-01")
                .withMembers(CARL.getEmail().value).build();
        assertTrue(CHESS_MEETUP.isSameSession(editedChess));

        // same name in a different case -> returns true
        editedChess = new SessionBuilder(CHESS_MEETUP)
                .withName(CHESS_MEETUP.getName().fullName.toUpperCase()).build();
        assertTrue(CHESS_MEETUP.isSameSession(editedChess));

        // different name, all other attributes same -> returns false
        editedChess = new SessionBuilder(CHESS_MEETUP).withName("Weekday Chess Meetup").build();
        assertFalse(CHESS_MEETUP.isSameSession(editedChess));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Session chessCopy = new SessionBuilder(CHESS_MEETUP).build();
        assertTrue(CHESS_MEETUP.equals(chessCopy));
        assertEquals(CHESS_MEETUP.hashCode(), chessCopy.hashCode());

        // same object -> returns true
        assertTrue(CHESS_MEETUP.equals(CHESS_MEETUP));

        // null -> returns false
        assertFalse(CHESS_MEETUP.equals(null));

        // different type -> returns false
        assertFalse(CHESS_MEETUP.equals(5));

        // different session -> returns false
        assertFalse(CHESS_MEETUP.equals(CYCLING_TRIP));

        // different name -> returns false
        Session editedChess = new SessionBuilder(CHESS_MEETUP).withName("Weekday Chess Meetup").build();
        assertFalse(CHESS_MEETUP.equals(editedChess));

        // different date -> returns false
        editedChess = new SessionBuilder(CHESS_MEETUP).withDate("2026-12-01").build();
        assertFalse(CHESS_MEETUP.equals(editedChess));

        // different members -> returns false
        editedChess = new SessionBuilder(CHESS_MEETUP).withMembers(CARL.getEmail().value).build();
        assertFalse(CHESS_MEETUP.equals(editedChess));
    }

    @Test
    public void toStringMethod() {
        String expected = Session.class.getCanonicalName() + "{name=" + CHESS_MEETUP.getName()
                + ", date=" + CHESS_MEETUP.getDate() + ", members=" + CHESS_MEETUP.getMembers() + "}";
        assertEquals(expected, CHESS_MEETUP.toString());
    }
}
