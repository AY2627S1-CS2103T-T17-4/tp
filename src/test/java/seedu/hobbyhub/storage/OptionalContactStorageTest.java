package seedu.hobbyhub.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import seedu.hobbyhub.commons.exceptions.IllegalValueException;
import seedu.hobbyhub.commons.util.JsonUtil;
import seedu.hobbyhub.testutil.MemberBuilder;

public class OptionalContactStorageTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void absentFields_areOmittedFromJsonAndReload() throws Exception {
        var member = new MemberBuilder().withPhone("").withAddress("").build();
        String json = JsonUtil.toJsonString(new JsonAdaptedMember(member));
        var tree = mapper.readTree(json);
        assertFalse(tree.has("phone"));
        assertFalse(tree.has("address"));
        assertEquals(member, JsonUtil.fromJsonString(json, JsonAdaptedMember.class).toModelType());
    }

    @Test
    public void nullOrEmptyStoredFields_loadAsAbsent() throws Exception {
        String base = JsonUtil.toJsonString(new JsonAdaptedMember(new MemberBuilder().build()));
        ObjectNode tree = (ObjectNode) mapper.readTree(base);
        tree.putNull("phone");
        tree.putNull("address");
        var nullFields = JsonUtil.fromJsonString(tree.toString(), JsonAdaptedMember.class).toModelType();
        assertTrue(nullFields.getPhone().isEmpty());
        assertTrue(nullFields.getAddress().isEmpty());
        tree.put("phone", "");
        tree.put("address", "");
        assertEquals(nullFields, JsonUtil.fromJsonString(tree.toString(), JsonAdaptedMember.class).toModelType());
    }

    @Test
    public void invalidNonEmptyFields_stillRejected() throws Exception {
        String base = JsonUtil.toJsonString(new JsonAdaptedMember(new MemberBuilder().build()));
        ObjectNode tree = (ObjectNode) mapper.readTree(base);
        tree.put("phone", "abc");
        var invalidPhone = JsonUtil.fromJsonString(tree.toString(), JsonAdaptedMember.class);
        assertThrows(IllegalValueException.class, invalidPhone::toModelType);
        tree.remove("phone");
        tree.put("address", " ");
        var invalidAddress = JsonUtil.fromJsonString(tree.toString(), JsonAdaptedMember.class);
        assertThrows(IllegalValueException.class, invalidAddress::toModelType);
    }
}
