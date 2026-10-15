package seedu.hobbyhub.model.member;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

/**
 * Tests whether a member's email matches the given email, ignoring capitalization.
 */
public class EmailMatchesPredicate implements Predicate<Member> {
    private final Email email;

    public EmailMatchesPredicate(Email email) {
        this.email = requireNonNull(email);
    }

    @Override
    public boolean test(Member member) {
        return member.getEmail().value.equalsIgnoreCase(email.value);
    }
}
