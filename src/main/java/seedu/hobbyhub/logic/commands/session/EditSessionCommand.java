package seedu.hobbyhub.logic.commands.session;

import static java.util.Objects.requireNonNull;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_DATETIME;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_LOCATION;
import static seedu.hobbyhub.model.Model.PREDICATE_SHOW_ALL_SESSIONS;

import java.time.LocalDateTime;
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

/** Edits a session's location and/or date and time. */
public class EditSessionCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the session identified by its index in the "
            + "displayed session list. At least one field must be supplied.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_LOCATION + " LOCATION] "
            + "[" + PREFIX_DATETIME + " DATETIME (ISO-8601, e.g. 2026-10-24T14:00)]\n"
            + "Example: " + COMMAND_WORD + " 1 " + PREFIX_LOCATION + " Community Center "
            + PREFIX_DATETIME + " 2026-10-24T14:00";

    public static final String MESSAGE_SUCCESS = "Edited session: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_WRONG_LIST =
            "This command is only available in the sessions list. Use list -s to switch to sessions.";

    private final Index index;
    private final String location;
    private final LocalDateTime dateTime;

    /** Creates a command to edit the supplied session fields. */
    public EditSessionCommand(Index index, String location, LocalDateTime dateTime) {
        requireNonNull(index);
        if (location == null && dateTime == null) {
            throw new IllegalArgumentException(MESSAGE_NOT_EDITED);
        }
        this.index = index;
        this.location = location;
        this.dateTime = dateTime;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (model.getListState() != ListState.SESSIONS) {
            throw new CommandException(MESSAGE_WRONG_LIST);
        }

        if (index.getZeroBased() >= model.getFilteredSessionList().size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_SESSION_DISPLAYED_INDEX);
        }

        Session sessionToEdit = model.getFilteredSessionList().get(index.getZeroBased());
        Session editedSession = createEditedSession(sessionToEdit);
        model.setSession(sessionToEdit, editedSession);
        model.updateFilteredSessionList(PREDICATE_SHOW_ALL_SESSIONS);
        return new CommandResult(String.format(MESSAGE_SUCCESS, editedSession.getName()));
    }

    /** Creates a session with supplied values and all other details copied from the original. */
    private Session createEditedSession(Session sessionToEdit) {
        return new Session(
                sessionToEdit.getName(),
                dateTime == null ? sessionToEdit.getDate() : dateTime,
                location == null ? sessionToEdit.getLocation() : location,
                sessionToEdit.isCompleted(),
                sessionToEdit.getMembers());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof EditSessionCommand otherEditSessionCommand)) {
            return false;
        }
        return index.equals(otherEditSessionCommand.index)
                && Objects.equals(location, otherEditSessionCommand.location)
                && Objects.equals(dateTime, otherEditSessionCommand.dateTime);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("location", location)
                .add("dateTime", dateTime)
                .toString();
    }
}
