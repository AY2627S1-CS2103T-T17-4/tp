package seedu.hobbyhub.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.INVALID_GROUP_DESC;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

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
import seedu.hobbyhub.model.group.Group;
import seedu.hobbyhub.model.member.Address;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.member.Phone;
import seedu.hobbyhub.storage.JsonHobbyHubStorage;
import seedu.hobbyhub.storage.JsonUserPrefsStorage;
import seedu.hobbyhub.storage.StorageManager;
import seedu.hobbyhub.testutil.MemberBuilder;
import seedu.hobbyhub.testutil.MemberUtil;
import seedu.hobbyhub.testutil.TypicalSessions;

/**
 * Tests that the optional member fields (phone, address and groups) can be left out, filled in, replaced and
 * cleared, and that invalid input never changes any data.
 */
public class OptionalContactDetailsTest {
    private static final String MINIMAL_ADD = "add -n Alice -c alice@example.com -h Chess:Advanced";
    @TempDir
    public Path directory;

    private final HobbyHubParser parser = new HobbyHubParser();

    /**
     * Returns a model whose first member has a phone, an address and a group, so that clearing each of them is
     * observable.
     */
    private Model createModelWithFilledFirstMember() {
        Model model = new ModelManager(TypicalSessions.getTypicalHobbyHubWithSessions(), new UserPrefs());
        Member original = model.getHobbyHub().getMemberList().getFirst();
        Member filled = new Member(original.getName(), new Phone("91234567"), original.getEmail(),
                new Address("Main Street"), original.getHobbies(), Set.of(new Group("friends")),
                original.getSessions());
        model.setMember(original, filled);
        return model;
    }

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
    public void edit_blankPhone_clearsPhoneOnly() throws Exception {
        Model model = createModelWithFilledFirstMember();
        Member before = model.getHobbyHub().getMemberList().getFirst();

        parser.parseCommand("edit 1 -p", ListState.MEMBERS).execute(model);

        Member after = model.getHobbyHub().getMemberList().getFirst();
        assertTrue(after.getPhone().isEmpty());
        assertEquals(before.getAddress(), after.getAddress());
        assertEquals(before.getGroups(), after.getGroups());
        assertEquals(before.getHobbies(), after.getHobbies());
        assertEquals(before.getSessions(), after.getSessions());
    }

    @Test
    public void edit_blankAddress_clearsAddressOnly() throws Exception {
        Model model = createModelWithFilledFirstMember();
        Member before = model.getHobbyHub().getMemberList().getFirst();

        parser.parseCommand("edit 1 -a", ListState.MEMBERS).execute(model);

        Member after = model.getHobbyHub().getMemberList().getFirst();
        assertTrue(after.getAddress().isEmpty());
        assertEquals(before.getPhone(), after.getPhone());
        assertEquals(before.getGroups(), after.getGroups());
        assertEquals(before.getHobbies(), after.getHobbies());
        assertEquals(before.getSessions(), after.getSessions());
    }

    @Test
    public void edit_blankPhoneAndAddress_clearsBoth() throws Exception {
        Model model = createModelWithFilledFirstMember();
        Member before = model.getHobbyHub().getMemberList().getFirst();

        parser.parseCommand("edit 1 -p -a", ListState.MEMBERS).execute(model);

        Member after = model.getHobbyHub().getMemberList().getFirst();
        assertTrue(after.getPhone().isEmpty());
        assertTrue(after.getAddress().isEmpty());
        assertEquals(before.getGroups(), after.getGroups());
        assertEquals(before.getName(), after.getName());
    }

    @Test
    public void edit_blankGroup_clearsGroupsOnly() throws Exception {
        Model model = createModelWithFilledFirstMember();
        Member before = model.getHobbyHub().getMemberList().getFirst();

        parser.parseCommand("edit 1 -g", ListState.MEMBERS).execute(model);

        Member after = model.getHobbyHub().getMemberList().getFirst();
        assertTrue(after.getGroups().isEmpty());
        assertEquals(before.getPhone(), after.getPhone());
        assertEquals(before.getAddress(), after.getAddress());
    }

    @Test
    public void edit_blankPhoneAndNewGroup_clearsPhoneAndReplacesAllGroups() throws Exception {
        Model model = createModelWithFilledFirstMember();
        Member before = model.getHobbyHub().getMemberList().getFirst();

        parser.parseCommand("edit 1 -p -g pool-2627", ListState.MEMBERS).execute(model);

        Member after = model.getHobbyHub().getMemberList().getFirst();
        assertTrue(after.getPhone().isEmpty());
        assertEquals(Set.of(new Group("pool-2627")), after.getGroups());
        assertEquals(before.getAddress(), after.getAddress());
    }

    @Test
    public void edit_invalidValues_doNotChangeData() {
        Model model = createModelWithFilledFirstMember();
        HobbyHub before = new HobbyHub(model.getHobbyHub());
        for (String command : List.of(
                // invalid phone
                "edit 1" + INVALID_PHONE_DESC, "edit 1 -p 12", "edit 1 -p 911a",
                "edit 1 -a Updated Street -p invalid",
                // invalid group, or a blank group among other groups
                "edit 1" + INVALID_GROUP_DESC, "edit 1 -g friends -g",
                // a blank value must not be applied when another value in the same command is invalid
                "edit 1 -p" + INVALID_GROUP_DESC,
                // repeated single-valued option
                "edit 1 -p -a Updated Street -p 91234567",
                // required fields cannot be cleared
                "edit 1 -n", "edit 1 -c", "edit 1 -h")) {
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
    public void clearedFields_autoSaveAndReloadAcrossSessions() throws Exception {
        Model model = createModelWithFilledFirstMember();
        var storage = new JsonHobbyHubStorage(directory.resolve("hobbyhub.json"));
        var logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(directory.resolve("preferences.json"))));

        logic.execute("edit 1 -p -a -g");

        Member reloaded = storage.readHobbyHub().orElseThrow().getMemberList().getFirst();
        assertTrue(reloaded.getPhone().isEmpty());
        assertTrue(reloaded.getAddress().isEmpty());
        assertTrue(reloaded.getGroups().isEmpty());
        assertEquals(model.getHobbyHub(), storage.readHobbyHub().orElseThrow());
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
