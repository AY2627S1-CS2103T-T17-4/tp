package seedu.hobbyhub.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

/** Structured profile content for a successful view command. */
public record ViewDetails(String title, String subtitle, List<Field> fields) {

    public ViewDetails {
        requireNonNull(title);
        requireNonNull(subtitle);
        fields = List.copyOf(requireNonNull(fields));
    }

    /** A labeled value displayed in the details panel. */
    public record Field(String label, String value) {
        public Field {
            requireNonNull(label);
            requireNonNull(value);
        }
    }
}
