package seedu.hobbyhub.logic.parser.session;

import static seedu.hobbyhub.logic.Messages.MESSAGE_SESSION_COMMAND_UNAVAILABLE;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseFailure;

import org.junit.jupiter.api.Test;

/**
 * Tests the placeholder parsers used while the session list is showing.
 * Replace each test when the real session command is implemented.
 */
public class SessionCommandParserStubsTest {

    @Test
    public void parse_addSession_throwsParseException() {
        assertParseFailure(new AddSessionCommandParser(), " -n Golf Meet -d 2026-10-24",
                String.format(MESSAGE_SESSION_COMMAND_UNAVAILABLE, "add"));
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
}
