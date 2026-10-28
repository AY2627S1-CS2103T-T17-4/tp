package seedu.hobbyhub.model.member;

import static java.util.Objects.requireNonNull;
import static seedu.hobbyhub.commons.util.AppUtil.checkArgument;

import java.util.Objects;

/**
 * Represents a hobby name and the member's skill level in that hobby.
 * Guarantees: immutable; is valid as declared in {@link #isValidHobby(String)}
 */
public class Hobby {

    public static final String MESSAGE_CONSTRAINTS =
            "Hobbies should start with a letter or digit and contain only Unicode letters, digits, combining marks, "
                    + "spaces, and the following special characters: & + ' / -";

    /*
     * The first character of the hobby must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[\\p{L}\\p{N}][\\p{L}\\p{N}\\p{M} &+'/-]*";

    public final String value;
    private final SkillLevel skillLevel;

    /**
     * Constructs a {@code Hobby}.
     *
     * @param hobby A valid hobby.
     */
    public Hobby(String hobby) {
        this(hobby, SkillLevel.BEGINNER);
    }

    /**
     * Constructs a hobby with an explicit skill level.
     */
    public Hobby(String hobby, SkillLevel skillLevel) {
        requireNonNull(hobby);
        this.skillLevel = requireNonNull(skillLevel);
        checkArgument(isValidHobby(hobby), MESSAGE_CONSTRAINTS);
        value = hobby;
    }

    /**
     * Returns true if a given string is a valid hobby.
     */
    public static boolean isValidHobby(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    public SkillLevel getSkillLevel() {
        return skillLevel;
    }

    @Override
    public String toString() {
        return value + " (" + skillLevel + ")";
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

        return value.equals(otherHobby.value) && skillLevel == otherHobby.skillLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, skillLevel);
    }

}
