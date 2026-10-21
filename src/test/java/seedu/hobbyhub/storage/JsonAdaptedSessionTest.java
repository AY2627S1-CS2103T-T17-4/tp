package seedu.hobbyhub.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.hobbyhub.storage.JsonAdaptedSession.MESSAGE_INVALID_DATE_TIME;
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
        JsonAdaptedSession session = new JsonAdaptedSession(INVALID_NAME, VALID_DATE_TIME, "", false, VALID_MEMBERS);
        assertThrows(IllegalValueException.class, SessionName.MESSAGE_CONSTRAINTS, session::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedSession session = new JsonAdaptedSession(null, VALID_DATE_TIME, "", false, VALID_MEMBERS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, SessionName.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, session::toModelType);
    }

    @Test
    public void toModelType_invalidDateTime_throwsIllegalValueException() {
        JsonAdaptedSession session = new JsonAdaptedSession(VALID_NAME, INVALID_DATE_TIME, "", false, VALID_MEMBERS);
        assertThrows(IllegalValueException.class, MESSAGE_INVALID_DATE_TIME, session::toModelType);

        // not a real calendar date
        JsonAdaptedSession sessionWithImpossibleDate = new JsonAdaptedSession(VALID_NAME, "2026-02-30T14:00",
                "", false, VALID_MEMBERS);
        assertThrows(IllegalValueException.class, MESSAGE_INVALID_DATE_TIME, sessionWithImpossibleDate::toModelType);
    }

    @Test
    public void toModelType_nullDateTime_throwsIllegalValueException() {
        JsonAdaptedSession session = new JsonAdaptedSession(VALID_NAME, null, "", false, VALID_MEMBERS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "date");
        assertThrows(IllegalValueException.class, expectedMessage, session::toModelType);
    }

    @Test
    public void toModelType_invalidMemberEmail_throwsIllegalValueException() {
        List<String> invalidMembers = new ArrayList<>(VALID_MEMBERS);
        invalidMembers.add(INVALID_EMAIL);
        JsonAdaptedSession session = new JsonAdaptedSession(VALID_NAME, VALID_DATE_TIME, "", false, invalidMembers);
        assertThrows(IllegalValueException.class, Email.MESSAGE_CONSTRAINTS, session::toModelType);
    }

    @Test
    public void toModelType_nullMemberEmail_throwsIllegalValueException() {
        List<String> membersWithNull = new ArrayList<>(Arrays.asList(VALID_MEMBERS.get(0), null));
        JsonAdaptedSession session = new JsonAdaptedSession(VALID_NAME, VALID_DATE_TIME, "", false, membersWithNull);
        assertThrows(IllegalValueException.class, Email.MESSAGE_CONSTRAINTS, session::toModelType);
    }

    @Test
    public void toModelType_dateOnlyLegacyValue_usesStartOfDayAndDefaultsNewFields() throws Exception {
        JsonAdaptedSession session = new JsonAdaptedSession(VALID_NAME, "2026-10-24", null, false, VALID_MEMBERS);
        Session modelSession = session.toModelType();

        assertEquals("2026-10-24T00:00", modelSession.getDate().toString());
        assertEquals("", modelSession.getLocation());
        assertEquals(false, modelSession.isCompleted());
    }
}
