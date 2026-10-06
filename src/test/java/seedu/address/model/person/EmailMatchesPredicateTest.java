package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class EmailMatchesPredicateTest {

    @Test
    public void constructor_nullEmail_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EmailMatchesPredicate(null));
    }

    @Test
    public void test_sameEmail_returnsTrue() {
        EmailMatchesPredicate predicate = new EmailMatchesPredicate(new Email("john@email.com"));
        assertTrue(predicate.test(new PersonBuilder().withEmail("john@email.com").build()));
    }

    @Test
    public void test_emailWithDifferentCapitalization_returnsTrue() {
        EmailMatchesPredicate predicate = new EmailMatchesPredicate(new Email("JOHN@EMAIL.COM"));
        assertTrue(predicate.test(new PersonBuilder().withEmail("john@email.com").build()));
        assertTrue(predicate.test(new PersonBuilder().withEmail("John@Email.Com").build()));
    }

    @Test
    public void test_differentEmail_returnsFalse() {
        EmailMatchesPredicate predicate = new EmailMatchesPredicate(new Email("john@email.com"));
        assertFalse(predicate.test(new PersonBuilder().withEmail("jane@email.com").build()));
        assertFalse(predicate.test(new PersonBuilder().withEmail("john@other.com").build()));
    }

    @Test
    public void test_emailIsSubstring_returnsFalse() {
        EmailMatchesPredicate predicate = new EmailMatchesPredicate(new Email("john@email.com"));
        assertFalse(predicate.test(new PersonBuilder().withEmail("longjohn@email.com").build()));
        assertFalse(predicate.test(new PersonBuilder().withEmail("john@email.com.sg").build()));
    }
}
