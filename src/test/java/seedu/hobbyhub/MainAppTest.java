package seedu.hobbyhub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.hobbyhub.commons.core.GuiSettings;
import seedu.hobbyhub.commons.exceptions.DataLoadingException;
import seedu.hobbyhub.model.HobbyHub;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.ModelManager;
import seedu.hobbyhub.model.ReadOnlyHobbyHub;
import seedu.hobbyhub.model.ReadOnlyUserPrefs;
import seedu.hobbyhub.model.UserPrefs;
import seedu.hobbyhub.storage.JsonUserPrefsStorage;
import seedu.hobbyhub.storage.Storage;
import seedu.hobbyhub.ui.FxTestHelper;
import seedu.hobbyhub.ui.Ui;

public class MainAppTest {

    @TempDir
    public Path testFolder;

    @Test
    public void initPrefs_missingFile_createsDefaultPreferences() throws Exception {
        Path preferencesFile = testFolder.resolve("preferences.json");
        MainApp app = new MainApp();

        UserPrefs preferences = app.initPrefs(new JsonUserPrefsStorage(preferencesFile));

        assertEquals(new UserPrefs(), preferences);
        assertTrue(Files.exists(preferencesFile));
    }

    @Test
    public void initPrefs_invalidFile_usesDefaultPreferences() throws Exception {
        Path preferencesFile = testFolder.resolve("preferences.json");
        Files.writeString(preferencesFile, "not valid json");
        MainApp app = new MainApp();

        UserPrefs preferences = app.initPrefs(new JsonUserPrefsStorage(preferencesFile));

        assertEquals(new UserPrefs(), preferences);
        assertTrue(Files.exists(preferencesFile));
    }

    @Test
    public void initPrefs_nullGuiSettings_restoresAndSavesDefaultSettings() throws Exception {
        Path preferencesFile = testFolder.resolve("preferences.json");
        Files.writeString(preferencesFile, "{\"guiSettings\":null}");
        JsonUserPrefsStorage storage = new JsonUserPrefsStorage(preferencesFile);

        UserPrefs preferences = new MainApp().initPrefs(storage);

        assertEquals(new GuiSettings(), preferences.getGuiSettings());
        assertEquals(new GuiSettings(), storage.readUserPrefs().orElseThrow().getGuiSettings());
    }

    @Test
    public void initPrefs_validGuiSettings_preservesSavedSettings() throws Exception {
        Path preferencesFile = testFolder.resolve("preferences.json");
        JsonUserPrefsStorage storage = new JsonUserPrefsStorage(preferencesFile);
        UserPrefs savedPreferences = new UserPrefs();
        savedPreferences.setGuiSettings(new GuiSettings(1000, 700, 100, 200));
        storage.saveUserPrefs(savedPreferences);

        UserPrefs preferences = new MainApp().initPrefs(storage);

        assertEquals(savedPreferences, preferences);
    }

    @Test
    public void init_nullGuiSettings_initializesModelWithDefaultSettings() throws Exception {
        Path preferencesFile = testFolder.resolve("preferences.json");
        Files.writeString(preferencesFile, "{\"guiSettings\":null}");
        TestMainApp app = new TestMainApp(preferencesFile, testFolder.resolve("hobbyhub.json"));

        app.init();

        assertEquals(new GuiSettings(), app.model.getGuiSettings());
        assertFalse(app.model.getHobbyHub().getMemberList().isEmpty());
    }

    @Test
    public void initPrefs_saveFailure_stillReturnsPreferences() {
        MainApp app = new MainApp();

        UserPrefs preferences = app.initPrefs(new FailingUserPrefsStorage(testFolder.resolve("preferences.json")));

        assertEquals(new UserPrefs(), preferences);
    }

    @Test
    public void createStorageMethods_useTheApplicationDefaultPaths() {
        MainApp app = new MainApp();

        assertEquals(Path.of("preferences.json"), app.createUserPrefsStorage().getUserPrefsFilePath());
        assertEquals(Path.of("data", "addressbook.json"), app.createHobbyHubStorage().getHobbyHubFilePath());
    }

    @Test
    public void init_initializesApplicationUsingConfiguredStorage() throws Exception {
        TestMainApp app = new TestMainApp(testFolder.resolve("preferences.json"), testFolder.resolve("hobbyhub.json"));

        app.init();

        assertTrue(app.model.getHobbyHub().getMemberList().size() > 0);
        assertEquals(testFolder.resolve("hobbyhub.json"), app.storage.getHobbyHubFilePath());
    }

    @Test
    public void initModelManager_handlesMissingAndUnreadableData() throws Exception {
        MainApp app = new MainApp();

        Model modelWithSampleData = invokeInitModelManager(app, new TestStorage(Optional.empty(), false));
        Model modelWithUnreadableData = invokeInitModelManager(app, new TestStorage(Optional.empty(), true));

        assertFalse(modelWithSampleData.getHobbyHub().getMemberList().isEmpty());
        assertEquals(new HobbyHub(), modelWithUnreadableData.getHobbyHub());
    }

    @Test
    public void stop_savesTheCurrentUserPreferences() {
        MainApp app = new MainApp();
        TestStorage storage = new TestStorage(Optional.empty(), false);
        app.model = new ModelManager();
        app.storage = storage;

        app.stop();

        assertEquals(app.model.getUserPrefs(), storage.savedUserPrefs);
    }

    @Test
    public void start_delegatesToTheUi() {
        MainApp app = new MainApp();
        RecordingUi ui = new RecordingUi();
        app.ui = ui;

        FxTestHelper.runOnFxThread(() -> app.start(new javafx.stage.Stage()));

        assertTrue(ui.started);
    }

    @Test
    public void stop_saveFailure_doesNotPreventShutdown() {
        MainApp app = new MainApp();
        TestStorage storage = new TestStorage(Optional.empty(), false);
        storage.throwsOnSave = true;
        app.model = new ModelManager();
        app.storage = storage;

        app.stop();

        assertTrue(storage.saveAttempted);
    }

    private Model invokeInitModelManager(MainApp app, Storage storage) throws Exception {
        Method method = MainApp.class.getDeclaredMethod("initModelManager", Storage.class, ReadOnlyUserPrefs.class);
        method.setAccessible(true);
        try {
            return (Model) method.invoke(app, storage, new UserPrefs());
        } catch (InvocationTargetException e) {
            throw (Exception) e.getCause();
        }
    }

    private static class TestStorage implements Storage {
        private final Optional<ReadOnlyHobbyHub> hobbyHub;
        private final boolean throwsOnRead;
        private ReadOnlyUserPrefs savedUserPrefs;
        private boolean saveAttempted;
        private boolean throwsOnSave;

        TestStorage(Optional<ReadOnlyHobbyHub> hobbyHub, boolean throwsOnRead) {
            this.hobbyHub = hobbyHub;
            this.throwsOnRead = throwsOnRead;
        }

        @Override
        public Path getUserPrefsFilePath() {
            return Path.of("preferences.json");
        }

        @Override
        public Optional<UserPrefs> readUserPrefs() {
            return Optional.empty();
        }

        @Override
        public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
            saveAttempted = true;
            if (throwsOnSave) {
                throw new IOException("unable to save preferences");
            }
            savedUserPrefs = userPrefs;
        }

        @Override
        public Path getHobbyHubFilePath() {
            return Path.of("data", "addressbook.json");
        }

        @Override
        public Optional<ReadOnlyHobbyHub> readHobbyHub() throws DataLoadingException {
            if (throwsOnRead) {
                throw new DataLoadingException(new IOException("unreadable data"));
            }
            return hobbyHub;
        }

        @Override
        public void saveHobbyHub(ReadOnlyHobbyHub hobbyHub) {
            // Saving hobby data is not part of these lifecycle tests.
        }
    }

    private static class FailingUserPrefsStorage extends JsonUserPrefsStorage {
        FailingUserPrefsStorage(Path filePath) {
            super(filePath);
        }

        @Override
        public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
            throw new IOException("unable to save preferences");
        }
    }

    private static class TestMainApp extends MainApp {
        private final Path preferencesFile;
        private final Path hobbyHubFile;

        TestMainApp(Path preferencesFile, Path hobbyHubFile) {
            this.preferencesFile = preferencesFile;
            this.hobbyHubFile = hobbyHubFile;
        }

        @Override
        protected JsonUserPrefsStorage createUserPrefsStorage() {
            return new JsonUserPrefsStorage(preferencesFile);
        }

        @Override
        protected seedu.hobbyhub.storage.JsonHobbyHubStorage createHobbyHubStorage() {
            return new seedu.hobbyhub.storage.JsonHobbyHubStorage(hobbyHubFile);
        }
    }

    private static class RecordingUi implements Ui {
        private boolean started;

        @Override
        public void start(javafx.stage.Stage primaryStage) {
            started = true;
        }
    }
}
