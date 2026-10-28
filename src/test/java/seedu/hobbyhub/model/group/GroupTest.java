package seedu.hobbyhub.model.group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.testutil.MemberBuilder;

public class GroupTest {
    @Test
    public void constructor_invalidName_rejected() {
        assertThrows(NullPointerException.class, () -> new Group(null));
        for (String name : new String[] {"", " ", " Leading", "@Club", "Club:Advanced", "Club*name"}) {
            assertFalse(Group.isValidGroupName(name));
            assertThrows(IllegalArgumentException.class, () -> new Group(name));
        }
        assertThrows(NullPointerException.class, () -> Group.isValidGroupName(null));
    }

    @Test
    public void validNamesAndEquality() {
        Group group = new Group("Weekend Club");
        assertTrue(Group.isValidGroupName("Weekend Club"));
        assertTrue(Group.isValidGroupName("Club123"));
        assertTrue(Group.isValidGroupName("Parents & Friends"));
        assertTrue(Group.isValidGroupName("C++ Club"));
        assertTrue(Group.isValidGroupName("O'Connor Club"));
        assertTrue(Group.isValidGroupName("Anne-Marie Club"));
        assertTrue(Group.isValidGroupName("Jos\u00e9 Club"));
        assertEquals(group, new Group("Weekend Club"));
        assertEquals(group.hashCode(), new Group("Weekend Club").hashCode());
        assertTrue(group.equals(group));
        assertFalse(group.equals(new Group("weekend club")));
        assertFalse(group.equals(null));
        assertFalse(group.equals("Weekend Club"));
        assertEquals("[Weekend Club]", group.toString());
    }

    @Test
    public void member_copiesGroupsAndExposesImmutableSet() {
        var base = new MemberBuilder().build();
        Set<Group> groups = new HashSet<>(Set.of(new Group("Weekend Club")));
        var member = new seedu.hobbyhub.model.member.Member(base.getName(), base.getPhone(), base.getEmail(),
                base.getAddress(), base.getHobbies(), groups);
        groups.clear();
        assertEquals(Set.of(new Group("Weekend Club")), member.getGroups());
        assertThrows(UnsupportedOperationException.class, () -> member.getGroups().clear());
    }
}
