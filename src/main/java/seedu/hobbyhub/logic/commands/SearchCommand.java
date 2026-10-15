package seedu.hobbyhub.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.hobbyhub.commons.util.ToStringBuilder;
import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.member.Member;

/**
 * Searches and lists all members in the HobbyHub matching the selected search predicate.
 */
public class SearchCommand extends Command {

    public static final String COMMAND_WORD = "search";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Searches for all members whose names/hobbies "
            + "contain the specified fragment (case-insensitive) and displays them as a list "
            + "with index numbers.\n"
            + "Parameters: -n NAME_FRAGMENT | -h HOBBY_FRAGMENT\n"
            + "Example: " + COMMAND_WORD + " -n alice";

    private final Predicate<Member> predicate;

    public SearchCommand(Predicate<Member> predicate) {
        this.predicate = requireNonNull(predicate);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredMemberList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_MEMBERS_LISTED_OVERVIEW, model.getFilteredMemberList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof SearchCommand otherSearchCommand)) {
            return false;
        }

        return predicate.equals(otherSearchCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
