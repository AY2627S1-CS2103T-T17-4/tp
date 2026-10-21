package seedu.hobbyhub.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.hobbyhub.model.Model.PREDICATE_SHOW_ALL_MEMBERS;
import static seedu.hobbyhub.model.Model.PREDICATE_SHOW_ALL_SESSIONS;

import java.util.Objects;
import java.util.Optional;

import seedu.hobbyhub.commons.util.ToStringBuilder;
import seedu.hobbyhub.model.ListState;
import seedu.hobbyhub.model.Model;

/**
 * Shows every member or every session, optionally switching which list is shown.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String FLAG_MEMBERS = "-m";
    public static final String FLAG_SESSIONS = "-s";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows all members or all sessions. Without a flag, shows everything in the current list.\n"
            + "Parameters: [" + FLAG_MEMBERS + " | " + FLAG_SESSIONS + "]\n"
            + "Example: " + COMMAND_WORD + " " + FLAG_SESSIONS;

    public static final String MESSAGE_SUCCESS = "Listed all members.";
    public static final String MESSAGE_SUCCESS_SESSIONS = "Listed all sessions.";

    private final ListState targetState;

    /**
     * Creates a ListCommand that shows everything in the current list.
     */
    public ListCommand() {
        this.targetState = null;
    }

    /**
     * Creates a ListCommand that switches to {@code targetState} and shows everything in it.
     */
    public ListCommand(ListState targetState) {
        requireNonNull(targetState);
        this.targetState = targetState;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        if (targetState != null) {
            model.setListState(targetState);
        }

        return switch (model.getListState()) {
            case MEMBERS -> {
                model.updateFilteredMemberList(PREDICATE_SHOW_ALL_MEMBERS);
                yield new CommandResult(MESSAGE_SUCCESS);
            }
            case SESSIONS -> {
                model.updateFilteredSessionList(PREDICATE_SHOW_ALL_SESSIONS);
                yield new CommandResult(MESSAGE_SUCCESS_SESSIONS);
            }
        };
    }

    /**
     * Returns the list this command switches to, or an empty Optional if it keeps the current list.
     */
    public Optional<ListState> getTargetState() {
        return Optional.ofNullable(targetState);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ListCommand otherListCommand)) {
            return false;
        }

        return targetState == otherListCommand.targetState;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(targetState);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetState", targetState)
                .toString();
    }
}
