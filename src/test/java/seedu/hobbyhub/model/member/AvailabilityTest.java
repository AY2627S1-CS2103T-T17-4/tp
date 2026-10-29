package seedu.hobbyhub.model.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import java.time.DayOfWeek;
import java.util.List;

import org.junit.jupiter.api.Test;

public class AvailabilityTest {
    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Availability(null));
    }

    @Test
    public void weekdays_areUniqueAndDisplayedInCalendarOrder() {
        Availability availability = new Availability(List.of(
                DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.SUNDAY, DayOfWeek.FRIDAY));

        assertEquals("Monday, Friday, Sunday", availability.toString());
        assertTrue(availability.contains(DayOfWeek.MONDAY));
        assertFalse(availability.contains(DayOfWeek.TUESDAY));
        assertEquals(availability, new Availability(List.of(
                DayOfWeek.FRIDAY, DayOfWeek.SUNDAY, DayOfWeek.MONDAY)));
        assertEquals(availability, availability);
        assertFalse(availability.equals(new Availability(List.of(DayOfWeek.TUESDAY))));
        assertFalse(availability.equals(null));
        assertFalse(availability.equals("Monday"));
    }

    @Test
    public void emptyAvailability_displaysNone() {
        Availability availability = Availability.empty();

        assertTrue(availability.isEmpty());
        assertEquals("None", availability.toString());
    }
}
