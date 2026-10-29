package seedu.hobbyhub.model.member;

import static java.util.Objects.requireNonNull;

import java.time.DayOfWeek;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

import seedu.hobbyhub.commons.util.ToStringBuilder;

/**
 * Matches a member's group fragment, complete email, or the skill level of any hobby, ignoring case.
 */
public class MemberSearchPredicate implements Predicate<Member> {
    /**
     * Searchable member fields beyond the existing name and hobby searches.
     */
    public enum Field {
        GROUP, EMAIL, SKILL_LEVEL, AVAILABILITY
    }

    private final Field field;
    private final String query;

    /**
     * Creates a predicate for a query validated by the member search parser.
     */
    public MemberSearchPredicate(Field field, String query) {
        this.field = requireNonNull(field);
        this.query = requireNonNull(query).toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean test(Member member) {
        return switch (field) {
            case GROUP -> member.getGroups().stream()
                    .anyMatch(group -> group.groupName.toLowerCase(Locale.ROOT).contains(query));
            case EMAIL -> member.getEmail().value.equalsIgnoreCase(query);
            case SKILL_LEVEL -> member.getHobbies().stream()
                    .anyMatch(hobby -> hobby.getSkillLevel().toString().equalsIgnoreCase(query));
            case AVAILABILITY -> member.getAvailability()
                    .contains(DayOfWeek.valueOf(query.toUpperCase(Locale.ROOT)));
        };
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof MemberSearchPredicate otherPredicate
                && field == otherPredicate.field && query.equals(otherPredicate.query);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, query);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("field", field).add("query", query).toString();
    }
}
