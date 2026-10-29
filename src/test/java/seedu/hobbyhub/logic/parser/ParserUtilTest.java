package seedu.hobbyhub.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.hobbyhub.logic.parser.ParserUtil.MESSAGE_INVALID_SESSION_DATE_TIME;
import static seedu.hobbyhub.testutil.Assert.assertThrows;
import static seedu.hobbyhub.testutil.TypicalIndexes.INDEX_FIRST_MEMBER;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.model.group.Group;
import seedu.hobbyhub.model.member.Address;
import seedu.hobbyhub.model.member.Availability;
import seedu.hobbyhub.model.member.Email;
import seedu.hobbyhub.model.member.Name;
import seedu.hobbyhub.model.member.Phone;
import seedu.hobbyhub.model.session.SessionName;

public class ParserUtilTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "++651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_GROUP = "#friend";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "123456";
    private static final String VALID_ADDRESS = "123 Main Street #0505";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_GROUP_1 = "friend";
    private static final String VALID_GROUP_2 = "neighbour";

    private static final String WHITESPACE = " \t\r\n";

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, ()
            -> ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1)));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_MEMBER, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_MEMBER, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone((String) null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(INVALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(VALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
        String phoneWithWhitespace = WHITESPACE + VALID_PHONE + WHITESPACE;
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(phoneWithWhitespace));
    }

    @Test
    public void parseAddress_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseAddress((String) null));
    }

    @Test
    public void parseAddress_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseAddress(INVALID_ADDRESS));
    }

    @Test
    public void parseAddress_multilineValue_throwsParseException() {
        String multilineAddress = VALID_ADDRESS + "\nUnit 05";
        assertThrows(ParseException.class, Address.MESSAGE_CONSTRAINTS, ()
            -> ParserUtil.parseAddress(multilineAddress));
    }

    @Test
    public void parseAddress_validValueWithoutWhitespace_returnsAddress() throws Exception {
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(VALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithWhitespace_returnsTrimmedAddress() throws Exception {
        String addressWithWhitespace = WHITESPACE + VALID_ADDRESS + WHITESPACE;
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(addressWithWhitespace));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail((String) null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(VALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace));
    }

    @Test
    public void parseGroup_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseGroup(null));
    }

    @Test
    public void parseSessionName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseSessionName(null));
    }

    @Test
    public void parseSessionName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseSessionName("-Invalid"));
    }

    @Test
    public void parseSessionName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        assertEquals(new SessionName("Golf Meetup"), ParserUtil.parseSessionName("  Golf Meetup  "));
    }

    @Test
    public void parseSessionName_singleCharacter_returnsName() throws Exception {
        assertEquals(new SessionName("A"), ParserUtil.parseSessionName(" A "));
    }

    @Test
    public void parseSessionName_emptyOrBlank_throwsParseException() {
        for (String name : List.of("", "   ", "\"", "\"   \"")) {
            assertThrows(ParseException.class, () -> ParserUtil.parseSessionName(name));
        }
    }

    @Test
    public void parseSessionName_quotedValue_returnsUnquotedName() throws Exception {
        assertEquals(new SessionName("Chess -night"), ParserUtil.parseSessionName(" \" Chess -night \" "));
    }

    @Test
    public void parseSessionName_unbalancedQuotes_throwsParseException() {
        for (String name : List.of("\"Chess -night", "Chess -night\"", "\"\"")) {
            assertThrows(ParseException.class, () -> ParserUtil.parseSessionName(name));
        }
    }

    @Test
    public void parseSessionLocation_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseSessionLocation(null));
    }

    @Test
    public void parseSessionLocation_blankValue_throwsParseException() {
        for (String location : List.of("", " \t ", "\u2003", "\u2002\u2003\u3000", " \u2003 \t")) {
            assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_SESSION_LOCATION, ()
                -> ParserUtil.parseSessionLocation(location));
        }
    }

    @Test
    public void parseSessionLocation_validValueWithWhitespace_returnsTrimmedLocation() throws Exception {
        assertEquals("Golf Club", ParserUtil.parseSessionLocation("  Golf Club  "));
        assertEquals("Golf\u2003Club", ParserUtil.parseSessionLocation("  Golf\u2003Club  "));
    }

    @Test
    public void parseSessionDateTime_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseSessionDateTime(null));
    }

    @Test
    public void parseSessionDateTime_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseSessionDateTime("next Saturday"));
    }

    @Test
    public void parseSessionDateTime_validValueWithWhitespace_returnsDateTime() throws Exception {
        assertEquals(LocalDateTime.parse("2026-10-24T14:00"),
                ParserUtil.parseSessionDateTime(" 2026-10-24T14:00 "));
    }

    @Test
    public void parseSessionDateTime_spaceSeparatedValue_returnsDateTime() throws Exception {
        assertEquals(LocalDateTime.parse("2026-11-01T10:00"), ParserUtil.parseSessionDateTime("2026-11-01 10:00"));
    }

    @Test
    public void parseSessionDateTime_isoValue_returnsDateTime() throws Exception {
        assertEquals(LocalDateTime.parse("2026-11-01T10:00"), ParserUtil.parseSessionDateTime("2026-11-01T10:00"));
    }

    @Test
    public void parseSessionDateTime_nonExistentDateOrTime_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_SESSION_DATE_TIME, () ->
                ParserUtil.parseSessionDateTime("2026-02-30 10:00"));
        assertThrows(ParseException.class, MESSAGE_INVALID_SESSION_DATE_TIME, () ->
                ParserUtil.parseSessionDateTime("2026-11-01 25:00"));
        assertThrows(ParseException.class, MESSAGE_INVALID_SESSION_DATE_TIME, () ->
                ParserUtil.parseSessionDateTime("2026-02-30T10:00"));
    }

    @Test
    public void parseSessionDateTime_valueWithSeconds_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseSessionDateTime("2026-11-01T10:00:30"));
    }

    @Test
    public void parseGroup_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseGroup(INVALID_GROUP));
    }

    @Test
    public void parseGroup_validValueWithoutWhitespace_returnsGroup() throws Exception {
        Group expectedGroup = new Group(VALID_GROUP_1);
        assertEquals(expectedGroup, ParserUtil.parseGroup(VALID_GROUP_1));
    }

    @Test
    public void parseGroup_validValueWithWhitespace_returnsTrimmedGroup() throws Exception {
        String groupWithWhitespace = WHITESPACE + VALID_GROUP_1 + WHITESPACE;
        Group expectedGroup = new Group(VALID_GROUP_1);
        assertEquals(expectedGroup, ParserUtil.parseGroup(groupWithWhitespace));
    }

    @Test
    public void parseGroups_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseGroups(null));
    }

    @Test
    public void parseGroups_collectionWithInvalidGroups_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseGroups(List.of(VALID_GROUP_1, INVALID_GROUP)));
    }

    @Test
    public void parseGroups_emptyCollection_returnsEmptySet() throws Exception {
        assertTrue(ParserUtil.parseGroups(List.of()).isEmpty());
    }

    @Test
    public void parseGroups_collectionWithValidGroups_returnsGroupSet() throws Exception {
        Set<Group> actualGroupSet = ParserUtil.parseGroups(List.of(VALID_GROUP_1, VALID_GROUP_2));
        Set<Group> expectedGroupSet = Set.of(new Group(VALID_GROUP_1), new Group(VALID_GROUP_2));

        assertEquals(expectedGroupSet, actualGroupSet);
    }

    @Test
    public void parseAvailability_validDays_returnsAvailability() throws Exception {
        Availability expected = new Availability(List.of(DayOfWeek.MONDAY, DayOfWeek.SUNDAY));

        assertEquals(expected, ParserUtil.parseAvailability(List.of("Monday", "sUNDAY")));
    }

    @Test
    public void parseAvailability_invalidDay_throwsParseException() {
        assertThrows(ParseException.class, Availability.MESSAGE_CONSTRAINTS, () ->
                ParserUtil.parseAvailability(List.of("Funday")));
        assertThrows(ParseException.class, Availability.MESSAGE_CONSTRAINTS, () ->
                ParserUtil.parseAvailability(List.of("")));
    }
}
