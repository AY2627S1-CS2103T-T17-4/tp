package seedu.hobbyhub.model.member;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import seedu.hobbyhub.commons.util.ToStringBuilder;

/**
 * Tests whether a member's hobby contains any of the keywords given, ignoring case.
 * Keyword matching is partial, so a keyword may match part of a hobby.
 */
public class HobbyContainsKeywordsPredicate implements Predicate<Member> {
    private final List<String> keywords;

    public HobbyContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords.stream().map(Hobby::normalizeName).toList();
    }

    @Override
    public boolean test(Member member) {
        return member.getHobbies().stream().anyMatch(hobby -> keywords.stream()
                .filter(keyword -> !keyword.isBlank())
                .anyMatch(keyword -> hobby.value.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT))));
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
    public int hashCode() {
        return keywords.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
