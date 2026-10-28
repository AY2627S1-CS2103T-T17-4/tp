package seedu.hobbyhub.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_MEMBER_DISPLAYED_INDEX;
import static seedu.hobbyhub.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.HOBBY_DESC_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.hobbyhub.logic.commands.AddCommand;
import seedu.hobbyhub.logic.commands.CommandResult;
import seedu.hobbyhub.logic.commands.ListCommand;
import seedu.hobbyhub.logic.commands.exceptions.CommandException;
import seedu.hobbyhub.logic.commands.session.AddSessionCommand;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.model.ListState;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.ModelManager;
import seedu.hobbyhub.model.ReadOnlyHobbyHub;
import seedu.hobbyhub.model.UserPrefs;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.storage.JsonHobbyHubStorage;
import seedu.hobbyhub.storage.JsonUserPrefsStorage;
import seedu.hobbyhub.storage.StorageManager;
import seedu.hobbyhub.testutil.MemberBuilder;
import seedu.hobbyhub.testutil.SessionBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonHobbyHubStorage hobbyHubStorage =
                new JsonHobbyHubStorage(temporaryFolder.resolve("hobbyHub.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(hobbyHubStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_MEMBER_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_listSessions_switchesListAndRoutesCommands() throws Exception {
        logic.execute(ListCommand.COMMAND_WORD + " " + ListCommand.FLAG_SESSIONS);
        assertEquals(ListState.SESSIONS, logic.getListState());

        // add now goes to the session parser
        CommandResult result = logic.execute(AddSessionCommand.COMMAND_WORD
                + " -n Golf Meet -l Golf Club -t 2026-10-24T14:00");
        assertEquals(String.format(AddSessionCommand.MESSAGE_SUCCESS, "Golf Meet"), result.getFeedbackToUser());
        assertEquals(1, logic.getFilteredSessionList().size());
        assertEquals("Golf Meet", logic.getFilteredSessionList().get(0).getName().fullName);

        logic.execute(ListCommand.COMMAND_WORD + " " + ListCommand.FLAG_MEMBERS);
        assertEquals(ListState.MEMBERS, logic.getListState());
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getFilteredMemberList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredMemberList().remove(0));
    }

    @Test
    public void getFilteredSessionList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredSessionList().remove(0));
    }

    @Test
    public void getMemberAndSessionLists_returnsAllRecords() {
        Member member = new MemberBuilder().build();
        model.addMember(member);
        model.addSession(new SessionBuilder().withMembers(member.getEmail().value).build());

        assertEquals(model.getHobbyHub().getMemberList(), logic.getMemberList());
        assertEquals(model.getHobbyHub().getSessionList(), logic.getSessionList());
    }

    @Test
    public void getListState_initially_isMembers() {
        assertEquals(ListState.MEMBERS, logic.getListState());
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getHobbyHub(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonHobbyHubStorage that throws the IOException e when saving
        JsonHobbyHubStorage hobbyHubStorage = new JsonHobbyHubStorage(prefPath) {
            @Override
            public void saveHobbyHub(ReadOnlyHobbyHub hobbyHub) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(hobbyHubStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveHobbyHub method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + HOBBY_DESC_AMY;
        ModelManager expectedModel = new ModelManager();
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }
}
