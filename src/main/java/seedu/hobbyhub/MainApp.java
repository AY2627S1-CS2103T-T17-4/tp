package seedu.hobbyhub;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.hobbyhub.commons.core.GuiSettings;
import seedu.hobbyhub.commons.core.LogsCenter;
import seedu.hobbyhub.commons.exceptions.DataLoadingException;
import seedu.hobbyhub.commons.util.StringUtil;
import seedu.hobbyhub.logic.Logic;
import seedu.hobbyhub.logic.LogicManager;
import seedu.hobbyhub.model.HobbyHub;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.ModelManager;
import seedu.hobbyhub.model.ReadOnlyHobbyHub;
import seedu.hobbyhub.model.ReadOnlyUserPrefs;
import seedu.hobbyhub.model.UserPrefs;
import seedu.hobbyhub.model.util.SampleDataUtil;
import seedu.hobbyhub.storage.JsonHobbyHubStorage;
import seedu.hobbyhub.storage.JsonUserPrefsStorage;
import seedu.hobbyhub.storage.Storage;
import seedu.hobbyhub.storage.StorageManager;
import seedu.hobbyhub.ui.Ui;
import seedu.hobbyhub.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final String VERSION = "V0.5.1";

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);
    private static final Path USER_PREFS_FILE_PATH = Paths.get("preferences.json");
    private static final Path HOBBY_HUB_FILE_PATH = Paths.get("data", "addressbook.json");

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing HobbyHub ]===========================");
        super.init();

        JsonUserPrefsStorage userPrefsStorage = createUserPrefsStorage();
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        JsonHobbyHubStorage hobbyHubStorage = createHobbyHubStorage();
        storage = new StorageManager(hobbyHubStorage, userPrefsStorage);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic, storage.getHobbyHubFilePath());
    }

    /**
     * Creates the storage used for application preferences.
     */
    protected JsonUserPrefsStorage createUserPrefsStorage() {
        return new JsonUserPrefsStorage(USER_PREFS_FILE_PATH);
    }

    /**
     * Creates the storage used for HobbyHub data.
     */
    protected JsonHobbyHubStorage createHobbyHubStorage() {
        return new JsonHobbyHubStorage(HOBBY_HUB_FILE_PATH);
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s HobbyHub and {@code userPrefs}. <br>
     * The data from the sample HobbyHub will be used instead if {@code storage}'s HobbyHub is not found,
     * or an empty HobbyHub will be used instead if errors occur when reading {@code storage}'s HobbyHub.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        logger.info("Using data file : " + storage.getHobbyHubFilePath());

        Optional<ReadOnlyHobbyHub> hobbyHubOptional;
        ReadOnlyHobbyHub initialData;
        try {
            hobbyHubOptional = storage.readHobbyHub();
            if (hobbyHubOptional.isEmpty()) {
                logger.info("Creating a new data file " + storage.getHobbyHubFilePath()
                        + " populated with a sample HobbyHub.");
            }
            initialData = hobbyHubOptional.orElseGet(SampleDataUtil::getSampleHobbyHub);
        } catch (DataLoadingException | IllegalArgumentException e) {
            logger.warning("Data file at " + storage.getHobbyHubFilePath() + " could not be loaded."
                    + " Will be starting with an empty HobbyHub.");
            initialData = new HobbyHub();
        }

        return new ModelManager(initialData, userPrefs);
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(JsonUserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (prefsOptional.isEmpty()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        if (initializedPrefs.getGuiSettings() == null) {
            logger.warning("Saved GUI settings are null. Using default window settings.");
            initializedPrefs.setGuiSettings(new GuiSettings());
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save preference file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting HobbyHub " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping HobbyHub ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
