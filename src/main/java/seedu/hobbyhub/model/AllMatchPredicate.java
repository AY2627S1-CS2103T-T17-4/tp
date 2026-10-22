package seedu.hobbyhub.model;

import java.util.List;
import java.util.function.Predicate;

import seedu.hobbyhub.commons.util.ToStringBuilder;

/**
 * Matches a record only when every supplied predicate matches it.
 */
public class AllMatchPredicate<T> implements Predicate<T> {
    private final List<Predicate<T>> predicates;

    /**
     * Copies the predicates so later changes to the supplied list cannot alter this filter.
     */
    public AllMatchPredicate(List<Predicate<T>> predicates) {
        this.predicates = List.copyOf(predicates);
    }

    @Override
    public boolean test(T value) {
        return predicates.stream().allMatch(predicate -> predicate.test(value));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof AllMatchPredicate<?> otherPredicate
                && predicates.equals(otherPredicate.predicates);
    }

    @Override
    public int hashCode() {
        return predicates.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("predicates", predicates).toString();
    }
}
