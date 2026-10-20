package seedu.hobbyhub.logic.parser;

import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.logic.commands.ListCommand;
import seedu.hobbyhub.model.ListState;

public class ListCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE);

    private final ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_noArgs_returnsListCommandForCurrentList() {
        assertParseSuccess(parser, "", new ListCommand());
        assertParseSuccess(parser, "   ", new ListCommand());
    }

    @Test
    public void parse_memberFlag_returnsListCommandForMembers() {
        assertParseSuccess(parser, " -m", new ListCommand(ListState.MEMBERS));
        assertParseSuccess(parser, "  -m  ", new ListCommand(ListState.MEMBERS));
    }

    @Test
    public void parse_sessionFlag_returnsListCommandForSessions() {
        assertParseSuccess(parser, " -s", new ListCommand(ListState.SESSIONS));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, " 3", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " -x", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " -M", MESSAGE_INVALID_FORMAT); // flags are case-sensitive
        assertParseFailure(parser, " -m -s", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " -sessions", MESSAGE_INVALID_FORMAT);
    }
}
