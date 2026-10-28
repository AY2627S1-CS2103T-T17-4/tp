package seedu.hobbyhub.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.hobbyhub.commons.core.GuiSettings;
import seedu.hobbyhub.logic.Logic;
import seedu.hobbyhub.logic.commands.CommandResult;
import seedu.hobbyhub.logic.commands.ViewCommand;
import seedu.hobbyhub.logic.commands.ViewDetails;
import seedu.hobbyhub.logic.commands.exceptions.CommandException;
import seedu.hobbyhub.logic.commands.session.ViewSessionCommand;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.model.ListState;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.session.Session;
import seedu.hobbyhub.model.session.SessionName;
import seedu.hobbyhub.testutil.MemberBuilder;
import seedu.hobbyhub.testutil.SessionBuilder;
import seedu.hobbyhub.testutil.TypicalMembers;
import seedu.hobbyhub.testutil.TypicalSessions;

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

    @Test
    public void executeCommand_listStateChanges_showsMatchingListAndHeading() {
        FxTestHelper.runOnFxThread(() -> {
            Stage stage = new Stage();
            TestLogic logic = new TestLogic();
            MainWindow mainWindow = new MainWindow(stage, logic, Path.of("data", "addressbook.json"));
            mainWindow.fillInnerParts();
            Scene scene = mainWindow.getRoot().getScene();
            mainWindow.show();
            StackPane listPanelPlaceholder = (StackPane) scene.lookup("#listPanelPlaceholder");
            Label listHeading = (Label) scene.lookup("#listHeading");

            // starts on the member list
            assertEquals("MEMBERS", listHeading.getText());
            assertEquals(List.of(mainWindow.getMemberListPanel().getRoot()), listPanelPlaceholder.getChildren());

            // a command that switches to sessions shows the session list
            logic.listState = ListState.SESSIONS;
            invokeExecuteCommand(mainWindow, "list -s");
            assertEquals("SESSIONS", listHeading.getText());
            assertEquals(List.of(mainWindow.getSessionListPanel().getRoot()), listPanelPlaceholder.getChildren());

            // and back
            logic.listState = ListState.MEMBERS;
            invokeExecuteCommand(mainWindow, "list -m");
            assertEquals("MEMBERS", listHeading.getText());
            assertEquals(List.of(mainWindow.getMemberListPanel().getRoot()), listPanelPlaceholder.getChildren());
        });
    }

    @Test
    public void executeCommand_failsAfterSwitchingList_stillShowsCurrentList() {
        FxTestHelper.runOnFxThread(() -> {
            Stage stage = new Stage();
            TestLogic logic = new TestLogic();
            logic.listState = ListState.SESSIONS;
            MainWindow mainWindow = new MainWindow(stage, logic, Path.of("data", "addressbook.json"));
            mainWindow.fillInnerParts();
            Scene scene = mainWindow.getRoot().getScene();
            StackPane listPanelPlaceholder = (StackPane) scene.lookup("#listPanelPlaceholder");
            Label listHeading = (Label) scene.lookup("#listHeading");

            // list -m switches the list, then saving fails
            logic.listStateOnExecute = ListState.MEMBERS;
            logic.commandException = new CommandException("Could not save data");
            assertThrows(AssertionError.class, () -> invokeExecuteCommand(mainWindow, "list -m"));

            assertEquals("MEMBERS", listHeading.getText());
            assertEquals(List.of(mainWindow.getMemberListPanel().getRoot()), listPanelPlaceholder.getChildren());
        });
    }

    @Test
    public void executeCommand_viewedMember_survivesListSwitchAndTracksIdentityChange() {
        FxTestHelper.runOnFxThread(() -> {
            Stage stage = new Stage();
            TestLogic logic = new TestLogic();
            Member originalMember = logic.members.get(0);
            logic.commandResult = new CommandResult("Member details",
                    ViewCommand.createViewDetails(originalMember));
            MainWindow mainWindow = new MainWindow(stage, logic, Path.of("data", "addressbook.json"));
            mainWindow.fillInnerParts();
            Scene scene = mainWindow.getRoot().getScene();
            mainWindow.show();

            invokeExecuteCommand(mainWindow, "view 1");
            assertEquals(originalMember.getName().fullName, ((Label) scene.lookup("#title")).getText());
            assertNull(invokePrivate(mainWindow, "findViewedSession"));

            Session originalSession = logic.sessions.get(0);
            logic.sessions.set(0, new Session(originalSession.getName(), originalSession.getDate(),
                    "Ignored while member is viewed", originalSession.isCompleted(), originalSession.getMembers()));

            logic.commandResult = new CommandResult("Switched lists");
            logic.listState = ListState.SESSIONS;
            invokeExecuteCommand(mainWindow, "list -s");
            assertEquals(originalMember.getName().fullName, ((Label) scene.lookup("#title")).getText());

            Member phoneUpdatedMember = new MemberBuilder(originalMember).withPhone("11111111").build();
            logic.members.set(0, phoneUpdatedMember);
            assertEquals("11111111", getDetailValue(mainWindow, 0));

            Member updatedMember = new MemberBuilder(phoneUpdatedMember).withName("Alice Updated")
                    .withPhone("12345678").withEmail("alice.updated@example.com").build();
            logic.members.set(0, updatedMember);

            assertEquals("Alice Updated", ((Label) scene.lookup("#title")).getText());
            assertEquals("alice.updated@example.com", ((Label) scene.lookup("#subtitle")).getText());
            assertEquals("12345678", getDetailValue(mainWindow, 0));
            logic.members.remove(updatedMember);
            assertEquals("Alice Updated", ((Label) scene.lookup("#title")).getText());
            stage.hide();
        });
    }

    @Test
    public void executeCommand_viewedSession_tracksSessionUpdatesAndReplacement() {
        FxTestHelper.runOnFxThread(() -> {
            Stage stage = new Stage();
            TestLogic logic = new TestLogic();
            Session originalSession = logic.sessions.get(0);
            logic.commandResult = new CommandResult("Session details",
                    ViewSessionCommand.createViewDetails(originalSession));
            MainWindow mainWindow = new MainWindow(stage, logic, Path.of("data", "addressbook.json"));
            mainWindow.fillInnerParts();
            Scene scene = mainWindow.getRoot().getScene();
            mainWindow.show();

            invokeExecuteCommand(mainWindow, "view 1");
            assertEquals(originalSession.getName().fullName, ((Label) scene.lookup("#title")).getText());
            assertNull(invokePrivate(mainWindow, "findViewedMember"));

            Member originalMember = logic.members.get(0);
            logic.members.set(0, new MemberBuilder(originalMember).withPhone("12345678").build());

            Session updatedSession = new Session(originalSession.getName(), originalSession.getDate(),
                    "Updated Hall", originalSession.isCompleted(), originalSession.getMembers());
            logic.sessions.set(0, updatedSession);
            assertEquals("Updated Hall", getDetailValue(mainWindow, 1));

            Session renamedSession = new Session(new SessionName("Renamed Session"), updatedSession.getDate(),
                    updatedSession.getLocation(), updatedSession.isCompleted(), updatedSession.getMembers());
            logic.sessions.set(0, renamedSession);

            assertEquals("Renamed Session", ((Label) scene.lookup("#title")).getText());
            assertEquals("Updated Hall", getDetailValue(mainWindow, 1));
            logic.sessions.remove(renamedSession);
            assertEquals("Renamed Session", ((Label) scene.lookup("#title")).getText());
            stage.hide();
        });
    }

    @Test
    public void findViewedRecords_withoutViewedDetails_returnsNull() {
        FxTestHelper.runOnFxThread(() -> {
            TestLogic logic = new TestLogic();
            MainWindow mainWindow = new MainWindow(new Stage(), logic, Path.of("data", "addressbook.json"));
            mainWindow.fillInnerParts();

            assertNull(invokePrivate(mainWindow, "findViewedMember"));
            assertNull(invokePrivate(mainWindow, "findViewedSession"));

            logic.members.set(0, new MemberBuilder(logic.members.get(0)).withPhone("87654321").build());
            Session session = logic.sessions.get(0);
            logic.sessions.set(0, new Session(session.getName(), session.getDate(), "Updated before viewing",
                    session.isCompleted(), session.getMembers()));
        });
    }

    @Test
    public void executeCommand_staleViewedRecord_ignoresUnrelatedRemoval() {
        FxTestHelper.runOnFxThread(() -> {
            TestLogic logic = new TestLogic();
            Member otherMember = new MemberBuilder().withName("Bob Yang").withPhone("98765432")
                    .withEmail("bob@example.com").withHobby("Chess").build();
            logic.members.add(otherMember);
            Session otherSession = new SessionBuilder().withName("Other Session").build();
            logic.sessions.add(otherSession);
            Stage stage = new Stage();
            MainWindow mainWindow = new MainWindow(stage, logic, Path.of("data", "addressbook.json"));
            mainWindow.fillInnerParts();
            mainWindow.show();
            Scene scene = mainWindow.getRoot().getScene();

            logic.commandResult = new CommandResult("Stale member", new ViewDetails("Stale Member", "stale",
                    List.of(), ViewDetails.RecordType.MEMBER, "missing@example.com"));
            invokeExecuteCommand(mainWindow, "view 1");
            logic.members.remove(otherMember);
            assertEquals("Stale Member", ((Label) scene.lookup("#title")).getText());

            logic.commandResult = new CommandResult("Stale session", new ViewDetails("Stale Session", "details",
                    List.of(), ViewDetails.RecordType.SESSION, "Missing Session"));
            invokeExecuteCommand(mainWindow, "view 1");
            logic.sessions.remove(otherSession);
            assertEquals("Stale Session", ((Label) scene.lookup("#title")).getText());
            stage.hide();
        });
    }

    private static String getDetailValue(MainWindow mainWindow, int index) {
        VBox fields = (VBox) mainWindow.getRoot().getScene().lookup("#fields");
        VBox field = (VBox) fields.getChildren().get(index);
        return ((Label) field.getChildren().get(1)).getText();
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

    private static Object invokePrivate(MainWindow mainWindow, String methodName) {
        try {
            Method method = MainWindow.class.getDeclaredMethod(methodName);
            method.setAccessible(true);
            return method.invoke(mainWindow);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static class TestLogic implements Logic {
        private final GuiSettings guiSettings = new GuiSettings(800, 700, 20, 30);
        private final ObservableList<Member> members = FXCollections.observableArrayList(TypicalMembers.ALICE);
        private final ObservableList<Session> sessions =
                FXCollections.observableArrayList(TypicalSessions.CHESS_MEETUP);
        private ListState listState = ListState.MEMBERS;
        private ListState listStateOnExecute;
        private CommandResult commandResult = new CommandResult("Unused");
        private CommandException commandException;
        private GuiSettings savedGuiSettings;

        @Override
        public CommandResult execute(String commandText) throws CommandException, ParseException {
            if (listStateOnExecute != null) {
                listState = listStateOnExecute;
            }
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
        public ObservableList<Session> getFilteredSessionList() {
            return sessions;
        }

        @Override
        public ListState getListState() {
            return listState;
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
