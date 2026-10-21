package seedu.hobbyhub.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.commons.exceptions.IllegalValueException;
import seedu.hobbyhub.model.member.Hobby;
import seedu.hobbyhub.model.member.SkillLevel;

public class JsonAdaptedHobbyTest {
    @Test
    public void toModelType_validSkills_roundTrip() throws Exception {
        for (SkillLevel level : SkillLevel.values()) {
            Hobby hobby = new Hobby("Chess", level);
            assertEquals(hobby, new JsonAdaptedHobby(hobby).toModelType());
            assertEquals(hobby, new JsonAdaptedHobby("Chess", level.toString().toLowerCase()).toModelType());
        }
    }

    @Test
    public void toModelType_invalidNameOrSkill_rejected() {
        for (String name : new String[] {null, "", "@Chess"}) {
            assertThrows(IllegalValueException.class, () -> new JsonAdaptedHobby(name, "Beginner").toModelType());
        }
        for (String skill : new String[] {null, "", "Expert"}) {
            assertThrows(IllegalValueException.class, JsonAdaptedHobby.MESSAGE_INVALID_SKILL, ()
                -> new JsonAdaptedHobby("Chess", skill).toModelType());
        }
    }
}
