package seedu.hobbyhub.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.tag.Tag;

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
    public void getTagSet_returnsTagsForEachName() {
        assertEquals(Set.of(new Tag("chess"), new Tag("cycling")), SampleDataUtil.getTagSet("chess", "cycling"));
    }
}
