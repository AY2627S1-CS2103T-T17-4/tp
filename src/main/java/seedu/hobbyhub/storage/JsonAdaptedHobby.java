package seedu.hobbyhub.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.hobbyhub.commons.exceptions.IllegalValueException;
import seedu.hobbyhub.model.member.Hobby;
import seedu.hobbyhub.model.member.SkillLevel;

/**
 * Jackson-friendly hobby name and skill level.
 */
class JsonAdaptedHobby {
    public static final String MESSAGE_INVALID_SKILL = "Skill level must be Beginner, Intermediate, or Advanced.";

    private final String name;
    private final String skillLevel;

    @JsonCreator
    public JsonAdaptedHobby(@JsonProperty("name") String name, @JsonProperty("skillLevel") String skillLevel) {
        this.name = name;
        this.skillLevel = skillLevel;
    }

    public JsonAdaptedHobby(Hobby hobby) {
        name = hobby.value;
        skillLevel = hobby.getSkillLevel().toString();
    }

    /** Converts the stored name and skill level into a validated hobby. */
    public Hobby toModelType() throws IllegalValueException {
        if (name == null || !Hobby.isValidHobby(name)) {
            throw new IllegalValueException(Hobby.MESSAGE_CONSTRAINTS);
        }
        for (SkillLevel level : SkillLevel.values()) {
            if (level.toString().equalsIgnoreCase(skillLevel)) {
                return new Hobby(name, level);
            }
        }
        throw new IllegalValueException(MESSAGE_INVALID_SKILL);
    }
}
