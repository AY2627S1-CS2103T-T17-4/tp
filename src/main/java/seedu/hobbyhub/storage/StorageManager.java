package seedu.hobbyhub.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.hobbyhub.commons.core.LogsCenter;
import seedu.hobbyhub.commons.exceptions.DataLoadingException;
import seedu.hobbyhub.model.ReadOnlyHobbyHub;
import seedu.hobbyhub.model.ReadOnlyUserPrefs;
import seedu.hobbyhub.model.UserPrefs;

/**
 * Manages storage of HobbyHub data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private JsonHobbyHubStorage hobbyHubStorage;
    private JsonUserPrefsStorage userPrefsStorage;

    /**
     * Creates a {@code StorageManager} with the given HobbyHub and user prefs storage.
     */
    public StorageManager(JsonHobbyHubStorage hobbyHubStorage, JsonUserPrefsStorage userPrefsStorage) {
        this.hobbyHubStorage = hobbyHubStorage;
        this.userPrefsStorage = userPrefsStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataLoadingException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ HobbyHub methods ==============================

    @Override
    public Path getHobbyHubFilePath() {
        return hobbyHubStorage.getHobbyHubFilePath();
    }

    @Override
    public Optional<ReadOnlyHobbyHub> readHobbyHub() throws DataLoadingException {
        logger.fine("Attempting to read data from file: " + hobbyHubStorage.getHobbyHubFilePath());
        return hobbyHubStorage.readHobbyHub();
    }

    @Override
    public void saveHobbyHub(ReadOnlyHobbyHub hobbyHub) throws IOException {
        logger.fine("Attempting to write to data file: " + hobbyHubStorage.getHobbyHubFilePath());
        hobbyHubStorage.saveHobbyHub(hobbyHub);
    }

}
