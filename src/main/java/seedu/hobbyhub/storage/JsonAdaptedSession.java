package seedu.hobbyhub.storage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.hobbyhub.commons.exceptions.IllegalValueException;
import seedu.hobbyhub.model.member.Email;
import seedu.hobbyhub.model.session.Session;
import seedu.hobbyhub.model.session.SessionName;

/**
 * Jackson-friendly version of {@link Session}.
 */
class JsonAdaptedSession {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Session's %s field is missing!";
    public static final String MESSAGE_INVALID_DATE_TIME = "Session date-times should use ISO-8601 format";
    public static final String MESSAGE_INVALID_LOCATION = "Session location cannot be blank.";

    private final String name;
    private final String date;
    private final String location;
    private final boolean completed;
    private final List<String> members = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedSession} with the given session details.
     */
    @JsonCreator
    public JsonAdaptedSession(@JsonProperty("name") String name,
            @JsonProperty("date") String date,
            @JsonProperty("location") String location, @JsonProperty("completed") boolean completed,
            @JsonProperty("members") List<String> members) {
        this.name = name;
        this.date = date;
        this.location = location;
        this.completed = completed;
        if (members != null) {
            this.members.addAll(members);
        }
    }

    /**
     * Converts a given {@code Session} into this class for Jackson use.
     */
    public JsonAdaptedSession(Session source) {
        name = source.getName().fullName;
        date = source.getDate().toString();
        location = source.getLocation();
        completed = source.isCompleted();
        source.getMembers().stream()
                .map(email -> email.value)
                .sorted()
                .forEach(members::add);
    }

    /**
     * Converts this Jackson-friendly adapted session object into the model's {@code Session} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted session.
     */
    public Session toModelType() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, SessionName.class.getSimpleName()));
        }
        if (!SessionName.isValidSessionName(name)) {
            throw new IllegalValueException(SessionName.MESSAGE_CONSTRAINTS);
        }
        final SessionName modelName = new SessionName(name);

        if (date == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "date"));
        }
        final LocalDateTime modelDateTime = parseSessionDateTime(date);

        if (location == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "location"));
        }
        if (location.isBlank()) {
            throw new IllegalValueException(MESSAGE_INVALID_LOCATION);
        }

        final Set<Email> modelMembers = new HashSet<>();
        for (String email : members) {
            if (email == null || !Email.isValidEmail(email)) {
                throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
            }
            modelMembers.add(new Email(email));
        }

        return new Session(modelName, modelDateTime, location, completed, modelMembers);
    }

    /**
     * Returns the parsed session date-time, treating a legacy date-only value as the start of that day.
     */
    private LocalDateTime parseSessionDateTime(String value) throws IllegalValueException {
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(value).atStartOfDay();
            } catch (DateTimeParseException invalidDateTime) {
                throw new IllegalValueException(MESSAGE_INVALID_DATE_TIME);
            }
        }
    }

}
