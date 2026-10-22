package seedu.hobbyhub.logic.commands.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.logic.commands.session.DeleteSessionCommand.MESSAGE_WRONG_LIST;
import static seedu.hobbyhub.testutil.Assert.assertThrows;
import static seedu.hobbyhub.testutil.TypicalMembers.ALICE;
import static seedu.hobbyhub.testutil.TypicalMembers.BENSON;
import static seedu.hobbyhub.testutil.TypicalSessions.CHESS_MEETUP;
import static seedu.hobbyhub.testutil.TypicalSessions.CYCLING_TRIP;
import static seedu.hobbyhub.testutil.TypicalSessions.getTypicalHobbyHubWithSessions;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.logic.commands.CommandResult;
import seedu.hobbyhub.logic.commands.exceptions.CommandException;
import seedu.hobbyhub.model.ListState;
import seedu.hobbyhub.model.ModelManager;
import seedu.hobbyhub.model.UserPrefs;

public class DeleteSessionCommandTest {

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteSessionCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        DeleteSessionCommand command = new DeleteSessionCommand(Index.fromOneBased(1));

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_memberList_throwsCommandException() {
        DeleteSessionCommand command = new DeleteSessionCommand(Index.fromOneBased(1));
        ModelManager model = createModelWithSessions();

        assertThrows(CommandException.class, MESSAGE_WRONG_LIST, () -> command.execute(model));
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        DeleteSessionCommand command = new DeleteSessionCommand(Index.fromOneBased(3));
        ModelManager model = createModelWithSessions();
        model.setListState(ListState.SESSIONS);

        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_SESSION_DISPLAYED_INDEX, () ->
                command.execute(model));
    }

    @Test
    public void execute_validIndex_deletesSessionAndRefreshesMemberSessions() throws Exception {
        ModelManager model = createModelWithSessions();
        model.setListState(ListState.SESSIONS);
        model.updateFilteredSessionList(CHESS_MEETUP::equals);
        DeleteSessionCommand command = new DeleteSessionCommand(Index.fromOneBased(1));

        CommandResult result = command.execute(model);

        assertEquals(String.format(DeleteSessionCommand.MESSAGE_SUCCESS, CHESS_MEETUP.getName()),
                result.getFeedbackToUser());
        assertEquals(List.of(CYCLING_TRIP), model.getHobbyHub().getSessionList());
        assertEquals(List.of(), model.getSessionsOf(ALICE));
        assertEquals(List.of(CYCLING_TRIP), model.getSessionsOf(BENSON));
        assertTrue(model.getFilteredSessionList().isEmpty());
    }

    @Test
    public void equals() {
        DeleteSessionCommand command = new DeleteSessionCommand(Index.fromOneBased(1));

        assertTrue(command.equals(command));
        assertTrue(command.equals(new DeleteSessionCommand(Index.fromOneBased(1))));
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
        assertFalse(command.equals(new DeleteSessionCommand(Index.fromOneBased(2))));
    }

    @Test
    public void toStringMethod() {
        DeleteSessionCommand command = new DeleteSessionCommand(Index.fromOneBased(1));

        assertEquals(DeleteSessionCommand.class.getCanonicalName() + "{targetIndex=" + Index.fromOneBased(1) + "}",
                command.toString());
    }

    private static ModelManager createModelWithSessions() {
        return new ModelManager(getTypicalHobbyHubWithSessions(), new UserPrefs());
    }
}
