package seedu.hobbyhub.logic.commands.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.logic.commands.session.AddSessionCommand.MESSAGE_DUPLICATE_SESSION;
import static seedu.hobbyhub.logic.commands.session.AddSessionCommand.MESSAGE_WRONG_LIST;
import static seedu.hobbyhub.testutil.Assert.assertThrows;
import static seedu.hobbyhub.testutil.TypicalSessions.CHESS_MEETUP;
import static seedu.hobbyhub.testutil.TypicalSessions.getTypicalHobbyHubWithSessions;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.logic.commands.CommandResult;
import seedu.hobbyhub.logic.commands.exceptions.CommandException;
import seedu.hobbyhub.model.ListState;
import seedu.hobbyhub.model.ModelManager;
import seedu.hobbyhub.model.UserPrefs;
import seedu.hobbyhub.model.session.Session;
import seedu.hobbyhub.testutil.SessionBuilder;

public class AddSessionCommandTest {

    @Test
    public void constructor_nullSession_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddSessionCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        AddSessionCommand command = new AddSessionCommand(createSession("Golf Meetup"));

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_memberList_throwsCommandException() {
        AddSessionCommand command = new AddSessionCommand(createSession("Golf Meetup"));
        ModelManager model = new ModelManager();

        assertThrows(CommandException.class, MESSAGE_WRONG_LIST, () -> command.execute(model));
    }

    @Test
    public void execute_duplicateSession_throwsCommandException() {
        ModelManager model = new ModelManager(getTypicalHobbyHubWithSessions(), new UserPrefs());
        model.setListState(ListState.SESSIONS);
        AddSessionCommand command = new AddSessionCommand(CHESS_MEETUP);

        assertThrows(CommandException.class, MESSAGE_DUPLICATE_SESSION, () -> command.execute(model));
    }

    @Test
    public void execute_newSession_addsSessionAndReturnsSuccess() throws Exception {
        ModelManager model = new ModelManager();
        model.setListState(ListState.SESSIONS);
        Session session = createSession("Golf Meetup");

        CommandResult result = new AddSessionCommand(session).execute(model);

        assertEquals(String.format(AddSessionCommand.MESSAGE_SUCCESS, session.getName()),
                result.getFeedbackToUser());
        assertEquals(1, model.getFilteredSessionList().size());
        assertEquals(session, model.getFilteredSessionList().get(0));
    }

    @Test
    public void equals() {
        Session golfMeetup = createSession("Golf Meetup");
        AddSessionCommand addGolfMeetupCommand = new AddSessionCommand(golfMeetup);
        AddSessionCommand addAnotherGolfMeetupCommand = new AddSessionCommand(createSession("Golf Meetup"));
        AddSessionCommand addChessMeetupCommand = new AddSessionCommand(createSession("Chess Meetup"));

        assertTrue(addGolfMeetupCommand.equals(addGolfMeetupCommand));
        assertTrue(addGolfMeetupCommand.equals(addAnotherGolfMeetupCommand));
        assertFalse(addGolfMeetupCommand.equals(null));
        assertFalse(addGolfMeetupCommand.equals(1));
        assertFalse(addGolfMeetupCommand.equals(addChessMeetupCommand));
    }

    @Test
    public void toStringMethod() {
        Session session = createSession("Golf Meetup");
        AddSessionCommand command = new AddSessionCommand(session);

        assertEquals(AddSessionCommand.class.getCanonicalName() + "{toAdd=" + session + "}", command.toString());
    }

    private static Session createSession(String name) {
        return new SessionBuilder().withName(name).withDateTime("2026-10-24T14:00")
                .withLocation("Golf Club").withMembers().build();
    }
}
