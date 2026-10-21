package seedu.hobbyhub.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.hobbyhub.logic.parser.Prefix;
import seedu.hobbyhub.model.member.Member;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_MEMBER_DISPLAYED_INDEX = "The member index provided is invalid.";
    public static final String MESSAGE_INVALID_SESSION_DISPLAYED_INDEX = "The session index provided is invalid.";
    public static final String MESSAGE_MEMBERS_LISTED_OVERVIEW = "%1$d member(s) listed!";
    public static final String MESSAGE_SESSION_LIST_REQUIRED =
            "The %1$s command is only available while the session list is shown. Use list -s to switch to it.";
    public static final String MESSAGE_SESSION_COMMAND_UNAVAILABLE =
            "The %1$s command is not available for sessions yet. Use list -m to switch to the member list.";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code member} for display to the user.
     */
    public static String format(Member member) {
        final StringBuilder builder = new StringBuilder();
        builder.append(member.getName())
                .append("; Phone: ")
                .append(member.getPhone())
                .append("; Email: ")
                .append(member.getEmail())
                .append("; Address: ")
                .append(member.getAddress())
                .append("; Hobbies: ")
                .append(member.getHobbies().stream().map(Object::toString).collect(Collectors.joining(", ")))
                .append("; Groups: ");
        member.getGroups().forEach(builder::append);
        return builder.toString();
    }

}
