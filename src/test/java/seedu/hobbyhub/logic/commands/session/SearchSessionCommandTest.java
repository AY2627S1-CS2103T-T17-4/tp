package seedu.hobbyhub.logic.commands.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.model.session.SessionSearchPredicate;
import seedu.hobbyhub.model.session.SessionSearchPredicate.Field;

public class SearchSessionCommandTest {
    @Test
    public void constructor_nullPredicate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SearchSessionCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        SearchSessionCommand command = new SearchSessionCommand(new SessionSearchPredicate(Field.NAME, "Chess"));
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void equals_comparesSearchPredicates() {
        SearchSessionCommand command = new SearchSessionCommand(new SessionSearchPredicate(Field.NAME, "Chess"));
        SearchSessionCommand equivalent = new SearchSessionCommand(new SessionSearchPredicate(Field.NAME, "CHESS"));

        assertTrue(command.equals(command));
        assertTrue(command.equals(equivalent));
        assertTrue(equivalent.equals(command));
        assertFalse(command.equals(null));
        assertFalse(command.equals("Chess"));
        assertFalse(command.equals(new SearchSessionCommand(new SessionSearchPredicate(Field.NAME, "Running"))));
        assertFalse(command.equals(new SearchSessionCommand(new SessionSearchPredicate(Field.LOCATION, "Chess"))));
    }

    @Test
    public void toStringMethod() {
        SessionSearchPredicate predicate = new SessionSearchPredicate(Field.NAME, "Chess");
        SearchSessionCommand command = new SearchSessionCommand(predicate);
        String expected = SearchSessionCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, command.toString());
    }
}
