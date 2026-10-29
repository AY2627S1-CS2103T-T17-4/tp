package seedu.hobbyhub.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.testutil.Assert.assertThrows;
import static seedu.hobbyhub.testutil.TypicalMembers.ALICE;
import static seedu.hobbyhub.testutil.TypicalMembers.HOON;
import static seedu.hobbyhub.testutil.TypicalMembers.IDA;
import static seedu.hobbyhub.testutil.TypicalMembers.getTypicalHobbyHub;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.hobbyhub.commons.exceptions.DataLoadingException;
import seedu.hobbyhub.model.HobbyHub;
import seedu.hobbyhub.model.ReadOnlyHobbyHub;

public class JsonHobbyHubStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonHobbyHubStorageTest");
    private static final String BACKUP_SUFFIX = ".bak";

    @TempDir
    public Path testFolder;

    @Test
    public void readHobbyHub_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readHobbyHub(null));
    }

    private Optional<ReadOnlyHobbyHub> readHobbyHub(String filePath) throws Exception {
        return new JsonHobbyHubStorage(Paths.get(filePath)).readHobbyHub(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    /**
     * Copies the test data file {@code fixtureName} into the temporary folder and reads the copy.
     * Reading a copy keeps backups made by the storage out of the test data folder.
     */
    private Optional<ReadOnlyHobbyHub> readHobbyHubFromCopy(String fixtureName) throws Exception {
        Path copy = testFolder.resolve(fixtureName);
        Files.copy(TEST_DATA_FOLDER.resolve(fixtureName), copy);
        return new JsonHobbyHubStorage(copy).readHobbyHub();
    }

    /**
     * Writes {@code content} to a new file named {@code fileName} in the temporary folder and reads it.
     */
    private Optional<ReadOnlyHobbyHub> readHobbyHubFromContent(String fileName, String content) throws Exception {
        Path file = testFolder.resolve(fileName);
        Files.writeString(file, content);
        return new JsonHobbyHubStorage(file).readHobbyHub();
    }

    /**
     * Returns the number of backup files in the temporary folder.
     */
    private long countBackups() throws IOException {
        try (Stream<Path> files = Files.list(testFolder)) {
            return files.filter(path -> path.toString().endsWith(BACKUP_SUFFIX)).count();
        }
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
    public void readHobbyHub_nonObjectRoot_throwsDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readHobbyHubFromContent("arrayRoot.json", "[]"));
    }

    @Test
    public void readHobbyHub_preHobbyData_usesDefaultHobbyAndPreservesDetails() throws Exception {
        ReadOnlyHobbyHub hobbyHub = readHobbyHub("legacyMemberData.json").get();

        assertEquals(1, hobbyHub.getMemberList().size());
        assertEquals("Legacy Person", hobbyHub.getMemberList().get(0).getName().fullName);
        assertEquals("legacy@example.com", hobbyHub.getMemberList().get(0).getEmail().value);
        assertEquals("Unspecified", hobbyHub.getMemberList().get(0).getHobby().value);
        assertEquals(1, hobbyHub.getMemberList().get(0).getGroups().size());
    }

    @Test
    public void readHobbyHub_invalidMemberHobbyHub_discardsInvalidMember() throws Exception {
        ReadOnlyHobbyHub hobbyHub = readHobbyHubFromCopy("invalidMemberHobbyHub.json").get();

        assertTrue(hobbyHub.getMemberList().isEmpty());
    }

    @Test
    public void readHobbyHub_invalidAndValidMemberHobbyHub_keepsValidMember() throws Exception {
        ReadOnlyHobbyHub hobbyHub = readHobbyHubFromCopy("invalidAndValidMemberHobbyHub.json").get();

        assertEquals(1, hobbyHub.getMemberList().size());
    }

    @Test
    public void readHobbyHub_nullAndMalformedEntries_discardsThem() throws Exception {
        ReadOnlyHobbyHub hobbyHub = readHobbyHubFromContent("malformedEntries.json",
                "{\"persons\": [null, []], \"sessions\": [null]}").get();

        assertTrue(hobbyHub.getMemberList().isEmpty());
        assertTrue(hobbyHub.getSessionList().isEmpty());
        assertEquals(1, countBackups());
    }

    @Test
    public void readHobbyHub_missingLists_loadsEmptyHobbyHub() throws Exception {
        ReadOnlyHobbyHub hobbyHub = readHobbyHubFromContent("noLists.json", "{}").get();

        assertTrue(hobbyHub.getMemberList().isEmpty());
        assertTrue(hobbyHub.getSessionList().isEmpty());
        assertEquals(0, countBackups());
    }

    @Test
    public void readHobbyHub_invalidEntries_backsUpOriginalFile() throws Exception {
        readHobbyHubFromCopy("invalidAndValidMemberHobbyHub.json");

        assertEquals(1, countBackups());
        try (Stream<Path> files = Files.list(testFolder)) {
            Path backup = files.filter(path -> path.toString().endsWith(BACKUP_SUFFIX)).findFirst().get();
            assertEquals(Files.readString(TEST_DATA_FOLDER.resolve("invalidAndValidMemberHobbyHub.json")),
                    Files.readString(backup));
        }
    }

    @Test
    public void readHobbyHub_validFile_noBackup() throws Exception {
        readHobbyHubFromCopy("legacyMemberData.json");

        assertEquals(0, countBackups());
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

        // Saved data is fully valid, so nothing was discarded
        assertEquals(0, countBackups());
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

    @Test
    public void readHobbyHub_backupFails_stillLoadsRemainingData() throws Exception {
        Path copy = testFolder.resolve("invalidAndValidMemberHobbyHub.json");
        Files.copy(TEST_DATA_FOLDER.resolve("invalidAndValidMemberHobbyHub.json"), copy);
        Path unwritableBackup = testFolder.resolve("missingFolder").resolve("backup.bak");
        JsonHobbyHubStorage storage = new JsonHobbyHubStorage(copy) {
            @Override
            Path getBackupPath(Path filePath) {
                return unwritableBackup;
            }
        };

        ReadOnlyHobbyHub hobbyHub = storage.readHobbyHub().get();

        assertEquals(1, hobbyHub.getMemberList().size());
        assertEquals(0, countBackups());
    }
}
