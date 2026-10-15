package seedu.hobbyhub.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.commons.exceptions.IllegalValueException;
import seedu.hobbyhub.commons.util.JsonUtil;
import seedu.hobbyhub.model.HobbyHub;
import seedu.hobbyhub.testutil.TypicalMembers;

public class JsonSerializableHobbyHubTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableHobbyHubTest");
    private static final Path TYPICAL_MEMBERS_FILE = TEST_DATA_FOLDER.resolve("typicalMembersHobbyHub.json");
    private static final Path INVALID_MEMBER_FILE = TEST_DATA_FOLDER.resolve("invalidMemberHobbyHub.json");
    private static final Path DUPLICATE_MEMBER_FILE = TEST_DATA_FOLDER.resolve("duplicateMemberHobbyHub.json");

    @Test
    public void toModelType_typicalMembersFileWithUnknownHobbyField_success() throws Exception {
        JsonSerializableHobbyHub dataFromFile = JsonUtil.readJsonFile(TYPICAL_MEMBERS_FILE,
                JsonSerializableHobbyHub.class).get();
        HobbyHub hobbyHubFromFile = dataFromFile.toModelType();
        HobbyHub typicalMembersHobbyHub = TypicalMembers.getTypicalHobbyHub();
        assertEquals(hobbyHubFromFile, typicalMembersHobbyHub);
    }

    @Test
    public void toModelType_invalidMemberFile_throwsIllegalValueException() throws Exception {
        JsonSerializableHobbyHub dataFromFile = JsonUtil.readJsonFile(INVALID_MEMBER_FILE,
                JsonSerializableHobbyHub.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateMembers_throwsIllegalValueException() throws Exception {
        JsonSerializableHobbyHub dataFromFile = JsonUtil.readJsonFile(DUPLICATE_MEMBER_FILE,
                JsonSerializableHobbyHub.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableHobbyHub.MESSAGE_DUPLICATE_MEMBER,
                dataFromFile::toModelType);
    }

}
