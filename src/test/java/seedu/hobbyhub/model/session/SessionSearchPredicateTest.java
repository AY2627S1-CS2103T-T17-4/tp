package seedu.hobbyhub.model.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.model.session.SessionSearchPredicate.Field;

public class SessionSearchPredicateTest {
    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SessionSearchPredicate(null, "Chess"));
        assertThrows(NullPointerException.class, () -> new SessionSearchPredicate(Field.NAME, null));
    }

    @Test
    public void equals_comparesFieldAndCaseInsensitiveQuery() {
        SessionSearchPredicate predicate = new SessionSearchPredicate(Field.NAME, "Chess");
        SessionSearchPredicate equivalent = new SessionSearchPredicate(Field.NAME, "CHESS");

        assertTrue(predicate.equals(predicate));
        assertTrue(predicate.equals(equivalent));
        assertTrue(equivalent.equals(predicate));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals("Chess"));
        assertFalse(predicate.equals(new SessionSearchPredicate(Field.LOCATION, "Chess")));
        assertFalse(predicate.equals(new SessionSearchPredicate(Field.NAME, "Running")));
    }

    @Test
    public void hashCode_equalPredicates_workAsSetKeys() {
        SessionSearchPredicate predicate = new SessionSearchPredicate(Field.NAME, "Chess");
        SessionSearchPredicate equivalent = new SessionSearchPredicate(Field.NAME, "chess");

        assertEquals(predicate.hashCode(), equivalent.hashCode());
        assertTrue(Set.of(predicate).contains(equivalent));
    }

    @Test
    public void toStringMethod() {
        SessionSearchPredicate predicate = new SessionSearchPredicate(Field.LOCATION, "Community Center");
        String expected = SessionSearchPredicate.class.getCanonicalName()
                + "{field=LOCATION, query=community center}";
        assertEquals(expected, predicate.toString());
    }
}
