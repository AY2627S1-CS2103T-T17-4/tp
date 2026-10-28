package seedu.hobbyhub.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.hobbyhub.testutil.Assert.assertThrows;
import static seedu.hobbyhub.testutil.TypicalMembers.ALICE;
import static seedu.hobbyhub.testutil.TypicalMembers.BENSON;
import static seedu.hobbyhub.testutil.TypicalSessions.CHESS_MEETUP;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.commons.exceptions.IllegalValueException;
import seedu.hobbyhub.commons.util.JsonUtil;
import seedu.hobbyhub.model.HobbyHub;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.testutil.HobbyHubBuilder;
import seedu.hobbyhub.testutil.SessionBuilder;
import seedu.hobbyhub.testutil.TypicalMembers;
import seedu.hobbyhub.testutil.TypicalSessions;

public class JsonSerializableHobbyHubTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableHobbyHubTest");
    private static final Path TYPICAL_MEMBERS_FILE = TEST_DATA_FOLDER.resolve("typicalMembersHobbyHub.json");
    private static final Path INVALID_MEMBER_FILE = TEST_DATA_FOLDER.resolve("invalidMemberHobbyHub.json");
    private static final Path DUPLICATE_MEMBER_FILE = TEST_DATA_FOLDER.resolve("duplicateMemberHobbyHub.json");
    private static final Path DUPLICATE_HOBBY_MEMBER_FILE =
            TEST_DATA_FOLDER.resolve("duplicateHobbyMemberHobbyHub.json");
    private static final Path TYPICAL_SESSIONS_FILE = TEST_DATA_FOLDER.resolve("typicalSessionsHobbyHub.json");
    private static final Path DUPLICATE_SESSION_FILE = TEST_DATA_FOLDER.resolve("duplicateSessionHobbyHub.json");
    private static final Path SESSION_WITH_UNKNOWN_MEMBER_FILE =
            TEST_DATA_FOLDER.resolve("sessionWithUnknownMemberHobbyHub.json");
    private static final Path SESSION_WITH_MIXED_CASE_EMAIL_FILE =
            TEST_DATA_FOLDER.resolve("sessionWithMixedCaseEmailHobbyHub.json");

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

    @Test
    public void toModelType_memberWithHobbiesDifferingOnlyByCaseAndSpace_throwsIllegalValueException()
            throws Exception {
        JsonSerializableHobbyHub dataFromFile = JsonUtil.readJsonFile(DUPLICATE_HOBBY_MEMBER_FILE,
                JsonSerializableHobbyHub.class).get();
        assertThrows(IllegalValueException.class, Member.MESSAGE_DUPLICATE_HOBBY, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_typicalSessionsFile_success() throws Exception {
        JsonSerializableHobbyHub dataFromFile = JsonUtil.readJsonFile(TYPICAL_SESSIONS_FILE,
                JsonSerializableHobbyHub.class).get();
        HobbyHub hobbyHubFromFile = dataFromFile.toModelType();
        HobbyHub typicalSessionsHobbyHub = TypicalSessions.getTypicalHobbyHubWithSessions();
        assertEquals(typicalSessionsHobbyHub, hobbyHubFromFile);
    }

    @Test
    public void toModelType_fileWithoutSessions_loadsWithNoSessions() throws Exception {
        // Data files from older versions have no sessions list
        JsonSerializableHobbyHub dataFromFile = JsonUtil.readJsonFile(TYPICAL_MEMBERS_FILE,
                JsonSerializableHobbyHub.class).get();
        assertEquals(0, dataFromFile.toModelType().getSessionList().size());
    }

    @Test
    public void toModelType_duplicateSessions_throwsIllegalValueException() throws Exception {
        JsonSerializableHobbyHub dataFromFile = JsonUtil.readJsonFile(DUPLICATE_SESSION_FILE,
                JsonSerializableHobbyHub.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableHobbyHub.MESSAGE_DUPLICATE_SESSION,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_sessionWithUnknownMember_unknownMemberDropped() throws Exception {
        JsonSerializableHobbyHub dataFromFile = JsonUtil.readJsonFile(SESSION_WITH_UNKNOWN_MEMBER_FILE,
                JsonSerializableHobbyHub.class).get();
        HobbyHub expectedHobbyHub = new HobbyHubBuilder().withMember(ALICE).withMember(BENSON)
                .withSession(new SessionBuilder(CHESS_MEETUP).withDateTime("2026-10-24T00:00").withLocation("")
                        .withMembers(ALICE.getEmail().value).build())
                .build();
        assertEquals(expectedHobbyHub, dataFromFile.toModelType());
    }

    @Test
    public void toModelType_sessionWithMixedCaseEmails_matchedToMembers() throws Exception {
        JsonSerializableHobbyHub dataFromFile = JsonUtil.readJsonFile(SESSION_WITH_MIXED_CASE_EMAIL_FILE,
                JsonSerializableHobbyHub.class).get();
        HobbyHub expectedHobbyHub = new HobbyHubBuilder().withMember(ALICE).withMember(BENSON)
                .withSession(new SessionBuilder(CHESS_MEETUP).withDateTime("2026-10-24T00:00").withLocation("")
                        .build()).build();
        assertEquals(expectedHobbyHub, dataFromFile.toModelType());
    }

    @Test
    public void constructor_fromModel_roundTripsSessions() throws Exception {
        HobbyHub original = TypicalSessions.getTypicalHobbyHubWithSessions();
        assertEquals(original, new JsonSerializableHobbyHub(original).toModelType());
    }

}
