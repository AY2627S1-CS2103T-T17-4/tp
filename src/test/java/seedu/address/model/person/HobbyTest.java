package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

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
