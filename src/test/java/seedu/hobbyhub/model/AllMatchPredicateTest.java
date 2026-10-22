package seedu.hobbyhub.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.model.member.HobbyContainsKeywordsPredicate;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.member.NameContainsKeywordsPredicate;

public class AllMatchPredicateTest {
    private final Predicate<String> startsWithA = value -> value.startsWith("a");
    private final Predicate<String> endsWithZ = value -> value.endsWith("z");

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AllMatchPredicate<String>(null));
        assertThrows(NullPointerException.class, () -> new AllMatchPredicate<>(Arrays.asList(startsWithA, null)));
    }

    @Test
    public void test_requiresEveryCondition() {
        AllMatchPredicate<String> predicate = new AllMatchPredicate<>(List.of(startsWithA, endsWithZ));
        assertTrue(predicate.test("abz"));
        assertFalse(predicate.test("abc"));
        assertFalse(predicate.test("xyz"));
        assertFalse(predicate.test("hello"));
        assertTrue(new AllMatchPredicate<String>(List.of()).test("anything"));
    }

    @Test
    public void constructor_copiesSuppliedList() {
        List<Predicate<String>> predicates = new ArrayList<>(List.of(startsWithA, endsWithZ));
        AllMatchPredicate<String> predicate = new AllMatchPredicate<>(predicates);
        predicates.clear();
        assertFalse(predicate.test("abc"));
    }

    @Test
    public void equalsAndHashCode_compareConditions() {
        AllMatchPredicate<String> predicate = new AllMatchPredicate<>(List.of(startsWithA, endsWithZ));
        AllMatchPredicate<String> equivalent = new AllMatchPredicate<>(List.of(startsWithA, endsWithZ));
        assertTrue(predicate.equals(predicate));
        assertTrue(predicate.equals(equivalent));
        assertTrue(equivalent.equals(predicate));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals("abz"));
        assertFalse(predicate.equals(new AllMatchPredicate<>(List.of(startsWithA))));
        assertEquals(predicate.hashCode(), equivalent.hashCode());
        assertTrue(Set.of(predicate).contains(equivalent));
    }

    @Test
    public void hashCode_equivalentSearchCriteria_workAsSetKeys() {
        AllMatchPredicate<Member> predicate = new AllMatchPredicate<>(List.of(
                new NameContainsKeywordsPredicate(List.of("Alice")),
                new HobbyContainsKeywordsPredicate(List.of("Chess"))));
        AllMatchPredicate<Member> equivalent = new AllMatchPredicate<>(List.of(
                new NameContainsKeywordsPredicate(List.of("Alice")),
                new HobbyContainsKeywordsPredicate(List.of("Chess"))));
        assertEquals(predicate, equivalent);
        assertEquals(predicate.hashCode(), equivalent.hashCode());
        assertTrue(Set.of(predicate).contains(equivalent));
    }

    @Test
    public void toStringMethod() {
        List<Predicate<String>> predicates = List.of(startsWithA, endsWithZ);
        AllMatchPredicate<String> predicate = new AllMatchPredicate<>(predicates);
        assertEquals(AllMatchPredicate.class.getCanonicalName() + "{predicates=" + predicates + "}",
                predicate.toString());
    }
}
