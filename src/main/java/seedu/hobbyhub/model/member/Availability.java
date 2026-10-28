package seedu.hobbyhub.model.member;

import static java.util.Objects.requireNonNull;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/** Represents the weekdays on which a member is generally available. */
public class Availability {
    public static final String MESSAGE_CONSTRAINTS =
            "Availability must use full weekday names from Monday to Sunday.";

    private final Set<DayOfWeek> days;

    /** Creates an availability value containing the supplied weekdays. */
    public Availability(Collection<DayOfWeek> days) {
        requireNonNull(days);
        days.forEach(Objects::requireNonNull);
        this.days = days.isEmpty() ? EnumSet.noneOf(DayOfWeek.class) : EnumSet.copyOf(days);
    }

    /** Returns an availability value with no weekdays selected. */
    public static Availability empty() {
        return new Availability(Set.of());
    }

    /** Returns whether this availability includes {@code day}. */
    public boolean contains(DayOfWeek day) {
        return days.contains(requireNonNull(day));
    }

    /** Returns whether no weekdays are selected. */
    public boolean isEmpty() {
        return days.isEmpty();
    }

    /** Returns the selected weekdays as an unmodifiable set. */
    public Set<DayOfWeek> getDays() {
        return Collections.unmodifiableSet(days);
    }

    /** Returns a weekday with user-friendly capitalization. */
    public static String formatDay(DayOfWeek day) {
        String lowerCaseName = requireNonNull(day).name().toLowerCase(Locale.ROOT);
        return Character.toUpperCase(lowerCaseName.charAt(0)) + lowerCaseName.substring(1);
    }

    @Override
    public String toString() {
        if (days.isEmpty()) {
            return "None";
        }
        return days.stream().sorted().map(Availability::formatDay).collect(Collectors.joining(", "));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof Availability otherAvailability
                && days.equals(otherAvailability.days);
    }

    @Override
    public int hashCode() {
        return days.hashCode();
    }
}
