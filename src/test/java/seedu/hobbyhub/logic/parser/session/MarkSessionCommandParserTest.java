package seedu.hobbyhub.logic.parser.session;

import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.logic.commands.session.MarkSessionCommand;

public class MarkSessionCommandParserTest {

    private final MarkSessionCommandParser markParser = new MarkSessionCommandParser(true);
    private final MarkSessionCommandParser unmarkParser = new MarkSessionCommandParser(false);

    @Test
    public void parse_validIndex_returnsCommand() {
        assertParseSuccess(markParser, " 1", new MarkSessionCommand(Index.fromOneBased(1), true));
        assertParseSuccess(unmarkParser, " 2 ", new MarkSessionCommand(Index.fromOneBased(2), false));
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        String markFailure = String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkSessionCommand.MESSAGE_USAGE);
        String unmarkFailure = String.format(MESSAGE_INVALID_COMMAND_FORMAT, MarkSessionCommand.MESSAGE_UNMARK_USAGE);

        assertParseFailure(markParser, "", markFailure);
        assertParseFailure(markParser, "a", markFailure);
        assertParseFailure(markParser, "0", markFailure);
        assertParseFailure(unmarkParser, "1 2", unmarkFailure);
    }
}
