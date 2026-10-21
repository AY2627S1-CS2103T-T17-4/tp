package seedu.hobbyhub.model.member;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import seedu.hobbyhub.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Member}'s {@code Name} contains any of the keywords given,
 * ignoring case. Keywords may match part of a word, e.g. "jo" matches "John".
 */
public class NameContainsKeywordsPredicate implements Predicate<Member> {
    private final List<String> keywords;

    public NameContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Member member) {
        String lowerCaseName = member.getName().fullName.toLowerCase(Locale.ROOT);
        return keywords.stream()
                .filter(keyword -> !keyword.isBlank())
                .anyMatch(keyword -> lowerCaseName.contains(keyword.toLowerCase(Locale.ROOT)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NameContainsKeywordsPredicate otherNameContainsKeywordsPredicate)) {
            return false;
        }

        return keywords.equals(otherNameContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
