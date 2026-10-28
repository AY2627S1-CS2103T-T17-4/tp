package seedu.hobbyhub.model.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.testutil.MemberBuilder;

public class HobbyContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = List.of("first");
        List<String> secondPredicateKeywordList = List.of("first", "second");

        HobbyContainsKeywordsPredicate firstPredicate =
                new HobbyContainsKeywordsPredicate(firstPredicateKeywordList);
        HobbyContainsKeywordsPredicate secondPredicate =
                new HobbyContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        HobbyContainsKeywordsPredicate firstPredicateCopy =
                new HobbyContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different keywords -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_hobbyContainsKeywords_returnsTrue() {
        // One keyword
        HobbyContainsKeywordsPredicate predicate = new HobbyContainsKeywordsPredicate(List.of("Chess"));
        assertTrue(predicate.test(new MemberBuilder().withHobby("Chess").build()));

        // Multiple keywords, with one matching
        predicate = new HobbyContainsKeywordsPredicate(List.of("Running", "Chess"));
        assertTrue(predicate.test(new MemberBuilder().withHobby("Chess").build()));

        // Mixed-case keyword
        predicate = new HobbyContainsKeywordsPredicate(List.of("cHeSs"));
        assertTrue(predicate.test(new MemberBuilder().withHobby("Chess").build()));

        // Partial keyword in the middle of a hobby
        predicate = new HobbyContainsKeywordsPredicate(List.of("hes"));
        assertTrue(predicate.test(new MemberBuilder().withHobby("Chess").build()));

        // Canonically equivalent Unicode keyword
        predicate = new HobbyContainsKeywordsPredicate(List.of("Cafe\u0301"));
        assertTrue(predicate.test(new MemberBuilder().withHobby("Café").build()));
    }

    @Test
    public void test_hobbyDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        HobbyContainsKeywordsPredicate predicate = new HobbyContainsKeywordsPredicate(List.of());
        assertFalse(predicate.test(new MemberBuilder().withHobby("Chess").build()));

        // Non-matching keyword
        predicate = new HobbyContainsKeywordsPredicate(List.of("Running"));
        assertFalse(predicate.test(new MemberBuilder().withHobby("Chess").build()));

        // Blank keyword does not match every hobby
        predicate = new HobbyContainsKeywordsPredicate(List.of(""));
        assertFalse(predicate.test(new MemberBuilder().withHobby("Chess").build()));

        // Keyword longer than the hobby
        predicate = new HobbyContainsKeywordsPredicate(List.of("Chessboard"));
        assertFalse(predicate.test(new MemberBuilder().withHobby("Chess").build()));

        // Keywords match other fields, but do not match the hobby
        predicate = new HobbyContainsKeywordsPredicate(List.of("Alice", "12345", "alice@email.com"));
        assertFalse(predicate.test(new MemberBuilder().withHobby("Chess").withName("Alice")
                .withPhone("12345").withEmail("alice@email.com").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        HobbyContainsKeywordsPredicate predicate = new HobbyContainsKeywordsPredicate(keywords);

        String expected = HobbyContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
