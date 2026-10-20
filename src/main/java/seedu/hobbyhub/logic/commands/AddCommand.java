package seedu.hobbyhub.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_HOBBY;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_TAG;

import seedu.hobbyhub.commons.util.ToStringBuilder;
import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.logic.commands.exceptions.CommandException;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.member.Member;

/**
 * Adds a member to the HobbyHub.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a member to the HobbyHub. "
            + "Parameters: "
            + PREFIX_NAME + " NAME "
            + PREFIX_PHONE + " PHONE "
            + PREFIX_EMAIL + " EMAIL "
            + PREFIX_ADDRESS + " ADDRESS "
            + PREFIX_HOBBY + " HOBBY "
            + "[" + PREFIX_TAG + " TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + " John Doe "
            + PREFIX_PHONE + " 98765432 "
            + PREFIX_EMAIL + " johnd@example.com "
            + PREFIX_ADDRESS + " 311, Clementi Ave 2, #02-25 "
            + PREFIX_HOBBY + " Chess "
            + PREFIX_TAG + " friends "
            + PREFIX_TAG + " owesMoney";

    public static final String MESSAGE_SUCCESS = "New member added: %1$s";
    public static final String MESSAGE_DUPLICATE_MEMBER = "A member with this email already exists.";

    private final Member toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Member}
     */
    public AddCommand(Member member) {
        requireNonNull(member);
        toAdd = member;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasMember(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_MEMBER);
        }

        model.addMember(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand otherAddCommand)) {
            return false;
        }

        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
