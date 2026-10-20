package seedu.hobbyhub.model.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class SessionNameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SessionName(null));
    }

    @Test
    public void constructor_invalidSessionName_throwsIllegalArgumentException() {
        String invalidSessionName = "";
        assertThrows(IllegalArgumentException.class, () -> new SessionName(invalidSessionName));
    }

    @Test
    public void isValidSessionName() {
        // null session name
        assertThrows(NullPointerException.class, () -> SessionName.isValidSessionName(null));

        // invalid session names
        assertFalse(SessionName.isValidSessionName("")); // empty string
        assertFalse(SessionName.isValidSessionName(" ")); // spaces only
        assertFalse(SessionName.isValidSessionName(" Golf Meet")); // leading space
        assertFalse(SessionName.isValidSessionName("-n Golf")); // starts with a dash, looks like an option
        assertFalse(SessionName.isValidSessionName("Golf@Meet")); // contains a disallowed character
        assertFalse(SessionName.isValidSessionName("a".repeat(SessionName.MAX_LENGTH + 1))); // too long

        // valid session names
        assertTrue(SessionName.isValidSessionName("Golf Meet")); // letters and a space
        assertTrue(SessionName.isValidSessionName("Golf_Meet")); // underscore
        assertTrue(SessionName.isValidSessionName("Rock & Roll Jam")); // ampersand
        assertTrue(SessionName.isValidSessionName("Alex's Chess Night - Round 2")); // apostrophe and dash
        assertTrue(SessionName.isValidSessionName("2v2 Badminton")); // starts with a digit
        assertTrue(SessionName.isValidSessionName("a".repeat(SessionName.MAX_LENGTH))); // exactly the limit
    }

    @Test
    public void equals() {
        SessionName name = new SessionName("Golf Meet");

        // same values -> returns true
        assertTrue(name.equals(new SessionName("Golf Meet")));

        // same values ignoring case -> returns true
        assertTrue(name.equals(new SessionName("golf MEET")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new SessionName("Golf Meet 2")));
    }

    @Test
    public void hashCode_sameNameDifferentCase_sameHashCode() {
        assertEquals(new SessionName("Golf Meet").hashCode(), new SessionName("GOLF meet").hashCode());
    }

    @Test
    public void toString_keepsOriginalCase() {
        assertEquals("Golf Meet", new SessionName("Golf Meet").toString());
    }
}
