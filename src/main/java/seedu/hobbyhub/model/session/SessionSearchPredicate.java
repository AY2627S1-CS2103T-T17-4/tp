package seedu.hobbyhub.model.session;

import static java.util.Objects.requireNonNull;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

import seedu.hobbyhub.commons.util.ToStringBuilder;

/**
 * Matches a session against a single validated search query.
 */
public class SessionSearchPredicate implements Predicate<Session> {
    /**
     * The searchable session fields.
     */
    public enum Field {
        NAME, LOCATION, DATE, COMPLETED, EMAIL
    }

    private final Field field;
    private final String query;

    /**
     * Creates a predicate for a query validated by the session search parser.
     */
    public SessionSearchPredicate(Field field, String query) {
        this.field = requireNonNull(field);
        this.query = requireNonNull(query).toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean test(Session session) {
        return switch (field) {
            case NAME -> session.getName().toString().toLowerCase(Locale.ROOT).contains(query);
            case LOCATION -> session.getLocation().toLowerCase(Locale.ROOT).contains(query);
            case DATE -> session.getDate().toLocalDate().toString().equals(query);
            case COMPLETED -> Boolean.toString(session.isCompleted()).equals(query);
            case EMAIL -> session.getMembers().stream().anyMatch(email -> email.value.equalsIgnoreCase(query));
        };
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof SessionSearchPredicate otherPredicate
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
