package seedu.hobbyhub.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.testutil.TypicalMembers.ALICE;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.logic.commands.exceptions.CommandException;
import seedu.hobbyhub.model.HobbyHub;
import seedu.hobbyhub.model.ListState;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.ModelManager;
import seedu.hobbyhub.model.UserPrefs;
import seedu.hobbyhub.model.session.Session;
import seedu.hobbyhub.model.session.SessionName;
import seedu.hobbyhub.testutil.SessionBuilder;

/** Tests the {@code assign} command. */
public class AssignCommandTest {

    private static final SessionName SESSION_NAME = new SessionName("Weekend Chess Meetup");

    private Model model;
    private Session session;

    @BeforeEach
    public void setUp() {
        HobbyHub hobbyHub = new HobbyHub();
        hobbyHub.addMember(ALICE);
        session = new SessionBuilder().withName(SESSION_NAME.fullName).build();
        hobbyHub.addSession(session);
        model = new ModelManager(hobbyHub, new UserPrefs());
    }

    @Test
    public void execute_validMemberAndSession_assignsBothSides() throws Exception {
        AssignCommand command = new AssignCommand(Index.fromOneBased(1), SESSION_NAME);

        CommandResult result = command.execute(model);

        Session assignedSession = model.getHobbyHub().getSessionList().get(0);
        assertEquals(String.format(AssignCommand.MESSAGE_ASSIGN_SUCCESS, ALICE.getName(), SESSION_NAME),
                result.getFeedbackToUser());
        assertTrue(assignedSession.hasMember(ALICE.getEmail()));
        assertEquals(List.of(assignedSession), model.getFilteredMemberList().get(0).getSessions());
    }

    @Test
    public void execute_wrongListState_throwsCommandException() {
        model.setListState(ListState.SESSIONS);
        AssignCommand command = new AssignCommand(Index.fromOneBased(1), SESSION_NAME);

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(AssignCommand.MESSAGE_MEMBER_LIST_REQUIRED, exception.getMessage());
    }

    @Test
    public void execute_invalidMemberIndex_throwsCommandException() {
        AssignCommand command = new AssignCommand(Index.fromOneBased(2), SESSION_NAME);

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(Messages.MESSAGE_INVALID_MEMBER_DISPLAYED_INDEX, exception.getMessage());
    }

    @Test
    public void execute_unknownSession_throwsCommandException() {
        AssignCommand command = new AssignCommand(Index.fromOneBased(1), new SessionName("Unknown Session"));

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(String.format(AssignCommand.MESSAGE_SESSION_NOT_FOUND, "Unknown Session"),
                exception.getMessage());
    }

    @Test
    public void execute_alreadyAssigned_throwsCommandException() {
        Session assignedSession = session.withMember(ALICE.getEmail());
        model.setSession(session, assignedSession);
        AssignCommand command = new AssignCommand(Index.fromOneBased(1), SESSION_NAME);

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(String.format(AssignCommand.MESSAGE_ALREADY_ASSIGNED, SESSION_NAME), exception.getMessage());
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        AssignCommand command = new AssignCommand(Index.fromOneBased(1), SESSION_NAME);

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void constructor_nullMemberIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AssignCommand(null, SESSION_NAME));
    }

    @Test
    public void constructor_nullSessionName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AssignCommand(Index.fromOneBased(1), null));
    }

    @Test
    public void equals() {
        AssignCommand command = new AssignCommand(Index.fromOneBased(1), SESSION_NAME);
        AssignCommand commandCopy = new AssignCommand(Index.fromOneBased(1), SESSION_NAME);

        assertTrue(command.equals(command));
        assertTrue(command.equals(commandCopy));
        assertEquals(command.hashCode(), commandCopy.hashCode());
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
        assertFalse(command.equals(new AssignCommand(Index.fromOneBased(2), SESSION_NAME)));
        assertFalse(command.equals(new AssignCommand(Index.fromOneBased(1), new SessionName("Different Session"))));
    }

    @Test
    public void toStringMethod() {
        AssignCommand command = new AssignCommand(Index.fromOneBased(1), SESSION_NAME);
        String expected = AssignCommand.class.getCanonicalName() + "{memberIndex=" + Index.fromOneBased(1)
                + ", sessionName=" + SESSION_NAME + "}";

        assertEquals(expected, command.toString());
    }
}
