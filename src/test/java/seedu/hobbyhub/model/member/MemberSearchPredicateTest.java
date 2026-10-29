package seedu.hobbyhub.model.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import java.time.DayOfWeek;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.model.member.MemberSearchPredicate.Field;
import seedu.hobbyhub.testutil.MemberBuilder;

public class MemberSearchPredicateTest {
    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MemberSearchPredicate(null, "Advanced"));
        assertThrows(NullPointerException.class, () -> new MemberSearchPredicate(Field.GROUP, null));
    }

    @Test
    public void equals_comparesFieldAndCaseInsensitiveQuery() {
        MemberSearchPredicate predicate = new MemberSearchPredicate(Field.GROUP, "Advanced");
        MemberSearchPredicate equivalent = new MemberSearchPredicate(Field.GROUP, "ADVANCED");

        assertTrue(predicate.equals(predicate));
        assertTrue(predicate.equals(equivalent));
        assertTrue(equivalent.equals(predicate));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals("Advanced"));
        assertFalse(predicate.equals(new MemberSearchPredicate(Field.SKILL_LEVEL, "Advanced")));
        assertFalse(predicate.equals(new MemberSearchPredicate(Field.GROUP, "Beginner")));
    }

    @Test
    public void hashCode_equalPredicates_workAsSetKeys() {
        MemberSearchPredicate predicate = new MemberSearchPredicate(Field.GROUP, "Advanced");
        MemberSearchPredicate equivalent = new MemberSearchPredicate(Field.GROUP, "advanced");

        assertEquals(predicate.hashCode(), equivalent.hashCode());
        assertTrue(Set.of(predicate).contains(equivalent));
    }

    @Test
    public void test_availability_matchesSelectedWeekday() {
        Member member = new MemberBuilder().withAvailability(DayOfWeek.MONDAY, DayOfWeek.SATURDAY).build();

        assertTrue(new MemberSearchPredicate(Field.AVAILABILITY, "monday").test(member));
        assertTrue(new MemberSearchPredicate(Field.AVAILABILITY, "SATURDAY").test(member));
        assertFalse(new MemberSearchPredicate(Field.AVAILABILITY, "Tuesday").test(member));
    }

    @Test
    public void toStringMethod() {
        MemberSearchPredicate predicate = new MemberSearchPredicate(Field.GROUP, "Weekend");
        String expected = MemberSearchPredicate.class.getCanonicalName() + "{field=GROUP, query=weekend}";
        assertEquals(expected, predicate.toString());
    }
}
