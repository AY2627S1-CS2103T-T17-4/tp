package seedu.hobbyhub.logic.parser.session;

import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.hobbyhub.logic.Messages.MESSAGE_SESSION_COMMAND_UNAVAILABLE;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.logic.commands.session.AddSessionCommand;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.testutil.SessionBuilder;

/**
 * Tests the parsers used while the session list is showing.
 */
public class SessionCommandParserStubsTest {

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
    public void parse_addSession_missingName_throwsParseException() {
        assertInvalidSessionCommandFormat("-l Golf Club -t 2026-10-24T14:00");
    }

    @Test
    public void parse_addSession_missingLocation_throwsParseException() {
        assertInvalidSessionCommandFormat("-n Golf Meet -t 2026-10-24T14:00");
    }

    @Test
    public void parse_addSession_missingDateTime_throwsParseException() {
        assertInvalidSessionCommandFormat("-n Golf Meet -l Golf Club");
    }

    @Test
    public void parse_addSession_preamble_throwsParseException() {
        assertInvalidSessionCommandFormat("unexpected -n Golf Meet -l Golf Club -t 2026-10-24T14:00");
    }

    @Test
    public void parse_addSession_duplicatePrefix_throwsParseException() {
        assertThrows(ParseException.class, () -> new AddSessionCommandParser().parse(
                "-n Golf Meet -n Another Meet -l Golf Club -t 2026-10-24T14:00"));
    }

    @Test
    public void parse_editSession_throwsParseException() {
        assertParseFailure(new EditSessionCommandParser(), " 1 -d 2026-10-25",
                String.format(MESSAGE_SESSION_COMMAND_UNAVAILABLE, "edit"));
    }

    @Test
    public void parse_deleteSession_throwsParseException() {
        assertParseFailure(new DeleteSessionCommandParser(), " 1",
                String.format(MESSAGE_SESSION_COMMAND_UNAVAILABLE, "delete"));
    }

    @Test
    public void parse_searchSession_throwsParseException() {
        assertParseFailure(new SearchSessionCommandParser(), " -n golf",
                String.format(MESSAGE_SESSION_COMMAND_UNAVAILABLE, "search"));
    }

    private static void assertInvalidSessionCommandFormat(String args) {
        assertParseFailure(new AddSessionCommandParser(), args,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddSessionCommand.MESSAGE_USAGE));
    }
}
