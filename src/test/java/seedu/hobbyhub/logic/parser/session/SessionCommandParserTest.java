package seedu.hobbyhub.logic.parser.session;

import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.hobbyhub.logic.parser.ParserUtil.MESSAGE_INVALID_SESSION_DATE_TIME;
import static seedu.hobbyhub.logic.parser.ParserUtil.MESSAGE_INVALID_SESSION_LOCATION;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.logic.commands.session.AddSessionCommand;
import seedu.hobbyhub.logic.commands.session.DeleteSessionCommand;
import seedu.hobbyhub.logic.commands.session.EditSessionCommand;
import seedu.hobbyhub.logic.commands.session.ViewSessionCommand;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.testutil.SessionBuilder;

/**
 * Tests the parsers used while the session list is showing.
 */
public class SessionCommandParserTest {

    @Test
    public void parse_addSession_returnsCommand() {
        AddSessionCommand expectedCommand = new AddSessionCommand(new SessionBuilder()
                .withName("Golf Meet")
                .withDateTime("2026-10-24T14:00")
                .withLocation("Golf Club")
                .withMembers()
                .build());
        assertParseSuccess(new AddSessionCommandParser(),
                " -n Golf Meet -l Golf Club -t 2026-10-24T14:00", expectedCommand);
    }

    @Test
    public void parse_addSessionWithSpaceSeparatedDateTime_returnsCommand() {
        AddSessionCommand expectedCommand = new AddSessionCommand(new SessionBuilder()
                .withName("Golf Meet")
                .withDateTime("2026-11-01T10:00")
                .withLocation("Golf Club")
                .withMembers()
                .build());
        assertParseSuccess(new AddSessionCommandParser(),
                " -n Golf Meet -l Golf Club -t 2026-11-01 10:00", expectedCommand);
    }

    @Test
    public void parse_editSessionWithSpaceSeparatedDateTime_returnsCommand() {
        EditSessionCommand expectedCommand = new EditSessionCommand(
                Index.fromOneBased(1), null, LocalDateTime.parse("2026-11-01T10:00"));
        assertParseSuccess(new EditSessionCommandParser(), " 1 -t 2026-11-01 10:00", expectedCommand);
    }

    @Test
    public void parse_addSessionMissingName_throwsParseException() {
        assertInvalidSessionCommandFormat("-l Golf Club -t 2026-10-24T14:00");
    }

    @Test
    public void parse_addSessionMissingLocation_throwsParseException() {
        assertInvalidSessionCommandFormat("-n Golf Meet -t 2026-10-24T14:00");
    }

    @Test
    public void parse_addSessionMissingDateTime_throwsParseException() {
        assertInvalidSessionCommandFormat("-n Golf Meet -l Golf Club");
    }

    @Test
    public void parse_addSessionPreamble_throwsParseException() {
        assertInvalidSessionCommandFormat("unexpected -n Golf Meet -l Golf Club -t 2026-10-24T14:00");
    }

    @Test
    public void parse_addSessionDuplicatePrefix_throwsParseException() {
        assertThrows(ParseException.class, () -> new AddSessionCommandParser().parse(
                "-n Golf Meet -n Another Meet -l Golf Club -t 2026-10-24T14:00"));
    }

    @Test
    public void parse_editSessionWithLocation_returnsCommand() {
        EditSessionCommand expectedCommand = new EditSessionCommand(
                Index.fromOneBased(1), "Golf Club", null);
        assertParseSuccess(new EditSessionCommandParser(), " 1 -l Golf Club", expectedCommand);
    }

    @Test
    public void parse_editSessionWithDateTime_returnsCommand() {
        EditSessionCommand expectedCommand = new EditSessionCommand(
                Index.fromOneBased(2), null, LocalDateTime.parse("2026-10-25T14:00"));
        assertParseSuccess(new EditSessionCommandParser(), " 2 -t 2026-10-25T14:00", expectedCommand);
    }

    @Test
    public void parse_editSessionWithBothFields_returnsCommand() {
        EditSessionCommand expectedCommand = new EditSessionCommand(
                Index.fromOneBased(1), "Golf Club", LocalDateTime.parse("2026-10-25T14:00"));
        assertParseSuccess(new EditSessionCommandParser(), " 1 -l Golf Club -t 2026-10-25T14:00", expectedCommand);
    }

    @Test
    public void parse_editSessionWithoutIndex_throwsParseException() {
        assertParseFailure(new EditSessionCommandParser(), " -l Golf Club",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditSessionCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_editSessionWithoutEditableFields_throwsParseException() {
        assertParseFailure(new EditSessionCommandParser(), " 1", EditSessionCommand.MESSAGE_NOT_EDITED);
    }

    @Test
    public void parse_editSessionWithInvalidIndex_throwsParseException() {
        assertParseFailure(new EditSessionCommandParser(), " zero -l Golf Club",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditSessionCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_editSessionWithUnsupportedNameOption_throwsParseException() {
        assertParseFailure(new EditSessionCommandParser(), " 1 -l Community Center -n Renamed",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditSessionCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_editSessionWithUnsupportedOptionAtStart_throwsParseException() {
        assertParseFailure(new EditSessionCommandParser(), "-n Renamed 1 -l Community Center",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditSessionCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_editSessionWithQuotedOptionText_returnsCommand() {
        EditSessionCommand expectedCommand = new EditSessionCommand(Index.fromOneBased(1),
                "\"Community Center -n Renamed\"", null);
        assertParseSuccess(new EditSessionCommandParser(), " 1 -l \"Community Center -n Renamed\"", expectedCommand);
    }

    @Test
    public void parse_editSessionWithLocationHyphens_returnsCommand() {
        EditSessionCommand expectedCommand = new EditSessionCommand(Index.fromOneBased(1), "East - West", null);
        assertParseSuccess(new EditSessionCommandParser(), " 1 -l East - West", expectedCommand);

        EditSessionCommand hyphenatedCommand = new EditSessionCommand(Index.fromOneBased(1), "East-West", null);
        assertParseSuccess(new EditSessionCommandParser(), " 1 -l East-West", hyphenatedCommand);
    }

    @Test
    public void parse_editSessionWithTrailingHyphen_returnsCommand() {
        EditSessionCommand expectedCommand = new EditSessionCommand(Index.fromOneBased(1), "Golf Club -", null);
        assertParseSuccess(new EditSessionCommandParser(), " 1 -l Golf Club -", expectedCommand);
    }

    @Test
    public void parse_editSessionWithRepeatedField_throwsParseException() {
        assertThrows(ParseException.class, () -> new EditSessionCommandParser().parse(
                " 1 -l Golf Club -l Another Club"));
    }

    @Test
    public void parse_editSessionWithBlankLocation_throwsParseException() {
        assertParseFailure(new EditSessionCommandParser(), " 1 -l ", MESSAGE_INVALID_SESSION_LOCATION);
    }

    @Test
    public void parse_editSessionWithInvalidDateTime_throwsParseException() {
        assertParseFailure(new EditSessionCommandParser(), " 1 -t tomorrow", MESSAGE_INVALID_SESSION_DATE_TIME);
    }

    @Test
    public void parse_deleteSession_returnsCommand() {
        assertParseSuccess(new DeleteSessionCommandParser(), " 1", new DeleteSessionCommand(Index.fromOneBased(1)));
    }

    @Test
    public void parse_deleteSessionWithoutValidIndex_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteSessionCommand.MESSAGE_USAGE);

        assertParseFailure(new DeleteSessionCommandParser(), "", expectedMessage);
        assertParseFailure(new DeleteSessionCommandParser(), " zero", expectedMessage);
        assertParseFailure(new DeleteSessionCommandParser(), " 1 extra", expectedMessage);
    }

    private static void assertInvalidSessionCommandFormat(String args) {
        assertParseFailure(new AddSessionCommandParser(), args,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddSessionCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_viewSession_returnsCommand() {
        assertParseSuccess(new ViewSessionCommandParser(), " 1", new ViewSessionCommand(Index.fromOneBased(1)));
    }

    @Test
    public void parse_viewSessionInvalidIndex_throwsParseException() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewSessionCommand.MESSAGE_USAGE);
        assertParseFailure(new ViewSessionCommandParser(), "", expected);
        assertParseFailure(new ViewSessionCommandParser(), " a", expected);
        assertParseFailure(new ViewSessionCommandParser(), " 0", expected);
        assertParseFailure(new ViewSessionCommandParser(), " -1", expected);
    }
}
