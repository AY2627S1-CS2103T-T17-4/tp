package seedu.hobbyhub.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.hobbyhub.commons.core.GuiSettings;
import seedu.hobbyhub.commons.core.LogsCenter;
import seedu.hobbyhub.logic.commands.Command;
import seedu.hobbyhub.logic.commands.CommandResult;
import seedu.hobbyhub.logic.commands.exceptions.CommandException;
import seedu.hobbyhub.logic.parser.HobbyHubParser;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.model.HobbyHub;
import seedu.hobbyhub.model.ListState;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.session.Session;
import seedu.hobbyhub.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final HobbyHubParser hobbyHubParser;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        hobbyHubParser = new HobbyHubParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        HobbyHub hobbyHubBeforeCommand = new HobbyHub(model.getHobbyHub());
        CommandResult commandResult;
        Command command = hobbyHubParser.parseCommand(commandText, model.getListState());
        commandResult = command.execute(model);

        try {
            storage.saveHobbyHub(model.getHobbyHub());
        } catch (AccessDeniedException e) {
            model.setHobbyHub(hobbyHubBeforeCommand);
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            model.setHobbyHub(hobbyHubBeforeCommand);
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }

        return commandResult;
    }

    @Override
    public ObservableList<Member> getFilteredMemberList() {
        return model.getFilteredMemberList();
    }

    @Override
    public ObservableList<Member> getMemberList() {
        return model.getHobbyHub().getMemberList();
    }

    @Override
    public ObservableList<Session> getFilteredSessionList() {
        return model.getFilteredSessionList();
    }

    @Override
    public ObservableList<Session> getSessionList() {
        return model.getHobbyHub().getSessionList();
    }

    @Override
    public ListState getListState() {
        return model.getListState();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
