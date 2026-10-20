package seedu.hobbyhub.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.hobbyhub.testutil.Assert.assertThrows;
import static seedu.hobbyhub.testutil.TypicalMembers.ALICE;
import static seedu.hobbyhub.testutil.TypicalMembers.BENSON;
import static seedu.hobbyhub.testutil.TypicalMembers.DANIEL;
import static seedu.hobbyhub.testutil.TypicalMembers.getTypicalHobbyHub;
import static seedu.hobbyhub.testutil.TypicalSessions.CHESS_MEETUP;
import static seedu.hobbyhub.testutil.TypicalSessions.CYCLING_TRIP;
import static seedu.hobbyhub.testutil.TypicalSessions.getTypicalHobbyHubWithSessions;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.member.exceptions.DuplicateMemberException;
import seedu.hobbyhub.model.session.Session;
import seedu.hobbyhub.model.session.exceptions.DuplicateSessionException;
import seedu.hobbyhub.model.session.exceptions.SessionNotFoundException;
import seedu.hobbyhub.testutil.MemberBuilder;
import seedu.hobbyhub.testutil.SessionBuilder;

public class HobbyHubTest {

    private final HobbyHub hobbyHub = new HobbyHub();

    @Test
    public void constructor() {
        assertEquals(List.of(), hobbyHub.getMemberList());
        assertEquals(List.of(), hobbyHub.getSessionList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> hobbyHub.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyHobbyHub_replacesData() {
        HobbyHub newData = getTypicalHobbyHub();
        hobbyHub.resetData(newData);
        assertEquals(newData, hobbyHub);
    }

    @Test
    public void resetData_withSessions_replacesSessions() {
        HobbyHub newData = getTypicalHobbyHubWithSessions();
        hobbyHub.resetData(newData);
        assertEquals(newData.getSessionList(), hobbyHub.getSessionList());
    }

    @Test
    public void resetData_withDuplicateSessions_throwsDuplicateSessionException() {
        Session editedChess = new SessionBuilder(CHESS_MEETUP).withDate("2026-12-01").build();
        HobbyHubStub newData = new HobbyHubStub(List.of(), List.of(CHESS_MEETUP, editedChess));

        assertThrows(DuplicateSessionException.class, () -> hobbyHub.resetData(newData));
    }

    @Test
    public void resetData_withDuplicateMembers_throwsDuplicateMemberException() {
        // Two members with the same identity fields
        Member editedAlice = new MemberBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Member> newMembers = List.of(ALICE, editedAlice);
        HobbyHubStub newData = new HobbyHubStub(newMembers);

        assertThrows(DuplicateMemberException.class, () -> hobbyHub.resetData(newData));
    }

    @Test
    public void hasMember_nullMember_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> hobbyHub.hasMember(null));
    }

    @Test
    public void hasMember_memberNotInHobbyHub_returnsFalse() {
        assertFalse(hobbyHub.hasMember(ALICE));
    }

    @Test
    public void hasMember_memberInHobbyHub_returnsTrue() {
        hobbyHub.addMember(ALICE);
        assertTrue(hobbyHub.hasMember(ALICE));
    }

    @Test
    public void hasMember_memberWithSameIdentityFieldsInHobbyHub_returnsTrue() {
        hobbyHub.addMember(ALICE);
        Member editedAlice = new MemberBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(hobbyHub.hasMember(editedAlice));
    }

    @Test
    public void getMemberList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> hobbyHub.getMemberList().remove(0));
    }

    @Test
    public void hasSession_nullSession_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> hobbyHub.hasSession(null));
    }

    @Test
    public void hasSession_sessionNotInHobbyHub_returnsFalse() {
        assertFalse(hobbyHub.hasSession(CHESS_MEETUP));
    }

    @Test
    public void hasSession_sessionWithSameNameInHobbyHub_returnsTrue() {
        hobbyHub.addSession(CHESS_MEETUP);
        Session sameNameDifferentCase = new SessionBuilder(CHESS_MEETUP)
                .withName(CHESS_MEETUP.getName().fullName.toLowerCase()).withDate("2026-12-01").build();
        assertTrue(hobbyHub.hasSession(sameNameDifferentCase));
    }

    @Test
    public void addSession_duplicateSession_throwsDuplicateSessionException() {
        hobbyHub.addSession(CHESS_MEETUP);
        assertThrows(DuplicateSessionException.class, () -> hobbyHub.addSession(CHESS_MEETUP));
    }

    @Test
    public void setSession_existingSession_replacesSession() {
        hobbyHub.addSession(CHESS_MEETUP);
        Session editedChess = CHESS_MEETUP.withoutMember(ALICE.getEmail());
        hobbyHub.setSession(CHESS_MEETUP, editedChess);
        assertEquals(List.of(editedChess), hobbyHub.getSessionList());
    }

    @Test
    public void removeSession_sessionNotInHobbyHub_throwsSessionNotFoundException() {
        assertThrows(SessionNotFoundException.class, () -> hobbyHub.removeSession(CHESS_MEETUP));
    }

    @Test
    public void removeSession_existingSession_removesSession() {
        hobbyHub.addSession(CHESS_MEETUP);
        hobbyHub.removeSession(CHESS_MEETUP);
        assertEquals(List.of(), hobbyHub.getSessionList());
    }

    @Test
    public void removeMember_memberInSessions_removedFromAllSessions() {
        HobbyHub hobbyHubWithSessions = getTypicalHobbyHubWithSessions();
        hobbyHubWithSessions.removeMember(BENSON);

        // BENSON was in both typical sessions
        for (Session session : hobbyHubWithSessions.getSessionList()) {
            assertFalse(session.hasMember(BENSON.getEmail()));
        }
        // other members are still assigned
        assertTrue(hobbyHubWithSessions.getSessionList().get(0).hasMember(ALICE.getEmail()));
        // the sessions themselves are kept
        assertEquals(2, hobbyHubWithSessions.getSessionList().size());
    }

    @Test
    public void removeMember_memberInNoSession_sessionsUnchanged() {
        HobbyHub hobbyHubWithSessions = getTypicalHobbyHubWithSessions();
        List<Session> sessionsBefore = List.copyOf(hobbyHubWithSessions.getSessionList());
        hobbyHubWithSessions.removeMember(DANIEL); // DANIEL is in no session
        assertEquals(sessionsBefore, hobbyHubWithSessions.getSessionList());
    }

    @Test
    public void getSessionList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> hobbyHub.getSessionList().remove(0));
    }

    @Test
    public void equalityAndHashCodeMethods() {
        HobbyHub sameHobbyHub = new HobbyHub();

        assertEquals(hobbyHub, hobbyHub);
        assertEquals(hobbyHub, sameHobbyHub);
        assertEquals(hobbyHub.hashCode(), sameHobbyHub.hashCode());
        assertFalse(hobbyHub.equals(null));
        assertFalse(hobbyHub.equals(ALICE));

        // different sessions -> returns false
        HobbyHub hobbyHubWithSession = new HobbyHub();
        hobbyHubWithSession.addSession(CYCLING_TRIP);
        assertFalse(hobbyHub.equals(hobbyHubWithSession));
    }

    @Test
    public void toStringMethod() {
        String expected = HobbyHub.class.getCanonicalName() + "{members=" + hobbyHub.getMemberList()
                + ", sessions=" + hobbyHub.getSessionList() + "}";
        assertEquals(expected, hobbyHub.toString());
    }

    /**
     * A stub ReadOnlyHobbyHub whose members and sessions lists can violate interface constraints.
     */
    private static class HobbyHubStub implements ReadOnlyHobbyHub {
        private final ObservableList<Member> members = FXCollections.observableArrayList();
        private final ObservableList<Session> sessions = FXCollections.observableArrayList();

        HobbyHubStub(Collection<Member> members) {
            this(members, List.of());
        }

        HobbyHubStub(Collection<Member> members, Collection<Session> sessions) {
            this.members.setAll(members);
            this.sessions.setAll(sessions);
        }

        @Override
        public ObservableList<Member> getMemberList() {
            return members;
        }

        @Override
        public ObservableList<Session> getSessionList() {
            return sessions;
        }
    }

}
