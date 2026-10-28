package seedu.hobbyhub.logic.commands.session;

import static java.util.Objects.requireNonNull;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_DATETIME;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_LOCATION;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_NAME;

import seedu.hobbyhub.commons.util.ToStringBuilder;
import seedu.hobbyhub.logic.commands.Command;
import seedu.hobbyhub.logic.commands.CommandResult;
import seedu.hobbyhub.logic.commands.exceptions.CommandException;
import seedu.hobbyhub.model.ListState;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.session.Session;

/** Adds a session to the HobbyHub. */
public class AddSessionCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a session to the HobbyHub.\n"
            + "Parameters: " + PREFIX_NAME + " SESSION_NAME "
            + PREFIX_LOCATION + " LOCATION "
            + PREFIX_DATETIME + " DATETIME (e.g. 2026-10-24 14:00 or 2026-10-24T14:00)\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_NAME + " Weekend Chess Meetup "
            + PREFIX_LOCATION + " Community Center " + PREFIX_DATETIME + " 2026-10-24T14:00";

    public static final String MESSAGE_SUCCESS = "New session added: %1$s";
    public static final String MESSAGE_DUPLICATE_SESSION = "A session with this name already exists.";
    public static final String MESSAGE_WRONG_LIST =
            "This command is only available in the sessions list. Use list -s to switch to sessions.";

    private final Session toAdd;

    /** Creates an {@code AddSessionCommand} to add the specified session. */
    public AddSessionCommand(Session session) {
        requireNonNull(session);
        toAdd = session;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.getListState() != ListState.SESSIONS) {
            throw new CommandException(MESSAGE_WRONG_LIST);
        }

        if (model.hasSession(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_SESSION);
        }

        model.addSession(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd.getName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddSessionCommand otherAddSessionCommand)) {
            return false;
        }

        return toAdd.equals(otherAddSessionCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
