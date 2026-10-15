package seedu.hobbyhub.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.hobbyhub.commons.util.ToStringBuilder;
import seedu.hobbyhub.logic.commands.exceptions.CommandException;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.member.Email;
import seedu.hobbyhub.model.member.EmailMatchesPredicate;
import seedu.hobbyhub.model.member.Member;

/**
 * Removes a member identified by their contact email from the entire HobbyHub.
 */
public class RemoveCommand extends Command {
    public static final String COMMAND_WORD = "remove";
    public static final String MESSAGE_USAGE = "remove -c EMAIL: Removes the member with the given contact email.";
    public static final String MESSAGE_SUCCESS = "Member removed: %1$s (%2$s)";
    public static final String MESSAGE_NOT_FOUND = "No member found with contact %1$s.";
    public static final String MESSAGE_AMBIGUOUS =
            "Multiple members found with contact %1$s. No member was removed. Use delete INDEX to resolve duplicates.";

    private final Email email;

    public RemoveCommand(Email email) {
        this.email = requireNonNull(email);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Member> matches = model.getHobbyHub().getMemberList().stream()
                .filter(new EmailMatchesPredicate(email)).toList();
        if (matches.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_NOT_FOUND, email));
        }
        if (matches.size() > 1) {
            throw new CommandException(String.format(MESSAGE_AMBIGUOUS, email));
        }
        Member member = matches.getFirst();
        model.deleteMember(member);
        return new CommandResult(String.format(MESSAGE_SUCCESS, member.getName(), member.getEmail()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof RemoveCommand otherCommand && email.equals(otherCommand.email);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("email", email).toString();
    }
}
