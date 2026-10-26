package seedu.hobbyhub.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

/** Structured profile content for a successful view command. */
public record ViewDetails(String title, String subtitle, List<Field> fields, RecordType recordType, String recordId) {

    /** Creates a details record after validating and copying its values. */
    public ViewDetails {
        requireNonNull(title);
        requireNonNull(subtitle);
        fields = List.copyOf(requireNonNull(fields));
        requireNonNull(recordType);
        requireNonNull(recordId);
    }

    /** Identifies the kind of record represented by these details. */
    public enum RecordType {
        MEMBER,
        SESSION
    }

    /** A labeled value displayed in the details panel. */
    public record Field(String label, String value) {
        /** Creates a labeled field after validating its values. */
        public Field {
            requireNonNull(label);
            requireNonNull(value);
        }
    }
}
