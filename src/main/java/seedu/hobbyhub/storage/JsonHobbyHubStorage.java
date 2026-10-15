package seedu.hobbyhub.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.hobbyhub.commons.core.LogsCenter;
import seedu.hobbyhub.commons.exceptions.DataLoadingException;
import seedu.hobbyhub.commons.exceptions.IllegalValueException;
import seedu.hobbyhub.commons.util.FileUtil;
import seedu.hobbyhub.commons.util.JsonUtil;
import seedu.hobbyhub.model.ReadOnlyHobbyHub;

/**
 * A class to access HobbyHub data stored as a JSON file on the hard disk.
 */
public class JsonHobbyHubStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonHobbyHubStorage.class);

    private Path filePath;

    public JsonHobbyHubStorage(Path filePath) {
        this.filePath = filePath;
    }

    public Path getHobbyHubFilePath() {
        return filePath;
    }

    /**
     * Returns HobbyHub data as a {@link ReadOnlyHobbyHub}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyHobbyHub> readHobbyHub() throws DataLoadingException {
        return readHobbyHub(filePath);
    }

    /**
     * Similar to {@link #readHobbyHub()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyHobbyHub> readHobbyHub(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        Optional<JsonSerializableHobbyHub> jsonHobbyHub = JsonUtil.readJsonFile(
                filePath, JsonSerializableHobbyHub.class);
        if (!jsonHobbyHub.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonHobbyHub.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataLoadingException(ive);
        }
    }

    /**
     * Saves the given {@link ReadOnlyHobbyHub} to the storage.
     * @param hobbyHub cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    public void saveHobbyHub(ReadOnlyHobbyHub hobbyHub) throws IOException {
        saveHobbyHub(hobbyHub, filePath);
    }

    /**
     * Similar to {@link #saveHobbyHub(ReadOnlyHobbyHub)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveHobbyHub(ReadOnlyHobbyHub hobbyHub, Path filePath) throws IOException {
        requireNonNull(hobbyHub);
        requireNonNull(filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableHobbyHub(hobbyHub), filePath);
    }

}
