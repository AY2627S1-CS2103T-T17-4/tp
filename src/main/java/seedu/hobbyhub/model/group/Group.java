package seedu.hobbyhub.model.group;

import static java.util.Objects.requireNonNull;
import static seedu.hobbyhub.commons.util.AppUtil.checkArgument;

/**
 * Represents a Group in the HobbyHub.
 * Guarantees: immutable; name is valid as declared in {@link #isValidGroupName(String)}
 */
public class Group {

    public static final String MESSAGE_CONSTRAINTS = "Group names must start with a letter or digit and contain "
            + "only letters, digits, combining marks, spaces, apostrophes, hyphens, periods, ampersands, or plus signs";
    public static final String VALIDATION_REGEX = "[\\p{L}\\p{N}][\\p{L}\\p{N}\\p{M} '&+.’-]*";

    public final String groupName;

    /**
     * Constructs a {@code Group}.
     *
     * @param groupName A valid group name.
     */
    public Group(String groupName) {
        requireNonNull(groupName);
        checkArgument(isValidGroupName(groupName), MESSAGE_CONSTRAINTS);
        this.groupName = groupName;
    }

    /**
     * Returns true if a given string is a valid group name.
     */
    public static boolean isValidGroupName(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Group otherGroup)) {
            return false;
        }

        return groupName.equals(otherGroup.groupName);
    }

    @Override
    public int hashCode() {
        return groupName.hashCode();
    }

    /**
     * Formats state as text for viewing.
     */
    public String toString() {
        return '[' + groupName + ']';
    }

}
