package seedu.hobbyhub.logic.parser.session;

import static seedu.hobbyhub.logic.Messages.MESSAGE_SESSION_COMMAND_UNAVAILABLE;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.logic.commands.session.AddSessionCommand;
import seedu.hobbyhub.testutil.SessionBuilder;

/**
 * Tests the parsers used while the session list is showing.
 */
public class SessionCommandParserStubsTest {

    @Test
    public void parse_addSession_validFields_returnsCommand() {
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
