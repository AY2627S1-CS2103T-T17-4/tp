package seedu.hobbyhub.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import seedu.hobbyhub.commons.exceptions.IllegalValueException;
import seedu.hobbyhub.commons.util.JsonUtil;
import seedu.hobbyhub.model.group.Group;
import seedu.hobbyhub.model.member.Hobby;
import seedu.hobbyhub.model.member.SkillLevel;
import seedu.hobbyhub.testutil.MemberBuilder;
import seedu.hobbyhub.testutil.TypicalSessions;

public class GroupStorageTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void savedGroups_roundTripWithHobbiesSkillsAndSessionAssignments() throws Exception {
        var hub = TypicalSessions.getTypicalHobbyHubWithSessions();
        var member = hub.getMemberList().getFirst();
        hub.setMember(member, new MemberBuilder(member).withGroups("Weekend Club", "Volunteers")
                .withHobbies(new Hobby("Chess", SkillLevel.ADVANCED), new Hobby("Tennis")).build());
        String json = JsonUtil.toJsonString(new JsonSerializableHobbyHub(hub));
        assertTrue(json.contains("\"groups\""));
        assertFalse(json.contains("\"tags\""));
        assertEquals(hub, JsonUtil.fromJsonString(json, JsonSerializableHobbyHub.class).toModelType());
    }

    @Test
    public void legacyTags_migrateToGroupsWithoutLosingEitherField() throws Exception {
        String json = JsonUtil.toJsonString(new JsonAdaptedMember(new MemberBuilder().build()));
        ObjectNode member = (ObjectNode) mapper.readTree(json);
        member.remove("groups");
        member.putArray("tags").add("friends");
        assertEquals(Set.of(new Group("friends")), readMember(member).getGroups());
        member.putArray("groups").add("Weekend Club").add("friends");
        assertEquals(Set.of(new Group("friends"), new Group("Weekend Club")), readMember(member).getGroups());
        member.remove("groups");
        member.remove("tags");
        assertTrue(readMember(member).getGroups().isEmpty());
    }

    @Test
    public void invalidStoredGroupOrNullEntry_failsValidation() throws Exception {
        String json = JsonUtil.toJsonString(new JsonAdaptedMember(new MemberBuilder().build()));
        for (String groups : List.of("[null]", "[\"@Club\"]")) {
            ObjectNode member = (ObjectNode) mapper.readTree(json);
            member.set("groups", mapper.readTree(groups));
            assertThrows(IllegalValueException.class, () -> readMember(member));
        }
    }

    private seedu.hobbyhub.model.member.Member readMember(ObjectNode json) throws Exception {
        return JsonUtil.fromJsonString(json.toString(), JsonAdaptedMember.class).toModelType();
    }
}
