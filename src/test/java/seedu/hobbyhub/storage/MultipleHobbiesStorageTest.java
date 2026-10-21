package seedu.hobbyhub.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import seedu.hobbyhub.commons.exceptions.IllegalValueException;
import seedu.hobbyhub.commons.util.JsonUtil;
import seedu.hobbyhub.model.HobbyHub;
import seedu.hobbyhub.model.member.Hobby;
import seedu.hobbyhub.model.member.SkillLevel;
import seedu.hobbyhub.testutil.MemberBuilder;
import seedu.hobbyhub.testutil.TypicalSessions;

public class MultipleHobbiesStorageTest {
    @Test
    public void jsonRoundTrip_preservesHobbiesSkillsAndSessions() throws Exception {
        HobbyHub hub = TypicalSessions.getTypicalHobbyHubWithSessions();
        var original = hub.getMemberList().getFirst();
        var edited = new MemberBuilder(original).withHobbies(new Hobby("Chess", SkillLevel.ADVANCED),
                new Hobby("Cycling", SkillLevel.INTERMEDIATE)).build();
        hub.setMember(original, edited);
        String json = JsonUtil.toJsonString(new JsonSerializableHobbyHub(hub));
        assertFalse(json.contains("\"hobby\""));
        assertTrue(json.contains("\"hobbies\""));
        assertEquals(hub, JsonUtil.fromJsonString(json, JsonSerializableHobbyHub.class).toModelType());
    }

    @Test
    public void legacyJson_loadsHobbyAtBeginner() throws Exception {
        String json = JsonUtil.toJsonString(new JsonAdaptedMember(new MemberBuilder().build()));
        var tree = new ObjectMapper().readTree(json);
        var object = (ObjectNode) tree;
        object.remove("hobbies");
        object.put("hobby", "Chess");
        var member = JsonUtil.fromJsonString(object.toString(), JsonAdaptedMember.class).toModelType();
        assertEquals(List.of(new Hobby("Chess", SkillLevel.BEGINNER)), member.getHobbies());
    }

    @Test
    public void invalidHobbyLists_rejected() throws Exception {
        var mapper = new ObjectMapper();
        String base = JsonUtil.toJsonString(new JsonAdaptedMember(new MemberBuilder().build()));
        for (String hobbies : new String[] {"[]", "[null]",
            "[{\"name\":\"Chess\",\"skillLevel\":\"Expert\"}]",
            "[{\"name\":\"Chess\",\"skillLevel\":\"Beginner\"},"
                + "{\"name\":\"chess\",\"skillLevel\":\"Advanced\"}]"}) {
            var object = (ObjectNode) mapper.readTree(base);
            object.set("hobbies", mapper.readTree(hobbies));
            var adapted = JsonUtil.fromJsonString(object.toString(), JsonAdaptedMember.class);
            assertThrows(IllegalValueException.class, adapted::toModelType);
        }
    }
}
