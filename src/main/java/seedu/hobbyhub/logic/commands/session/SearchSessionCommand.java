package seedu.hobbyhub.logic.commands.session;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.hobbyhub.commons.util.ToStringBuilder;
import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.logic.commands.Command;
import seedu.hobbyhub.logic.commands.CommandResult;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.session.Session;

/**
 * Filters the session list without changing stored sessions or the active list.
 */
public class SearchSessionCommand extends Command {
    public static final String MESSAGE_USAGE = "search: Filters sessions using exactly one option.\n"
            + "Names and locations use case-insensitive partial matching; participant emails match in full.\n"
            + "Parameters: -n NAME_FRAGMENT | -l LOCATION_FRAGMENT | -t YYYY-MM-DD "
            + "| -done true/false | -c PARTICIPANT_EMAIL\n"
            + "Example: search -t 2026-10-24";

    private final Predicate<Session> predicate;

    public SearchSessionCommand(Predicate<Session> predicate) {
        this.predicate = requireNonNull(predicate);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredSessionList(predicate);
        return new CommandResult(String.format(Messages.MESSAGE_SESSIONS_LISTED_OVERVIEW,
                model.getFilteredSessionList().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof SearchSessionCommand otherCommand
                && predicate.equals(otherCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("predicate", predicate).toString();
    }
}
