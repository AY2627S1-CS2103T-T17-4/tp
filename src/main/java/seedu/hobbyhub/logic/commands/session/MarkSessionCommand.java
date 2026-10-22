package seedu.hobbyhub.logic.commands.session;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.commons.util.ToStringBuilder;
import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.logic.commands.Command;
import seedu.hobbyhub.logic.commands.CommandResult;
import seedu.hobbyhub.logic.commands.exceptions.CommandException;
import seedu.hobbyhub.model.ListState;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.session.Session;

/**
 * Marks a session in the displayed session list as completed ({@code mark}) or not completed ({@code unmark}).
 */
public class MarkSessionCommand extends Command {

    public static final String COMMAND_WORD = "mark";
    public static final String UNMARK_COMMAND_WORD = "unmark";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks the session identified by its index in the displayed session list as completed.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";
    public static final String MESSAGE_UNMARK_USAGE = UNMARK_COMMAND_WORD
            + ": Marks the session identified by its index in the displayed session list as not completed.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + UNMARK_COMMAND_WORD + " 1";

    public static final String MESSAGE_MARK_SUCCESS = "Marked session as completed: %1$s";
    public static final String MESSAGE_UNMARK_SUCCESS = "Marked session as not completed: %1$s";
    public static final String MESSAGE_ALREADY_COMPLETED = "Session \"%1$s\" is already marked as completed.";
    public static final String MESSAGE_ALREADY_NOT_COMPLETED = "Session \"%1$s\" is already marked as not completed.";

    private final Index index;
    private final boolean isCompleted;

    /**
     * Creates a command that sets the completion status of the session at {@code index} to {@code isCompleted}.
     */
    public MarkSessionCommand(Index index, boolean isCompleted) {
        requireNonNull(index);
        this.index = index;
        this.isCompleted = isCompleted;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (model.getListState() != ListState.SESSIONS) {
            throw new CommandException(String.format(Messages.MESSAGE_SESSION_LIST_REQUIRED,
                    isCompleted ? COMMAND_WORD : UNMARK_COMMAND_WORD));
        }

        List<Session> lastShownList = model.getFilteredSessionList();
        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_SESSION_DISPLAYED_INDEX);
        }

        Session sessionToMark = lastShownList.get(index.getZeroBased());
        if (sessionToMark.isCompleted() == isCompleted) {
            String message = isCompleted ? MESSAGE_ALREADY_COMPLETED : MESSAGE_ALREADY_NOT_COMPLETED;
            throw new CommandException(String.format(message, sessionToMark.getName()));
        }

        model.setSession(sessionToMark, sessionToMark.withCompleted(isCompleted));
        String message = isCompleted ? MESSAGE_MARK_SUCCESS : MESSAGE_UNMARK_SUCCESS;
        return new CommandResult(String.format(message, sessionToMark.getName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof MarkSessionCommand otherMarkSessionCommand)) {
            return false;
        }

        return index.equals(otherMarkSessionCommand.index)
                && isCompleted == otherMarkSessionCommand.isCompleted;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index.getZeroBased(), isCompleted);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("isCompleted", isCompleted)
                .toString();
    }
}
