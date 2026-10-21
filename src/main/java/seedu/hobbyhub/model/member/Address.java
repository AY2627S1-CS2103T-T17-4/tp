package seedu.hobbyhub.model.member;

import static java.util.Objects.requireNonNull;
import static seedu.hobbyhub.commons.util.AppUtil.checkArgument;

/**
 * Represents a Member's address in the HobbyHub.
 * An empty value represents an optional field that has not been provided.
 * Guarantees: immutable; is valid as declared in {@link #isValidAddress(String)}
 */
public class Address {

    public static final String MESSAGE_CONSTRAINTS =
            "When provided, addresses can take any value but must not be blank";

    /*
     * The first character of the address must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[^\\s].*";

    public final String value;

    /**
     * Constructs an {@code Address}.
     *
     * @param address A valid address, or an empty string for an absent value.
     */
    public Address(String address) {
        requireNonNull(address);
        checkArgument(isValidAddress(address), MESSAGE_CONSTRAINTS);
        value = address;
    }

    /**
     * Returns true if a given string is a valid address.
     */
    public static boolean isValidAddress(String test) {
        return test.isEmpty() || test.matches(VALIDATION_REGEX);
    }

    /** Returns an absent optional address, represented by an empty value. */
    public static Address empty() {
        return new Address("");
    }

    /** Returns whether this optional address has not been provided. */
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
        if (!(other instanceof Address otherAddress)) {
            return false;
        }

        return value.equals(otherAddress.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
