package seedu.hobbyhub.logic.commands.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.logic.commands.session.EditSessionCommand.MESSAGE_NOT_EDITED;
import static seedu.hobbyhub.logic.commands.session.EditSessionCommand.MESSAGE_WRONG_LIST;
import static seedu.hobbyhub.testutil.Assert.assertThrows;
import static seedu.hobbyhub.testutil.TypicalSessions.CHESS_MEETUP;

import java.time.LocalDateTime;

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
import seedu.hobbyhub.testutil.SessionBuilder;

public class EditSessionCommandTest {

    private static final LocalDateTime NEW_DATE_TIME = LocalDateTime.parse("2026-11-01T15:30");
    private static final Session COMPLETED_SESSION = new SessionBuilder(CHESS_MEETUP)
            .withCompleted(true)
            .build();

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EditSessionCommand(null, "New Location", null));
    }

    @Test
    public void constructor_noFields_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, MESSAGE_NOT_EDITED, () ->
                new EditSessionCommand(Index.fromOneBased(1), null, null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        EditSessionCommand command = new EditSessionCommand(Index.fromOneBased(1), "New Location", null);

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_memberList_throwsCommandException() {
        ModelManager model = createModelWithSession();
        EditSessionCommand command = new EditSessionCommand(Index.fromOneBased(1), "New Location", null);

        assertThrows(CommandException.class, MESSAGE_WRONG_LIST, () -> command.execute(model));
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        ModelManager model = createModelWithSession();
        model.setListState(ListState.SESSIONS);
        EditSessionCommand command = new EditSessionCommand(Index.fromOneBased(2), "New Location", null);

        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_SESSION_DISPLAYED_INDEX, () ->
                command.execute(model));
    }

    @Test
    public void execute_locationOnly_updatesLocationAndPreservesOtherFields() throws Exception {
        ModelManager model = createModelWithSession();
        model.setListState(ListState.SESSIONS);
        EditSessionCommand command = new EditSessionCommand(Index.fromOneBased(1), "New Location", null);

        CommandResult result = command.execute(model);

        Session expectedSession = new SessionBuilder(COMPLETED_SESSION).withLocation("New Location").build();
        assertEquals(String.format(EditSessionCommand.MESSAGE_SUCCESS, expectedSession.getName()),
                result.getFeedbackToUser());
        assertEquals(expectedSession, model.getHobbyHub().getSessionList().getFirst());
        assertEquals(1, model.getFilteredSessionList().size());
    }

    @Test
    public void execute_dateTimeOnly_updatesDateTimeAndPreservesOtherFields() throws Exception {
        ModelManager model = createModelWithSession();
        model.setListState(ListState.SESSIONS);
        EditSessionCommand command = new EditSessionCommand(Index.fromOneBased(1), null, NEW_DATE_TIME);

        command.execute(model);

        Session expectedSession = new SessionBuilder(COMPLETED_SESSION)
                .withDateTime(NEW_DATE_TIME.toString())
                .build();
        assertEquals(expectedSession, model.getHobbyHub().getSessionList().getFirst());
    }

    @Test
    public void execute_bothFields_updatesLocationAndDateTime() throws Exception {
        ModelManager model = createModelWithSession();
        model.setListState(ListState.SESSIONS);
        EditSessionCommand command = new EditSessionCommand(Index.fromOneBased(1), "New Location", NEW_DATE_TIME);

        command.execute(model);

        Session expectedSession = new SessionBuilder(COMPLETED_SESSION)
                .withLocation("New Location")
                .withDateTime(NEW_DATE_TIME.toString())
                .build();
        assertEquals(expectedSession, model.getHobbyHub().getSessionList().getFirst());
    }

    @Test
    public void execute_usesIndexFromFilteredSessionList() throws Exception {
        HobbyHub hobbyHub = new HobbyHub();
        hobbyHub.addSession(CHESS_MEETUP);
        hobbyHub.addSession(new SessionBuilder().withName("Running Meetup").build());
        ModelManager model = new ModelManager(hobbyHub, new UserPrefs());
        model.setListState(ListState.SESSIONS);
        model.updateFilteredSessionList(CHESS_MEETUP::equals);
        EditSessionCommand command = new EditSessionCommand(Index.fromOneBased(1), "New Location", null);

        command.execute(model);

        assertEquals("New Location", model.getHobbyHub().getSessionList().getFirst().getLocation());
        assertEquals(2, model.getFilteredSessionList().size());
    }

    @Test
    public void equals() {
        EditSessionCommand original = new EditSessionCommand(Index.fromOneBased(1), "New Location", NEW_DATE_TIME);
        EditSessionCommand equivalent = new EditSessionCommand(Index.fromOneBased(1), "New Location", NEW_DATE_TIME);

        assertTrue(original.equals(original));
        assertTrue(original.equals(equivalent));
        assertFalse(original.equals(null));
        assertFalse(original.equals(1));
        assertFalse(original.equals(new EditSessionCommand(Index.fromOneBased(2), "New Location", NEW_DATE_TIME)));
        assertFalse(original.equals(new EditSessionCommand(Index.fromOneBased(1), "Other Location", NEW_DATE_TIME)));
        assertFalse(original.equals(new EditSessionCommand(Index.fromOneBased(1), "New Location",
                NEW_DATE_TIME.plusDays(1))));
    }

    @Test
    public void toStringMethod() {
        EditSessionCommand command = new EditSessionCommand(Index.fromOneBased(1), "New Location", NEW_DATE_TIME);

        assertEquals(EditSessionCommand.class.getCanonicalName() + "{index=" + Index.fromOneBased(1)
                + ", location=New Location, dateTime=" + NEW_DATE_TIME + "}", command.toString());
    }

    private static ModelManager createModelWithSession() {
        HobbyHub hobbyHub = new HobbyHub();
        hobbyHub.addSession(COMPLETED_SESSION);
        return new ModelManager(hobbyHub, new UserPrefs());
    }

}
