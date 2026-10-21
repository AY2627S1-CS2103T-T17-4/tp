package seedu.hobbyhub.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.hobbyhub.logic.LogicManager;
import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.logic.parser.HobbyHubParser;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.model.HobbyHub;
import seedu.hobbyhub.model.ListState;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.ModelManager;
import seedu.hobbyhub.model.UserPrefs;
import seedu.hobbyhub.storage.JsonHobbyHubStorage;
import seedu.hobbyhub.storage.JsonUserPrefsStorage;
import seedu.hobbyhub.storage.StorageManager;
import seedu.hobbyhub.testutil.MemberBuilder;
import seedu.hobbyhub.testutil.MemberUtil;
import seedu.hobbyhub.testutil.TypicalSessions;

public class OptionalContactDetailsTest {
    private static final String MINIMAL_ADD = "add -n Alice -c alice@example.com -h Chess:Advanced";
    @TempDir
    public Path directory;

    private final HobbyHubParser parser = new HobbyHubParser();

    @Test
    public void add_withoutPhoneOrAddress_success() throws Exception {
        Model model = new ModelManager();
        var result = parser.parseCommand(MINIMAL_ADD, ListState.MEMBERS).execute(model);
        var member = model.getHobbyHub().getMemberList().getFirst();
        assertTrue(member.getPhone().isEmpty());
        assertTrue(member.getAddress().isEmpty());
        assertTrue(result.getFeedbackToUser().contains("Phone: Not provided"));
        assertTrue(result.getFeedbackToUser().contains("Address: Not provided"));
    }

    @Test
    public void add_withEitherOptionalField_success() throws Exception {
        Model withPhone = new ModelManager();
        parser.parseCommand(MINIMAL_ADD + " -p 91234567", ListState.MEMBERS).execute(withPhone);
        var memberWithPhone = withPhone.getHobbyHub().getMemberList().getFirst();
        assertEquals("91234567", memberWithPhone.getPhone().value);
        assertTrue(memberWithPhone.getAddress().isEmpty());
        Model withAddress = new ModelManager();
        parser.parseCommand(MINIMAL_ADD + " -a Main Street", ListState.MEMBERS).execute(withAddress);
        var memberWithAddress = withAddress.getHobbyHub().getMemberList().getFirst();
        assertTrue(memberWithAddress.getPhone().isEmpty());
        assertEquals("Main Street", memberWithAddress.getAddress().value);
    }

    @Test
    public void add_blankOrMalformedOptionalValues_rejectedWithoutChangingData() {
        Model model = new ModelManager();
        for (String suffix : List.of(" -p", " -p 12", " -p 911a", " -a", " -p 91234567 -p 92345678")) {
            assertThrows(ParseException.class, ()
                -> parser.parseCommand(MINIMAL_ADD + suffix, ListState.MEMBERS).execute(model));
            assertTrue(model.getHobbyHub().getMemberList().isEmpty());
        }
    }

    @Test
    public void edit_fillsMissingFieldsAndPreservesHobbiesGroupsAndSessions() throws Exception {
        Model model = new ModelManager(TypicalSessions.getTypicalHobbyHubWithSessions(), new UserPrefs());
        var original = model.getHobbyHub().getMemberList().getFirst();
        var before = new MemberBuilder(original).withPhone("").withAddress("").build();
        model.setMember(original, before);
        var sessions = List.copyOf(model.getHobbyHub().getSessionList());
        parser.parseCommand("edit 1 -p 91234567", ListState.MEMBERS).execute(model);
        var withPhone = model.getHobbyHub().getMemberList().getFirst();
        assertEquals("91234567", withPhone.getPhone().value);
        assertTrue(withPhone.getAddress().isEmpty());
        parser.parseCommand("edit 1 -a Updated Street", ListState.MEMBERS).execute(model);
        var withAddress = model.getHobbyHub().getMemberList().getFirst();
        assertEquals(withPhone.getPhone(), withAddress.getPhone());
        assertEquals("Updated Street", withAddress.getAddress().value);
        assertEquals(before.getHobbies(), withAddress.getHobbies());
        assertEquals(before.getGroups(), withAddress.getGroups());
        assertEquals(sessions, model.getHobbyHub().getSessionList());
        parser.parseCommand("edit 1 -n Updated Name", ListState.MEMBERS).execute(model);
        assertEquals(withAddress.getPhone(), model.getHobbyHub().getMemberList().getFirst().getPhone());
        assertEquals(withAddress.getAddress(), model.getHobbyHub().getMemberList().getFirst().getAddress());
    }

    @Test
    public void edit_blankOrInvalidValues_doNotChangeData() {
        Model model = new ModelManager(TypicalSessions.getTypicalHobbyHubWithSessions(), new UserPrefs());
        HobbyHub before = new HobbyHub(model.getHobbyHub());
        for (String command : List.of("edit 1 -p", "edit 1 -a", "edit 1 -p -a",
            "edit 1 -a Updated Street -p invalid")) {
            assertThrows(ParseException.class, ()
                -> parser.parseCommand(command, ListState.MEMBERS).execute(model));
            assertEquals(before, model.getHobbyHub());
        }
    }

    @Test
    public void omittedFields_autoSaveAndReloadAcrossSessions() throws Exception {
        Model model = new ModelManager(TypicalSessions.getTypicalHobbyHubWithSessions(), new UserPrefs());
        var original = model.getHobbyHub().getMemberList().getFirst();
        model.setMember(original, new MemberBuilder(original).withPhone("").withAddress("").build());
        var storage = new JsonHobbyHubStorage(directory.resolve("hobbyhub.json"));
        var logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(directory.resolve("preferences.json"))));
        logic.execute("edit 1 -n Updated Name");
        assertEquals(model.getHobbyHub(), storage.readHobbyHub().orElseThrow());
        assertTrue(storage.readHobbyHub().orElseThrow().getMemberList().getFirst().getPhone().isEmpty());
        assertTrue(storage.readHobbyHub().orElseThrow().getMemberList().getFirst().getAddress().isEmpty());
    }

    @Test
    public void generatedAddCommand_omitsAbsentFieldsAndRoundTrips() throws Exception {
        var member = new MemberBuilder().withPhone("").withAddress("").build();
        String command = MemberUtil.getAddCommand(member);
        assertFalse(command.contains(" -p"));
        assertFalse(command.contains(" -a"));
        assertEquals(new AddCommand(member), parser.parseCommand(command, ListState.MEMBERS));
        assertTrue(Messages.format(member).contains("Not provided"));
    }
}
