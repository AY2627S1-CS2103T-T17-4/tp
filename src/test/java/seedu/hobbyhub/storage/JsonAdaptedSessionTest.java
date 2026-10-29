package seedu.hobbyhub.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.hobbyhub.storage.JsonAdaptedSession.MESSAGE_INVALID_DATE_TIME;
import static seedu.hobbyhub.storage.JsonAdaptedSession.MESSAGE_INVALID_LOCATION;
import static seedu.hobbyhub.storage.JsonAdaptedSession.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.hobbyhub.testutil.Assert.assertThrows;
import static seedu.hobbyhub.testutil.TypicalSessions.CHESS_MEETUP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.commons.exceptions.IllegalValueException;
import seedu.hobbyhub.model.member.Email;
import seedu.hobbyhub.model.session.Session;
import seedu.hobbyhub.model.session.SessionName;
import seedu.hobbyhub.testutil.SessionBuilder;

public class JsonAdaptedSessionTest {
    private static final String INVALID_NAME = "-Golf Meet";
    private static final String INVALID_DATE_TIME = "24/10/2026";
    private static final String INVALID_EMAIL = "example.com";

    private static final String VALID_NAME = CHESS_MEETUP.getName().toString();
    private static final String VALID_DATE_TIME = CHESS_MEETUP.getDate().toString();
    private static final String VALID_LOCATION = CHESS_MEETUP.getLocation();
    private static final List<String> VALID_MEMBERS = CHESS_MEETUP.getMembers().stream()
            .map(email -> email.value)
            .toList();

    @Test
    public void toModelType_validSessionDetails_returnsSession() throws Exception {
        JsonAdaptedSession session = new JsonAdaptedSession(CHESS_MEETUP);
        assertEquals(CHESS_MEETUP, session.toModelType());
    }

    @Test
    public void toModelType_nullMembers_returnsSessionWithNoMembers() throws Exception {
        JsonAdaptedSession session = new JsonAdaptedSession(VALID_NAME, VALID_DATE_TIME,
                CHESS_MEETUP.getLocation(), CHESS_MEETUP.isCompleted(), null);
        Session expected = new SessionBuilder(CHESS_MEETUP).withMembers().build();
        assertEquals(expected, session.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedSession session = new JsonAdaptedSession(INVALID_NAME, VALID_DATE_TIME,
                VALID_LOCATION, false, VALID_MEMBERS);
        assertThrows(IllegalValueException.class, SessionName.MESSAGE_CONSTRAINTS, session::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedSession session = new JsonAdaptedSession(null, VALID_DATE_TIME,
                VALID_LOCATION, false, VALID_MEMBERS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, SessionName.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, session::toModelType);
    }

    @Test
    public void toModelType_invalidDateTime_throwsIllegalValueException() {
        JsonAdaptedSession session = new JsonAdaptedSession(VALID_NAME, INVALID_DATE_TIME,
                VALID_LOCATION, false, VALID_MEMBERS);
        assertThrows(IllegalValueException.class, MESSAGE_INVALID_DATE_TIME, session::toModelType);

        // not a real calendar date
        JsonAdaptedSession sessionWithImpossibleDate = new JsonAdaptedSession(VALID_NAME, "2026-02-30T14:00",
                VALID_LOCATION, false, VALID_MEMBERS);
        assertThrows(IllegalValueException.class, MESSAGE_INVALID_DATE_TIME, sessionWithImpossibleDate::toModelType);
    }

    @Test
    public void toModelType_nullDateTime_throwsIllegalValueException() {
        JsonAdaptedSession session = new JsonAdaptedSession(VALID_NAME, null, VALID_LOCATION, false, VALID_MEMBERS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "date");
        assertThrows(IllegalValueException.class, expectedMessage, session::toModelType);
    }

    @Test
    public void toModelType_nullLocation_throwsIllegalValueException() {
        JsonAdaptedSession session = new JsonAdaptedSession(VALID_NAME, VALID_DATE_TIME,
                null, false, VALID_MEMBERS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "location");
        assertThrows(IllegalValueException.class, expectedMessage, session::toModelType);
    }

    @Test
    public void toModelType_blankLocation_throwsIllegalValueException() {
        for (String location : List.of("", "   ", " \t ", "\u2003", "\u2002\u2003\u3000", " \u2003 \t")) {
            JsonAdaptedSession session = new JsonAdaptedSession(VALID_NAME, VALID_DATE_TIME,
                    location, false, VALID_MEMBERS);
            assertThrows(IllegalValueException.class, MESSAGE_INVALID_LOCATION, session::toModelType);
        }
    }

    @Test
    public void toModelType_locationWithUnicodeWhitespace_preservesNonBlankLocation() throws Exception {
        String location = "Golf\u2003Club";
        JsonAdaptedSession session = new JsonAdaptedSession(VALID_NAME, VALID_DATE_TIME,
                location, false, VALID_MEMBERS);
        assertEquals(location, session.toModelType().getLocation());
    }

    @Test
    public void toModelType_invalidMemberEmail_throwsIllegalValueException() {
        List<String> invalidMembers = new ArrayList<>(VALID_MEMBERS);
        invalidMembers.add(INVALID_EMAIL);
        JsonAdaptedSession session = new JsonAdaptedSession(VALID_NAME, VALID_DATE_TIME,
                VALID_LOCATION, false, invalidMembers);
        assertThrows(IllegalValueException.class, Email.MESSAGE_CONSTRAINTS, session::toModelType);
    }

    @Test
    public void toModelType_nullMemberEmail_throwsIllegalValueException() {
        List<String> membersWithNull = new ArrayList<>(Arrays.asList(VALID_MEMBERS.get(0), null));
        JsonAdaptedSession session = new JsonAdaptedSession(VALID_NAME, VALID_DATE_TIME,
                VALID_LOCATION, false, membersWithNull);
        assertThrows(IllegalValueException.class, Email.MESSAGE_CONSTRAINTS, session::toModelType);
    }

    @Test
    public void toModelType_dateOnlyLegacyValue_usesStartOfDay() throws Exception {
        JsonAdaptedSession session = new JsonAdaptedSession(VALID_NAME, "2026-10-24",
                VALID_LOCATION, false, VALID_MEMBERS);
        Session modelSession = session.toModelType();

        assertEquals("2026-10-24T00:00", modelSession.getDate().toString());
        assertEquals(VALID_LOCATION, modelSession.getLocation());
        assertEquals(false, modelSession.isCompleted());
    }
}
