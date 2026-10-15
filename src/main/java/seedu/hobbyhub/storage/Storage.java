package seedu.hobbyhub.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import seedu.hobbyhub.commons.exceptions.DataLoadingException;
import seedu.hobbyhub.model.ReadOnlyHobbyHub;
import seedu.hobbyhub.model.ReadOnlyUserPrefs;
import seedu.hobbyhub.model.UserPrefs;

/**
 * API of the Storage component
 */
public interface Storage {

    /**
     * Returns the file path of the UserPrefs data file.
     */
    Path getUserPrefsFilePath();

    /**
     * Returns UserPrefs data from storage.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if the loading of data from preference file failed.
     */
    Optional<UserPrefs> readUserPrefs() throws DataLoadingException;

    /**
     * Saves the given {@link seedu.hobbyhub.model.ReadOnlyUserPrefs} to the storage.
     * @param userPrefs cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException;

    /**
     * Returns the file path of the HobbyHub data file.
     */
    Path getHobbyHubFilePath();

    /**
     * Returns HobbyHub data as a {@link ReadOnlyHobbyHub}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    Optional<ReadOnlyHobbyHub> readHobbyHub() throws DataLoadingException;

    /**
     * Saves the given {@link ReadOnlyHobbyHub} to the storage.
     * @param hobbyHub cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveHobbyHub(ReadOnlyHobbyHub hobbyHub) throws IOException;

}
