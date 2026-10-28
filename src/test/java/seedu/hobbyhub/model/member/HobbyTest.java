package seedu.hobbyhub.model.member;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class HobbyTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Hobby(null));
    }

    @Test
    public void constructor_invalidHobby_throwsIllegalArgumentException() {
        String invalidHobby = "";
        assertThrows(IllegalArgumentException.class, () -> new Hobby(invalidHobby));
    }

    @Test
    public void isValidHobby() {
        // null hobby
        assertThrows(NullPointerException.class, () -> Hobby.isValidHobby(null));

        // invalid hobbies
        assertFalse(Hobby.isValidHobby("")); // empty string
        assertFalse(Hobby.isValidHobby(" ")); // spaces only
        assertFalse(Hobby.isValidHobby("&")); // only special characters
        assertFalse(Hobby.isValidHobby(" D&D")); // starts with whitespace
        assertFalse(Hobby.isValidHobby("@sketching")); // unsupported special character

        // valid hobbies
        assertTrue(Hobby.isValidHobby("Chess")); // alphabets only
        assertTrue(Hobby.isValidHobby("Dota 2")); // alphanumeric characters and spaces
        assertTrue(Hobby.isValidHobby("D&D")); // ampersand
        assertTrue(Hobby.isValidHobby("C++")); // plus signs
        assertTrue(Hobby.isValidHobby("Muay-Thai")); // hyphen
        assertTrue(Hobby.isValidHobby("Cooking/Baking")); // slash
        assertTrue(Hobby.isValidHobby("Children's Theatre")); // apostrophe
        assertTrue(Hobby.isValidHobby("Café hopping")); // accented Latin letter
        assertTrue(Hobby.isValidHobby("書道")); // non-Latin letters
        assertTrue(Hobby.isValidHobby("Cafe\u0301 hopping")); // decomposed accented letter
    }

    @Test
    public void constructor_canonicallyEquivalentName_normalizesToNfc() {
        Hobby composed = new Hobby("Café");
        Hobby decomposed = new Hobby("Cafe\u0301");

        assertTrue(composed.equals(decomposed));
        assertTrue(composed.hashCode() == decomposed.hashCode());
        assertTrue(decomposed.value.equals("Café"));
    }

    @Test
    public void equals() {
        Hobby hobby = new Hobby("Board Games");

        // same values -> returns true
        assertTrue(hobby.equals(new Hobby("Board Games")));

        // same object -> returns true
        assertTrue(hobby.equals(hobby));

        // null -> returns false
        assertFalse(hobby.equals(null));

        // different types -> returns false
        assertFalse(hobby.equals(5.0f));

        // different values -> returns false
        assertFalse(hobby.equals(new Hobby("Chess")));
    }
}
