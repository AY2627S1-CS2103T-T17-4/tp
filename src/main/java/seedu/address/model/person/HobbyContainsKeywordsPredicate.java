package seedu.address.model.person;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests whether a person's hobby contains any of the keywords given, ignoring case.
 * Keyword matching is partial, so a keyword may match part of a hobby.
 */
public class HobbyContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    public HobbyContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Person person) {
        String lowerCaseHobby = person.getHobby().value.toLowerCase(Locale.ROOT);
        return keywords.stream()
                .filter(keyword -> !keyword.isBlank())
                .anyMatch(keyword -> lowerCaseHobby.contains(keyword.toLowerCase(Locale.ROOT)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof HobbyContainsKeywordsPredicate otherPredicate)) {
            return false;
        }

        return keywords.equals(otherPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
