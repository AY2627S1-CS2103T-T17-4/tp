package seedu.hobbyhub.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.hobbyhub.testutil.Assert.assertThrows;
import static seedu.hobbyhub.testutil.TypicalMembers.ALICE;
import static seedu.hobbyhub.testutil.TypicalMembers.HOON;
import static seedu.hobbyhub.testutil.TypicalMembers.IDA;
import static seedu.hobbyhub.testutil.TypicalMembers.getTypicalHobbyHub;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.hobbyhub.commons.exceptions.DataLoadingException;
import seedu.hobbyhub.model.HobbyHub;
import seedu.hobbyhub.model.ReadOnlyHobbyHub;

public class JsonHobbyHubStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonHobbyHubStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readHobbyHub_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readHobbyHub(null));
    }

    private java.util.Optional<ReadOnlyHobbyHub> readHobbyHub(String filePath) throws Exception {
        return new JsonHobbyHubStorage(Paths.get(filePath)).readHobbyHub(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readHobbyHub("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readHobbyHub("notJsonFormatHobbyHub.json"));
    }

    @Test
    public void readHobbyHub_preHobbyData_usesDefaultHobbyAndPreservesDetails() throws Exception {
        ReadOnlyHobbyHub hobbyHub = readHobbyHub("legacyMemberData.json").get();

        assertEquals(1, hobbyHub.getMemberList().size());
        assertEquals("Legacy Person", hobbyHub.getMemberList().get(0).getName().fullName);
        assertEquals("legacy@example.com", hobbyHub.getMemberList().get(0).getEmail().value);
        assertEquals("Unspecified", hobbyHub.getMemberList().get(0).getHobby().value);
        assertEquals(1, hobbyHub.getMemberList().get(0).getTags().size());
    }

    @Test
    public void readHobbyHub_invalidMemberHobbyHub_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readHobbyHub("invalidMemberHobbyHub.json"));
    }

    @Test
    public void readHobbyHub_invalidAndValidMemberHobbyHub_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readHobbyHub("invalidAndValidMemberHobbyHub.json"));
    }

    @Test
    public void readAndSaveHobbyHub_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempHobbyHub.json");
        HobbyHub original = getTypicalHobbyHub();
        JsonHobbyHubStorage jsonHobbyHubStorage = new JsonHobbyHubStorage(filePath);

        // Save in new file and read back
        jsonHobbyHubStorage.saveHobbyHub(original, filePath);
        ReadOnlyHobbyHub readBack = jsonHobbyHubStorage.readHobbyHub(filePath).get();
        assertEquals(original, new HobbyHub(readBack));

        // Modify data, overwrite existing file, and read back
        original.addMember(HOON);
        original.removeMember(ALICE);
        jsonHobbyHubStorage.saveHobbyHub(original, filePath);
        readBack = jsonHobbyHubStorage.readHobbyHub(filePath).get();
        assertEquals(original, new HobbyHub(readBack));

        // Save and read without specifying file path
        original.addMember(IDA);
        jsonHobbyHubStorage.saveHobbyHub(original); // file path not specified
        readBack = jsonHobbyHubStorage.readHobbyHub().get(); // file path not specified
        assertEquals(original, new HobbyHub(readBack));

    }

    @Test
    public void saveHobbyHub_nullHobbyHub_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveHobbyHub(null, "SomeFile.json"));
    }

    /**
     * Saves {@code hobbyHub} at the specified {@code filePath}.
     */
    private void saveHobbyHub(ReadOnlyHobbyHub hobbyHub, String filePath) {
        try {
            new JsonHobbyHubStorage(Paths.get(filePath))
                    .saveHobbyHub(hobbyHub, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveHobbyHub_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveHobbyHub(new HobbyHub(), null));
    }
}
