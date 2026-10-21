package seedu.hobbyhub.model.member;

import static java.util.Objects.requireNonNull;
import static seedu.hobbyhub.commons.util.AppUtil.checkArgument;

/**
 * Represents a Member's phone number in the HobbyHub.
 * An empty value represents an optional field that has not been provided.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {


    public static final String MESSAGE_CONSTRAINTS =
            "When provided, phone numbers must contain only digits and be at least 3 digits long";
    public static final String VALIDATION_REGEX = "\\d{3,}";
    public final String value;

    /**
     * Constructs a {@code Phone}.
     *
     * @param phone A valid phone number, or an empty string for an absent value.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        value = phone;
    }

    /**
     * Returns true if a given string is a valid phone number.
     */
    public static boolean isValidPhone(String test) {
        return test.isEmpty() || test.matches(VALIDATION_REGEX);
    }

    /** Returns an absent optional phone, represented by an empty value. */
    public static Phone empty() {
        return new Phone("");
    }

    /** Returns whether this optional phone has not been provided. */
    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public String toString() {
        return isEmpty() ? "Not provided" : value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Phone otherPhone)) {
            return false;
        }

        return value.equals(otherPhone.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
