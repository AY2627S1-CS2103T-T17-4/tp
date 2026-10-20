package seedu.hobbyhub.logic;

import javafx.collections.ObservableList;
import seedu.hobbyhub.commons.core.GuiSettings;
import seedu.hobbyhub.logic.commands.CommandResult;
import seedu.hobbyhub.logic.commands.exceptions.CommandException;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.model.ListState;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.session.Session;

/**
 * API of the Logic component
 */
public interface Logic {
    /**
     * Executes the command and returns the result.
     * @param commandText The command as entered by the user.
     * @return the result of the command execution.
     * @throws CommandException If an error occurs during command execution.
     * @throws ParseException If an error occurs during parsing.
     */
    CommandResult execute(String commandText) throws CommandException, ParseException;

    /** Returns an unmodifiable view of the filtered list of members */
    ObservableList<Member> getFilteredMemberList();

    /** Returns an unmodifiable view of the filtered list of sessions */
    ObservableList<Session> getFilteredSessionList();

    /** Returns the list currently shown to the user */
    ListState getListState();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Set the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);
}
