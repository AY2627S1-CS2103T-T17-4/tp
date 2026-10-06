package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's primary hobby in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidHobby(String)}
 */
public class Hobby {

    public static final String MESSAGE_CONSTRAINTS =
            "Hobbies should only contain alphanumeric characters, spaces, and the following special characters: "
                    + "& + ' / -";

    /*
     * The first character of the hobby must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[\\p{Alnum}][\\p{Alnum} &+'/-]*";

    public final String value;

    /**
     * Constructs a {@code Hobby}.
     *
     * @param hobby A valid hobby.
     */
    public Hobby(String hobby) {
        requireNonNull(hobby);
        checkArgument(isValidHobby(hobby), MESSAGE_CONSTRAINTS);
        value = hobby;
    }

    /**
     * Returns true if a given string is a valid hobby.
     */
    public static boolean isValidHobby(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Hobby otherHobby)) {
            return false;
        }

        return value.equals(otherHobby.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
