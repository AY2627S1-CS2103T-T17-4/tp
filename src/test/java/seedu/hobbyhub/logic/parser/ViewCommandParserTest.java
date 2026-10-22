package seedu.hobbyhub.logic.parser;

import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.hobbyhub.testutil.TypicalIndexes.INDEX_FIRST_MEMBER;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.logic.commands.ViewCommand;

public class ViewCommandParserTest {

    private ViewCommandParser parser = new ViewCommandParser();

    @Test
    public void parse_validArgs_returnsViewCommand() {
        assertParseSuccess(parser, "1", new ViewCommand(INDEX_FIRST_MEMBER));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "a", expected);
        assertParseFailure(parser, "", expected);
        assertParseFailure(parser, "0", expected);
        assertParseFailure(parser, "-1", expected);
    }
}
