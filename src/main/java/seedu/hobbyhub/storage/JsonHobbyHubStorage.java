package seedu.hobbyhub.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;

import seedu.hobbyhub.commons.core.LogsCenter;
import seedu.hobbyhub.commons.exceptions.DataLoadingException;
import seedu.hobbyhub.commons.exceptions.IllegalValueException;
import seedu.hobbyhub.commons.util.FileUtil;
import seedu.hobbyhub.commons.util.JsonUtil;
import seedu.hobbyhub.model.HobbyHub;
import seedu.hobbyhub.model.ReadOnlyHobbyHub;

/**
 * A class to access HobbyHub data stored as a JSON file on the hard disk.
 * Invalid members and sessions in the file are discarded, and the original file is backed up beforehand.
 */
public class JsonHobbyHubStorage {

    private static final String MESSAGE_INVALID_ROOT = "Data file must contain a JSON object at the top level.";

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
     * Members and sessions that cannot be parsed or are invalid are discarded and logged, while the rest of the
     * data is kept. If any entry is discarded, a copy of the original file is saved alongside it as a backup.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if the file is not valid JSON, or its top level is not a JSON object.
     */
    public Optional<ReadOnlyHobbyHub> readHobbyHub(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        Optional<JsonNode> jsonRoot = JsonUtil.readJsonFile(filePath, JsonNode.class);
        if (jsonRoot.isEmpty()) {
            return Optional.empty();
        }

        JsonNode root = jsonRoot.get();
        if (!root.isObject()) {
            logger.info("Illegal values found in " + filePath + ": " + MESSAGE_INVALID_ROOT);
            throw new DataLoadingException(new IllegalValueException(MESSAGE_INVALID_ROOT));
        }

        List<JsonAdaptedMember> members = new ArrayList<>();
        List<JsonAdaptedSession> sessions = new ArrayList<>();
        int discardedCount = parseEntries(root.path(JsonSerializableHobbyHub.MEMBERS_FIELD),
                JsonAdaptedMember.class, members);
        discardedCount += parseEntries(root.path(JsonSerializableHobbyHub.SESSIONS_FIELD),
                JsonAdaptedSession.class, sessions);

        JsonSerializableHobbyHub jsonHobbyHub = new JsonSerializableHobbyHub(members, sessions);
        HobbyHub hobbyHub = jsonHobbyHub.toModelType();
        discardedCount += jsonHobbyHub.getDiscardedCount();

        if (discardedCount > 0) {
            logger.warning(discardedCount + " invalid entries discarded from " + filePath);
            backUp(filePath);
        }
        return Optional.of(hobbyHub);
    }

    /**
     * Parses each element of {@code array} independently as a {@code type} and adds it to {@code output}.
     * Elements that are {@code null} or cannot be parsed are skipped. Returns the number of skipped elements.
     * Does nothing if {@code array} is not a JSON array.
     *
     * @param <T> the type of the adapted entry.
     */
    private static <T> int parseEntries(JsonNode array, Class<T> type, List<T> output) {
        if (!array.isArray()) {
            return 0;
        }

        int skippedCount = 0;
        for (JsonNode node : array) {
            try {
                T entry = JsonUtil.fromJsonString(node.toString(), type);
                if (entry == null) {
                    logger.warning("Discarding null " + type.getSimpleName() + " entry.");
                    skippedCount++;
                } else {
                    output.add(entry);
                }
            } catch (IOException ioe) {
                logger.warning("Discarding malformed " + type.getSimpleName() + " entry: " + ioe.getMessage());
                skippedCount++;
            }
        }
        return skippedCount;
    }

    /**
     * Returns the path where a backup of the file at {@code filePath} is saved, which is a timestamped file
     * next to it.
     */
    Path getBackupPath(Path filePath) {
        return filePath.resolveSibling(filePath.getFileName() + "." + System.currentTimeMillis() + ".bak");
    }

    /**
     * Saves a copy of the file at {@code filePath} at {@link #getBackupPath(Path)}, so that discarded entries
     * remain recoverable after the next save overwrites the file. Failure to back up is logged and otherwise
     * ignored.
     */
    private void backUp(Path filePath) {
        Path backupPath = getBackupPath(filePath);
        try {
            Files.copy(filePath, backupPath);
            logger.info("Original data file backed up to " + backupPath);
        } catch (IOException ioe) {
            logger.warning("Could not back up " + filePath + ": " + ioe.getMessage());
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
