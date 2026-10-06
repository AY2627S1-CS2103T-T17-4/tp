package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

/**
 * Tests whether a person's email matches the given email, ignoring capitalization.
 */
public class EmailMatchesPredicate implements Predicate<Person> {
    private final Email email;

    public EmailMatchesPredicate(Email email) {
        this.email = requireNonNull(email);
    }

    @Override
    public boolean test(Person person) {
        return person.getEmail().value.equalsIgnoreCase(email.value);
    }
}
