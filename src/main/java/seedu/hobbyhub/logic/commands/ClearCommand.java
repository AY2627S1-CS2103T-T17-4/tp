package seedu.hobbyhub.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.hobbyhub.model.HobbyHub;
import seedu.hobbyhub.model.Model;

/**
 * Clears the HobbyHub.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "Address book has been cleared!";


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.setHobbyHub(new HobbyHub());
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
