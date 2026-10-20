package seedu.hobbyhub.model.session;

import static java.util.Objects.requireNonNull;
import static seedu.hobbyhub.commons.util.AppUtil.checkArgument;

/**
 * Represents a Session's name in the HobbyHub.
 * Session names are unique, and two names are considered equal if they match ignoring case.
 * Guarantees: immutable; is valid as declared in {@link #isValidSessionName(String)}
 */
public class SessionName {

    public static final int MAX_LENGTH = 50;

    public static final String MESSAGE_CONSTRAINTS = "Session names should start with a letter or digit, "
            + "contain only letters, digits, spaces and the characters _ - ' &, "
            + "and be at most " + MAX_LENGTH + " characters long";

    /*
     * The first character must be a letter or digit, so that a blank name or a name that
     * looks like an option (e.g. "-n") is not valid.
     */
    public static final String VALIDATION_REGEX = "[\\p{Alnum}][\\p{Alnum} _'&-]*";

    public final String fullName;

    /**
     * Constructs a {@code SessionName}.
     *
     * @param name A valid session name.
     */
    public SessionName(String name) {
        requireNonNull(name);
        checkArgument(isValidSessionName(name), MESSAGE_CONSTRAINTS);
        fullName = name;
    }

    /**
     * Returns true if a given string is a valid session name.
     */
    public static boolean isValidSessionName(String test) {
        requireNonNull(test);
        return test.length() <= MAX_LENGTH && test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof SessionName otherName)) {
            return false;
        }

        return fullName.equalsIgnoreCase(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.toLowerCase().hashCode();
    }

}
