package seedu.hobbyhub.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.model.Model.PREDICATE_SHOW_ALL_MEMBERS;
import static seedu.hobbyhub.model.Model.PREDICATE_SHOW_ALL_SESSIONS;
import static seedu.hobbyhub.testutil.Assert.assertThrows;
import static seedu.hobbyhub.testutil.TypicalMembers.ALICE;
import static seedu.hobbyhub.testutil.TypicalMembers.BENSON;
import static seedu.hobbyhub.testutil.TypicalMembers.CARL;
import static seedu.hobbyhub.testutil.TypicalMembers.DANIEL;
import static seedu.hobbyhub.testutil.TypicalSessions.CHESS_MEETUP;
import static seedu.hobbyhub.testutil.TypicalSessions.CYCLING_TRIP;
import static seedu.hobbyhub.testutil.TypicalSessions.getTypicalHobbyHubWithSessions;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.commons.core.GuiSettings;
import seedu.hobbyhub.model.member.NameContainsKeywordsPredicate;
import seedu.hobbyhub.model.session.Session;
import seedu.hobbyhub.testutil.HobbyHubBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new HobbyHub(), new HobbyHub(modelManager.getHobbyHub()));
    }

    @Test
    public void constructor_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager = new ModelManager(new HobbyHub(), userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setGuiSettings(new GuiSettings(5, 6, 7, 8));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void hasMember_nullMember_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasMember(null));
    }

    @Test
    public void hasMember_memberNotInHobbyHub_returnsFalse() {
        assertFalse(modelManager.hasMember(ALICE));
    }

    @Test
    public void hasMember_memberInHobbyHub_returnsTrue() {
        modelManager.addMember(ALICE);
        assertTrue(modelManager.hasMember(ALICE));
    }

    @Test
    public void getFilteredMemberList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredMemberList().remove(0));
    }

    @Test
    public void hasSession_nullSession_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasSession(null));
    }

    @Test
    public void hasSession_sessionNotInHobbyHub_returnsFalse() {
        assertFalse(modelManager.hasSession(CHESS_MEETUP));
    }

    @Test
    public void addSession_validSession_sessionAddedAndShown() {
        modelManager.addSession(CHESS_MEETUP);
        assertTrue(modelManager.hasSession(CHESS_MEETUP));
        assertEquals(List.of(CHESS_MEETUP), modelManager.getFilteredSessionList());
    }

    @Test
    public void deleteSession_existingSession_sessionRemoved() {
        modelManager.addSession(CHESS_MEETUP);
        modelManager.deleteSession(CHESS_MEETUP);
        assertFalse(modelManager.hasSession(CHESS_MEETUP));
    }

    @Test
    public void setSession_existingSession_sessionReplaced() {
        modelManager.addSession(CHESS_MEETUP);
        Session editedChess = CHESS_MEETUP.withMember(CARL.getEmail());
        modelManager.setSession(CHESS_MEETUP, editedChess);
        assertEquals(List.of(editedChess), modelManager.getFilteredSessionList());
    }

    @Test
    public void updateFilteredSessionList_predicate_filtersSessions() {
        modelManager = new ModelManager(getTypicalHobbyHubWithSessions(), new UserPrefs());
        modelManager.updateFilteredSessionList(session -> session.equals(CYCLING_TRIP));
        assertEquals(List.of(CYCLING_TRIP), modelManager.getFilteredSessionList());

        modelManager.updateFilteredSessionList(PREDICATE_SHOW_ALL_SESSIONS);
        assertEquals(List.of(CHESS_MEETUP, CYCLING_TRIP), modelManager.getFilteredSessionList());
    }

    @Test
    public void getFilteredSessionList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredSessionList().remove(0));
    }

    @Test
    public void getSessionsOf() {
        modelManager = new ModelManager(getTypicalHobbyHubWithSessions(), new UserPrefs());
        assertThrows(NullPointerException.class, () -> modelManager.getSessionsOf(null));

        // member in one session
        assertEquals(List.of(CHESS_MEETUP), modelManager.getSessionsOf(ALICE));

        // member in two sessions
        assertEquals(List.of(CHESS_MEETUP, CYCLING_TRIP), modelManager.getSessionsOf(BENSON));

        // member in no session
        assertEquals(List.of(), modelManager.getSessionsOf(DANIEL));

        // still counts sessions hidden by the session filter
        modelManager.updateFilteredSessionList(session -> false);
        assertEquals(List.of(CHESS_MEETUP), modelManager.getSessionsOf(ALICE));
    }

    @Test
    public void deleteMember_memberInSessions_removedFromSessions() {
        modelManager = new ModelManager(getTypicalHobbyHubWithSessions(), new UserPrefs());
        modelManager.deleteMember(BENSON);
        assertEquals(List.of(), modelManager.getSessionsOf(BENSON));
    }

    @Test
    public void equals() {
        HobbyHub hobbyHub = new HobbyHubBuilder().withMember(ALICE).withMember(BENSON).build();
        HobbyHub differentHobbyHub = new HobbyHub();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(hobbyHub, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(hobbyHub, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different hobbyHub -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentHobbyHub, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredMemberList(new NameContainsKeywordsPredicate(List.of(keywords)));
        assertFalse(modelManager.equals(new ModelManager(hobbyHub, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredMemberList(PREDICATE_SHOW_ALL_MEMBERS);

        // different sessions -> returns false
        HobbyHub hobbyHubWithSession = new HobbyHubBuilder().withMember(ALICE).withMember(BENSON)
                .withSession(CHESS_MEETUP).build();
        assertFalse(modelManager.equals(new ModelManager(hobbyHubWithSession, userPrefs)));

        // different filtered session list -> returns false
        ModelManager modelManagerWithSession = new ModelManager(hobbyHubWithSession, userPrefs);
        modelManagerWithSession.updateFilteredSessionList(session -> false);
        assertFalse(modelManagerWithSession.equals(new ModelManager(hobbyHubWithSession, userPrefs)));

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(hobbyHub, differentUserPrefs)));
    }
}
