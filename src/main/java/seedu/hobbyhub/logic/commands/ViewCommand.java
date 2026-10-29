package seedu.hobbyhub.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.commons.util.ToStringBuilder;
import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.logic.commands.exceptions.CommandException;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.member.Member;

/**
 * Views details of a member or session identified using its displayed index,
 * depending on which list is currently shown.
 */
public class ViewCommand extends Command {

    public static final String COMMAND_WORD = "view";

    public static final String MESSAGE_USAGE = COMMAND_WORD
        + ": Shows details of the member identified by the index number used in the displayed member list.\n"
        + "Parameters: INDEX (must be a positive integer)\n"
        + "Example: " + COMMAND_WORD + " 1";

    private final Index targetIndex;

    /** Selects {@code targetIndex} for viewing */
    public ViewCommand(Index targetIndex) {
        requireNonNull(targetIndex);
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Member> lastShownList = model.getFilteredMemberList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_MEMBER_DISPLAYED_INDEX);
        }

        Member member = lastShownList.get(targetIndex.getZeroBased());
        return new CommandResult(formatMember(member), createViewDetails(member));
    }

    /** Creates structured UI details for the supplied member. */
    public static ViewDetails createViewDetails(Member member) {
        String hobbies = member.getHobbies().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        String groups = member.getGroups().isEmpty()
                ? "None"
                : member.getGroups().stream().map(Object::toString).sorted(String.CASE_INSENSITIVE_ORDER)
                        .collect(Collectors.joining(", "));
        String sessions = member.getSessions().isEmpty()
                ? "None"
                : member.getSessions().stream()
                        .map(session -> session.getName() + " (" + session.getDate() + " @ "
                                + session.getLocation() + ")")
                        .collect(Collectors.joining("\n"));

        return new ViewDetails(member.getName().fullName, member.getEmail().value, List.of(
                new ViewDetails.Field("Phone", member.getPhone().toString()),
                new ViewDetails.Field("Address", member.getAddress().toString()),
                new ViewDetails.Field("Hobbies", hobbies),
                new ViewDetails.Field("Groups", groups),
                new ViewDetails.Field("Availability", member.getAvailability().toString()),
                new ViewDetails.Field("Sessions", sessions)), ViewDetails.RecordType.MEMBER,
                member.getEmail().value);
    }

    /**
     * Format string on additional details when viewing {@code member} details
     *
     * @param m A particular member
     * @return A string of the details of the member
     */
    public static String formatMember(Member m) {
        String hobbies = m.getHobbies().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        String groups = m.getGroups().isEmpty()
                ? "none"
                : m.getGroups().stream().map(Object::toString).collect(Collectors.joining(" "));
        String sessions = m.getSessions().isEmpty()
                ? "none"
                : m.getSessions().stream()
                        .map(s -> s.getName() + " (" + s.getDate() + " @ "
                            + s.getLocation() + ")" + (s.isCompleted() ? " -- Done" : ""))
                        .collect(Collectors.joining("; "));

        return "Name: " + m.getName() + "\n"
                + "Phone: " + m.getPhone() + "\n"
                + "Email: " + m.getEmail() + "\n"
                + "Address: " + m.getAddress() + "\n"
                + "Hobbies: " + hobbies + "\n"
                + "Groups: " + groups + "\n"
                + "Availability: " + m.getAvailability() + "\n"
                + "Sessions: " + sessions;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ViewCommand otherViewCommand)) {
            return false;
        }

        return targetIndex.equals(otherViewCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
