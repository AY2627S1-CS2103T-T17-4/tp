package seedu.hobbyhub.logic.commands.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.hobbyhub.testutil.TypicalIndexes.INDEX_FIRST_MEMBER;
import static seedu.hobbyhub.testutil.TypicalIndexes.INDEX_SECOND_MEMBER;
import static seedu.hobbyhub.testutil.TypicalSessions.CHESS_MEETUP;
import static seedu.hobbyhub.testutil.TypicalSessions.CYCLING_TRIP;
import static seedu.hobbyhub.testutil.TypicalSessions.getTypicalHobbyHubWithSessions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.model.ListState;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.ModelManager;
import seedu.hobbyhub.model.UserPrefs;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code ViewSessionCommand}.
 */
public class ViewSessionCommandTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalHobbyHubWithSessions(), new UserPrefs());
        model.setListState(ListState.SESSIONS);
    }

    @Test
    public void execute_validIndexUnfilteredList_success() {
        ViewSessionCommand command = new ViewSessionCommand(INDEX_FIRST_MEMBER);

        Model expectedModel = new ModelManager(model.getHobbyHub(), new UserPrefs());
        expectedModel.setListState(ListState.SESSIONS);

        assertCommandSuccess(command, model, ViewSessionCommand.formatSession(CHESS_MEETUP), expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredSessionList().size() + 1);

        assertCommandFailure(new ViewSessionCommand(outOfBoundIndex), model,
                Messages.MESSAGE_INVALID_SESSION_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        model.updateFilteredSessionList(session -> session.equals(CYCLING_TRIP));
        ViewSessionCommand command = new ViewSessionCommand(INDEX_FIRST_MEMBER);

        Model expectedModel = new ModelManager(model.getHobbyHub(), new UserPrefs());
        expectedModel.setListState(ListState.SESSIONS);
        expectedModel.updateFilteredSessionList(session -> session.equals(CYCLING_TRIP));

        assertCommandSuccess(command, model, ViewSessionCommand.formatSession(CYCLING_TRIP), expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        model.updateFilteredSessionList(session -> session.equals(CYCLING_TRIP));

        // second session exists in HobbyHub but is hidden by filter
        assertCommandFailure(new ViewSessionCommand(INDEX_SECOND_MEMBER), model,
                Messages.MESSAGE_INVALID_SESSION_DISPLAYED_INDEX);
    }

    @Test
    public void execute_memberListShowing_throwsCommandException() {
        model.setListState(ListState.MEMBERS);
        assertCommandFailure(new ViewSessionCommand(INDEX_FIRST_MEMBER), model,
                ViewSessionCommand.MESSAGE_WRONG_LIST);
    }

    @Test
    public void formatSession_listsParticipantEmailsSorted() {
        String details = ViewSessionCommand.formatSession(CHESS_MEETUP);
        assertTrue(details.contains("Members (2):\n  - alice@example.com\n  - johnd@example.com"));
    }

    @Test
    public void equals() {
        ViewSessionCommand viewFirst = new ViewSessionCommand(INDEX_FIRST_MEMBER);
        ViewSessionCommand viewSecond = new ViewSessionCommand(INDEX_SECOND_MEMBER);

        // same object -> returns true
        assertTrue(viewFirst.equals(viewFirst));

        // same values -> returns true
        assertTrue(viewFirst.equals(new ViewSessionCommand(INDEX_FIRST_MEMBER)));

        // different types -> returns false
        assertFalse(viewFirst.equals(1));

        // null -> returns false
        assertFalse(viewFirst.equals(null));

        // different index -> returns false
        assertFalse(viewFirst.equals(viewSecond));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        String expected = ViewSessionCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, new ViewSessionCommand(targetIndex).toString());
    }
}
