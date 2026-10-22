package seedu.hobbyhub.logic.commands.session;

import static java.util.Objects.requireNonNull;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.commons.util.ToStringBuilder;
import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.logic.commands.Command;
import seedu.hobbyhub.logic.commands.CommandResult;
import seedu.hobbyhub.logic.commands.exceptions.CommandException;
import seedu.hobbyhub.model.ListState;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.session.Session;

/** Deletes a session identified using its displayed index. */
public class DeleteSessionCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the session identified by the index number used in the displayed session list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_SUCCESS = "Deleted session: %1$s";
    public static final String MESSAGE_WRONG_LIST =
            "This command is only available in the sessions list. Use list -s to switch to sessions.";

    private final Index targetIndex;

    /** Creates a command to delete the session at the supplied displayed index. */
    public DeleteSessionCommand(Index targetIndex) {
        requireNonNull(targetIndex);
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (model.getListState() != ListState.SESSIONS) {
            throw new CommandException(MESSAGE_WRONG_LIST);
        }

        if (targetIndex.getZeroBased() >= model.getFilteredSessionList().size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_SESSION_DISPLAYED_INDEX);
        }

        Session sessionToDelete = model.getFilteredSessionList().get(targetIndex.getZeroBased());
        model.deleteSession(sessionToDelete);
        return new CommandResult(String.format(MESSAGE_SUCCESS, sessionToDelete.getName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DeleteSessionCommand otherDeleteSessionCommand)) {
            return false;
        }
        return targetIndex.equals(otherDeleteSessionCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
