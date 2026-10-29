package seedu.hobbyhub.model.member;

import static java.util.Objects.requireNonNull;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

import seedu.hobbyhub.commons.util.ToStringBuilder;

/**
 * Matches members with a single hobby satisfying both the name fragment and skill level.
 */
public class HobbyAndSkillPredicate implements Predicate<Member> {
    private final String hobbyQuery;
    private final SkillLevel skillLevel;

    /**
     * Creates a predicate for a validated hobby query and skill level.
     */
    public HobbyAndSkillPredicate(String hobbyQuery, SkillLevel skillLevel) {
        this.hobbyQuery = Hobby.normalizeName(requireNonNull(hobbyQuery)).toLowerCase(Locale.ROOT);
        this.skillLevel = requireNonNull(skillLevel);
    }

    @Override
    public boolean test(Member member) {
        return member.getHobbies().stream().anyMatch(hobby ->
                hobby.value.toLowerCase(Locale.ROOT).contains(hobbyQuery) && hobby.getSkillLevel() == skillLevel);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof HobbyAndSkillPredicate otherPredicate
                && hobbyQuery.equals(otherPredicate.hobbyQuery) && skillLevel == otherPredicate.skillLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hobbyQuery, skillLevel);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("hobbyQuery", hobbyQuery).add("skillLevel", skillLevel).toString();
    }
}
