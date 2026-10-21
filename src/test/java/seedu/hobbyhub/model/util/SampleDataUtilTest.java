package seedu.hobbyhub.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.model.group.Group;
import seedu.hobbyhub.model.member.Member;

public class SampleDataUtilTest {

    @Test
    public void constructor_createsUtilityInstance() {
        assertEquals(SampleDataUtil.class, new SampleDataUtil().getClass());
    }

    @Test
    public void getSampleMembers_returnsExpectedMembers() {
        Member[] sampleMembers = SampleDataUtil.getSampleMembers();

        assertEquals(6, sampleMembers.length);
        assertEquals("Alex Yeoh", sampleMembers[0].getName().fullName);
        assertEquals("Chess", sampleMembers[0].getHobby().value);
    }

    @Test
    public void getSampleHobbyHub_containsAllSampleMembers() {
        List<Member> sampleMembers = List.of(SampleDataUtil.getSampleMembers());

        assertIterableEquals(sampleMembers, SampleDataUtil.getSampleHobbyHub().getMemberList());
    }

    @Test
    public void getGroupSet_returnsGroupsForEachName() {
        assertEquals(Set.of(new Group("chess"), new Group("cycling")), SampleDataUtil.getGroupSet("chess", "cycling"));
    }
}
