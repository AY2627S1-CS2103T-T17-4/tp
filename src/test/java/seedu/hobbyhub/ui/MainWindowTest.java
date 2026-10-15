package seedu.hobbyhub.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import java.lang.reflect.Method;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import seedu.hobbyhub.commons.core.GuiSettings;
import seedu.hobbyhub.logic.Logic;
import seedu.hobbyhub.logic.commands.CommandResult;
import seedu.hobbyhub.logic.commands.exceptions.CommandException;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.testutil.TypicalMembers;

public class MainWindowTest {

    @Test
    public void constructorAndFillInnerParts_configuresWindowAndDisplaysMembers() {
        FxTestHelper.runOnFxThread(() -> {
            Stage stage = new Stage();
            TestLogic logic = new TestLogic();
            MainWindow mainWindow = new MainWindow(stage, logic, Path.of("data", "addressbook.json"));

            assertEquals(stage, mainWindow.getPrimaryStage());
            assertEquals(logic.getGuiSettings().getWindowWidth(), stage.getWidth());
            assertEquals(logic.getGuiSettings().getWindowHeight(), stage.getHeight());

            mainWindow.fillInnerParts();
            assertNotNull(mainWindow.getMemberListPanel());

            mainWindow.show();
            assertTrue(stage.isShowing());
            stage.hide();
        });
    }

    @Test
    public void executeCommand_withHelpAndExit_updatesUiAndSavesWindowSettings() {
        FxTestHelper.runOnFxThread(() -> {
            Stage stage = new Stage();
            TestLogic logic = new TestLogic();
            logic.commandResult = new CommandResult("Completed", true, true);
            MainWindow mainWindow = new MainWindow(stage, logic, Path.of("data", "addressbook.json"));
            mainWindow.fillInnerParts();
            mainWindow.show();
            TextField commandInput = (TextField) mainWindow.getRoot().getScene().getRoot().lookup(".text-field");

            commandInput.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F1,
                    false, false, false, false));
            commandInput.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F1,
                    false, false, false, false));

            assertEquals(logic.commandResult, invokeExecuteCommand(mainWindow, "list"));
            assertNotNull(logic.savedGuiSettings);
            assertTrue(!stage.isShowing());
        });
    }

    @Test
    public void executeCommand_commandException_displaysAndRethrowsTheError() {
        FxTestHelper.runOnFxThread(() -> {
            Stage stage = new Stage();
            TestLogic logic = new TestLogic();
            logic.commandException = new CommandException("Invalid command");
            MainWindow mainWindow = new MainWindow(stage, logic, Path.of("data", "addressbook.json"));
            mainWindow.fillInnerParts();

            assertThrows(AssertionError.class, () -> invokeExecuteCommand(mainWindow, "invalid"));
        });
    }

    private static CommandResult invokeExecuteCommand(MainWindow mainWindow, String commandText) {
        try {
            Method method = MainWindow.class.getDeclaredMethod("executeCommand", String.class);
            method.setAccessible(true);
            return (CommandResult) method.invoke(mainWindow, commandText);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static class TestLogic implements Logic {
        private final GuiSettings guiSettings = new GuiSettings(800, 700, 20, 30);
        private final ObservableList<Member> members = FXCollections.observableArrayList(TypicalMembers.ALICE);
        private CommandResult commandResult = new CommandResult("Unused");
        private CommandException commandException;
        private GuiSettings savedGuiSettings;

        @Override
        public CommandResult execute(String commandText) throws CommandException, ParseException {
            if (commandException != null) {
                throw commandException;
            }
            return commandResult;
        }

        @Override
        public ObservableList<Member> getFilteredMemberList() {
            return members;
        }

        @Override
        public GuiSettings getGuiSettings() {
            return guiSettings;
        }

        @Override
        public void setGuiSettings(GuiSettings updatedGuiSettings) {
            savedGuiSettings = updatedGuiSettings;
        }
    }
}
