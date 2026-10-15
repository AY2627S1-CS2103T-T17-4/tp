package seedu.hobbyhub.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static seedu.hobbyhub.testutil.TypicalMembers.getTypicalHobbyHub;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.hobbyhub.commons.core.GuiSettings;
import seedu.hobbyhub.model.HobbyHub;
import seedu.hobbyhub.model.ReadOnlyHobbyHub;
import seedu.hobbyhub.model.UserPrefs;

public class StorageManagerTest {

    @TempDir
    public Path testFolder;

    private StorageManager storageManager;

    @BeforeEach
    public void setUp() {
        JsonHobbyHubStorage hobbyHubStorage = new JsonHobbyHubStorage(getTempFilePath("ab"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(getTempFilePath("prefs"));
        storageManager = new StorageManager(hobbyHubStorage, userPrefsStorage);
    }

    private Path getTempFilePath(String fileName) {
        return testFolder.resolve(fileName);
    }

    @Test
    public void prefsReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonUserPrefsStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonUserPrefsStorageTest} class.
         */
        UserPrefs original = new UserPrefs();
        original.setGuiSettings(new GuiSettings(300, 600, 4, 6));
        storageManager.saveUserPrefs(original);
        UserPrefs retrieved = storageManager.readUserPrefs().get();
        assertEquals(original, retrieved);
    }

    @Test
    public void hobbyHubReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonHobbyHubStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonHobbyHubStorageTest} class.
         */
        HobbyHub original = getTypicalHobbyHub();
        storageManager.saveHobbyHub(original);
        ReadOnlyHobbyHub retrieved = storageManager.readHobbyHub().get();
        assertEquals(original, new HobbyHub(retrieved));
    }

    @Test
    public void getHobbyHubFilePath() {
        assertNotNull(storageManager.getHobbyHubFilePath());
    }

}
