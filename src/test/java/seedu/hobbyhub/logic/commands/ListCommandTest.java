package seedu.hobbyhub.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.showMemberAtIndex;
import static seedu.hobbyhub.testutil.Assert.assertThrows;
import static seedu.hobbyhub.testutil.TypicalIndexes.INDEX_FIRST_MEMBER;
import static seedu.hobbyhub.testutil.TypicalMembers.getTypicalHobbyHub;
import static seedu.hobbyhub.testutil.TypicalSessions.getTypicalHobbyHubWithSessions;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.hobbyhub.model.ListState;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.ModelManager;
import seedu.hobbyhub.model.UserPrefs;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalHobbyHub(), new UserPrefs());
        expectedModel = new ModelManager(model.getHobbyHub(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showMemberAtIndex(model, INDEX_FIRST_MEMBER);
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_sessionFlag_switchesToSessionList() {
        expectedModel.setListState(ListState.SESSIONS);
        assertCommandSuccess(new ListCommand(ListState.SESSIONS), model, ListCommand.MESSAGE_SUCCESS_SESSIONS,
                expectedModel);
        assertEquals(ListState.SESSIONS, model.getListState());
    }

    @Test
    public void execute_memberFlagInSessionList_switchesToMemberList() {
        model.setListState(ListState.SESSIONS);
        assertCommandSuccess(new ListCommand(ListState.MEMBERS), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
        assertEquals(ListState.MEMBERS, model.getListState());
    }

    @Test
    public void execute_noFlagInSessionList_staysInSessionListAndShowsAllSessions() {
        model = new ModelManager(getTypicalHobbyHubWithSessions(), new UserPrefs());
        expectedModel = new ModelManager(model.getHobbyHub(), new UserPrefs());
        model.setListState(ListState.SESSIONS);
        expectedModel.setListState(ListState.SESSIONS);
        model.updateFilteredSessionList(session -> false);

        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS_SESSIONS, expectedModel);
        assertEquals(getTypicalHobbyHubWithSessions().getSessionList(), model.getFilteredSessionList());
    }

    @Test
    public void constructor_nullListState_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListCommand(null));
    }

    @Test
    public void equals() {
        ListCommand listCurrent = new ListCommand();
        ListCommand listMembers = new ListCommand(ListState.MEMBERS);
        ListCommand listSessions = new ListCommand(ListState.SESSIONS);

        assertTrue(listCurrent.equals(listCurrent));
        assertTrue(listCurrent.equals(new ListCommand()));
        assertTrue(listSessions.equals(new ListCommand(ListState.SESSIONS)));
        assertEquals(listSessions.hashCode(), new ListCommand(ListState.SESSIONS).hashCode());

        assertFalse(listCurrent.equals(null));
        assertFalse(listCurrent.equals(1));
        assertFalse(listCurrent.equals(listMembers)); // keeping the current list is not the same as -m
        assertFalse(listMembers.equals(listSessions));
    }

    @Test
    public void getTargetState() {
        assertEquals(Optional.empty(), new ListCommand().getTargetState());
        assertEquals(Optional.of(ListState.SESSIONS), new ListCommand(ListState.SESSIONS).getTargetState());
    }

    @Test
    public void toStringMethod() {
        String expected = ListCommand.class.getCanonicalName() + "{targetState=SESSIONS}";
        assertEquals(expected, new ListCommand(ListState.SESSIONS).toString());
    }
}
