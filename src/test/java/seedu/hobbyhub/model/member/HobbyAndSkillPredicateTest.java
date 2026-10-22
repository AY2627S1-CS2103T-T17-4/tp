package seedu.hobbyhub.model.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.testutil.MemberBuilder;

public class HobbyAndSkillPredicateTest {
    private final Member member = new MemberBuilder().withHobbies(
            new Hobby("Chess", SkillLevel.BEGINNER), new Hobby("Tennis", SkillLevel.ADVANCED)).build();

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new HobbyAndSkillPredicate(null, SkillLevel.BEGINNER));
        assertThrows(NullPointerException.class, () -> new HobbyAndSkillPredicate("Chess", null));
    }

    @Test
    public void test_requiresNameAndSkillOnSameHobby() {
        assertTrue(new HobbyAndSkillPredicate("CHES", SkillLevel.BEGINNER).test(member));
        assertTrue(new HobbyAndSkillPredicate("ten", SkillLevel.ADVANCED).test(member));
        assertTrue(new HobbyAndSkillPredicate("e", SkillLevel.ADVANCED).test(member));
        assertFalse(new HobbyAndSkillPredicate("Chess", SkillLevel.ADVANCED).test(member));
        assertFalse(new HobbyAndSkillPredicate("Painting", SkillLevel.ADVANCED).test(member));
        assertFalse(new HobbyAndSkillPredicate("Tennis", SkillLevel.INTERMEDIATE).test(member));
    }

    @Test
    public void equalsAndHashCode_compareHobbyAndSkill() {
        HobbyAndSkillPredicate predicate = new HobbyAndSkillPredicate("Chess", SkillLevel.BEGINNER);
        HobbyAndSkillPredicate equivalent = new HobbyAndSkillPredicate("CHESS", SkillLevel.BEGINNER);
        assertTrue(predicate.equals(predicate));
        assertTrue(predicate.equals(equivalent));
        assertTrue(equivalent.equals(predicate));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals("Chess"));
        assertFalse(predicate.equals(new HobbyAndSkillPredicate("Tennis", SkillLevel.BEGINNER)));
        assertFalse(predicate.equals(new HobbyAndSkillPredicate("Chess", SkillLevel.ADVANCED)));
        assertEquals(predicate.hashCode(), equivalent.hashCode());
        assertTrue(Set.of(predicate).contains(equivalent));
    }

    @Test
    public void toStringMethod() {
        HobbyAndSkillPredicate predicate = new HobbyAndSkillPredicate("Chess", SkillLevel.BEGINNER);
        assertEquals(HobbyAndSkillPredicate.class.getCanonicalName() + "{hobbyQuery=chess, skillLevel=Beginner}",
                predicate.toString());
    }
}
