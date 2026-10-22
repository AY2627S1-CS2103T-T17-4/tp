package seedu.hobbyhub.logic.commands.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.testutil.Assert.assertThrows;
import static seedu.hobbyhub.testutil.TypicalSessions.CHESS_MEETUP;
import static seedu.hobbyhub.testutil.TypicalSessions.CYCLING_TRIP;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.logic.commands.CommandResult;
import seedu.hobbyhub.logic.commands.exceptions.CommandException;
import seedu.hobbyhub.model.HobbyHub;
import seedu.hobbyhub.model.ListState;
import seedu.hobbyhub.model.ModelManager;
import seedu.hobbyhub.model.UserPrefs;
import seedu.hobbyhub.model.session.Session;

public class MarkSessionCommandTest {

    private static final Index FIRST = Index.fromOneBased(1);
    private static final Index SECOND = Index.fromOneBased(2);
    private static final Session COMPLETED_CHESS_MEETUP = CHESS_MEETUP.withCompleted(true);

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MarkSessionCommand(null, true));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MarkSessionCommand(FIRST, true).execute(null));
    }

    @Test
    public void execute_memberList_throwsCommandException() {
        ModelManager model = createModelWith(CHESS_MEETUP);
        model.setListState(ListState.MEMBERS);

        assertThrows(CommandException.class, String.format(Messages.MESSAGE_SESSION_LIST_REQUIRED, "mark"), () ->
                new MarkSessionCommand(FIRST, true).execute(model));
        assertThrows(CommandException.class, String.format(Messages.MESSAGE_SESSION_LIST_REQUIRED, "unmark"), () ->
                new MarkSessionCommand(FIRST, false).execute(model));
    }

    @Test
    public void execute_mark_marksSessionAsCompleted() throws Exception {
        ModelManager model = createModelWith(CHESS_MEETUP);

        CommandResult result = new MarkSessionCommand(FIRST, true).execute(model);

        assertEquals(String.format(MarkSessionCommand.MESSAGE_MARK_SUCCESS, CHESS_MEETUP.getName()),
                result.getFeedbackToUser());
        assertEquals(COMPLETED_CHESS_MEETUP, model.getFilteredSessionList().getFirst());
    }

    @Test
    public void execute_unmark_marksSessionAsNotCompleted() throws Exception {
        ModelManager model = createModelWith(COMPLETED_CHESS_MEETUP);

        CommandResult result = new MarkSessionCommand(FIRST, false).execute(model);

        assertEquals(String.format(MarkSessionCommand.MESSAGE_UNMARK_SUCCESS, CHESS_MEETUP.getName()),
                result.getFeedbackToUser());
        assertEquals(CHESS_MEETUP, model.getFilteredSessionList().getFirst());
    }

    @Test
    public void execute_markCompletedSession_throwsCommandException() {
        ModelManager model = createModelWith(COMPLETED_CHESS_MEETUP);

        assertThrows(CommandException.class,
                String.format(MarkSessionCommand.MESSAGE_ALREADY_COMPLETED, CHESS_MEETUP.getName()), () ->
                        new MarkSessionCommand(FIRST, true).execute(model));
    }

    @Test
    public void execute_unmarkNotCompletedSession_throwsCommandException() {
        ModelManager model = createModelWith(CHESS_MEETUP);

        assertThrows(CommandException.class,
                String.format(MarkSessionCommand.MESSAGE_ALREADY_NOT_COMPLETED, CHESS_MEETUP.getName()), () ->
                        new MarkSessionCommand(FIRST, false).execute(model));
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        ModelManager model = createModelWith(CHESS_MEETUP);

        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_SESSION_DISPLAYED_INDEX, () ->
                new MarkSessionCommand(SECOND, true).execute(model));
    }

    @Test
    public void execute_filteredList_marksSessionAtDisplayedIndex() throws Exception {
        ModelManager model = createModelWith(CHESS_MEETUP, CYCLING_TRIP);
        model.updateFilteredSessionList(CYCLING_TRIP::isSameSession);

        new MarkSessionCommand(FIRST, true).execute(model);

        assertEquals(CHESS_MEETUP, model.getHobbyHub().getSessionList().get(0));
        assertTrue(model.getHobbyHub().getSessionList().get(1).isCompleted());
    }

    @Test
    public void execute_mark_keepsMembersAssigned() throws Exception {
        ModelManager model = createModelWith(CHESS_MEETUP);

        new MarkSessionCommand(FIRST, true).execute(model);

        assertEquals(CHESS_MEETUP.getMembers(), model.getFilteredSessionList().getFirst().getMembers());
    }

    @Test
    public void equals() {
        MarkSessionCommand markFirst = new MarkSessionCommand(FIRST, true);

        assertTrue(markFirst.equals(markFirst));
        assertTrue(markFirst.equals(new MarkSessionCommand(FIRST, true)));
        assertEquals(markFirst.hashCode(), new MarkSessionCommand(FIRST, true).hashCode());

        assertFalse(markFirst.equals(null));
        assertFalse(markFirst.equals(1));
        assertFalse(markFirst.equals(new MarkSessionCommand(SECOND, true)));
        assertFalse(markFirst.equals(new MarkSessionCommand(FIRST, false)));
        assertNotEquals(markFirst.hashCode(), new MarkSessionCommand(FIRST, false).hashCode());
    }

    @Test
    public void toStringMethod() {
        MarkSessionCommand command = new MarkSessionCommand(FIRST, true);

        assertEquals(MarkSessionCommand.class.getCanonicalName() + "{index=" + FIRST + ", isCompleted=true}",
                command.toString());
    }

    private static ModelManager createModelWith(Session... sessions) {
        HobbyHub hobbyHub = new HobbyHub();
        for (Session session : sessions) {
            hobbyHub.addSession(session);
        }
        ModelManager model = new ModelManager(hobbyHub, new UserPrefs());
        model.setListState(ListState.SESSIONS);
        return model;
    }
}
