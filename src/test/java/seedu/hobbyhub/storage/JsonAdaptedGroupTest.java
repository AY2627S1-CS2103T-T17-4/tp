package seedu.hobbyhub.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.commons.exceptions.IllegalValueException;
import seedu.hobbyhub.model.group.Group;

public class JsonAdaptedGroupTest {
    @Test
    public void validGroup_roundTrip() throws Exception {
        Group group = new Group("Weekend Club");
        JsonAdaptedGroup adapted = new JsonAdaptedGroup(group);
        assertEquals("Weekend Club", adapted.getGroupName());
        assertEquals(group, adapted.toModelType());
    }

    @Test
    public void invalidGroup_throwsIllegalValueException() {
        for (String value : new String[] {null, "", "@Club"}) {
            assertThrows(IllegalValueException.class, () -> new JsonAdaptedGroup(value).toModelType());
        }
    }
}
