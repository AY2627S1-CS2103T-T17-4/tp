package seedu.hobbyhub.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.hobbyhub.model.Model.PREDICATE_SHOW_ALL_MEMBERS;

import seedu.hobbyhub.model.Model;

/**
 * Lists all members in the HobbyHub to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_SUCCESS = "Listed all members.";


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredMemberList(PREDICATE_SHOW_ALL_MEMBERS);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
