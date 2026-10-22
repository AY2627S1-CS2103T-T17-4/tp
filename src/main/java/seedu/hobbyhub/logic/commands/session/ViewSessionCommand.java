package seedu.hobbyhub.logic.commands.session;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.commons.util.ToStringBuilder;
import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.logic.commands.Command;
import seedu.hobbyhub.logic.commands.CommandResult;
import seedu.hobbyhub.logic.commands.ViewCommand;
import seedu.hobbyhub.logic.commands.exceptions.CommandException;
import seedu.hobbyhub.model.ListState;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.session.Session;

/**
 * Views details of a session identified using its displayed index.
 */
public class ViewSessionCommand extends Command {

    public static final String COMMAND_WORD = ViewCommand.COMMAND_WORD;

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows details of the session identified by the index number used in the displayed session list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_WRONG_LIST =
            "This command is only available in the sessions list. Use list -s to switch to sessions.";

    private final Index targetIndex;

    /** Creates a command to view the session at {@code targetIndex}. */
    public ViewSessionCommand(Index targetIndex) {
        requireNonNull(targetIndex);
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (model.getListState() != ListState.SESSIONS) {
            throw new CommandException(MESSAGE_WRONG_LIST);
        }

        List<Session> lastShownList = model.getFilteredSessionList();
        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_SESSION_DISPLAYED_INDEX);
        }

        return new CommandResult(formatSession(lastShownList.get(targetIndex.getZeroBased())));
    }

    /**
     * Format string on additional details when viewing {@code session} details
     *
     * @param s A particular session
     * @return A string of the details of the session, inclusive of it's participants.
     */
    public static String formatSession(Session s) {
        String members = s.getMembers().isEmpty()
            ? "none"
            : s.getMembers().stream()
            .map(email -> email.value)
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .map(email -> "  - " + email)
            .collect(Collectors.joining("\n", "\n", ""));

        return "Name: " + s.getName() + "\n"
            + "Date: " + s.getDate() + "\n"
            + "Location: " + s.getLocation() + "\n"
            + "Completed: " + (s.isCompleted() ? "yes" : "no") + "\n"
            + "Members (" + s.getMembers().size() + "):" + (s.getMembers().isEmpty() ? " none" : members);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ViewSessionCommand otherViewSessionCommand)) {
            return false;
        }
        return targetIndex.equals(otherViewSessionCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
