package seedu.hobbyhub.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.logic.commands.UnassignCommand;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.model.session.SessionName;

/** Tests parsing for the {@code unassign} command. */
public class UnassignCommandParserTest {

    private final UnassignCommandParser parser = new UnassignCommandParser();

    @Test
    public void parse_validArguments_returnsCommand() throws Exception {
        UnassignCommand expected = new UnassignCommand(Index.fromOneBased(2),
                new SessionName("Weekend Chess Meetup"));

        assertEquals(expected, parser.parse(" 2 Weekend Chess Meetup "));
    }

    @Test
    public void parse_missingArguments_throwsParseException() {
        ParseException exception = assertThrows(ParseException.class, () -> parser.parse("  "));

        assertEquals(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, UnassignCommand.MESSAGE_USAGE),
                exception.getMessage());
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        ParseException exception = assertThrows(ParseException.class, () -> parser.parse("zero Chess Meetup"));
        String expectedMessage = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                UnassignCommand.MESSAGE_USAGE + "\n" + ParserUtil.MESSAGE_INVALID_INDEX);

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void parse_invalidSessionName_throwsParseException() {
        ParseException exception = assertThrows(ParseException.class, () -> parser.parse("1 Invalid#Session"));
        String expectedMessage = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                UnassignCommand.MESSAGE_USAGE + "\n" + SessionName.MESSAGE_CONSTRAINTS);

        assertEquals(expectedMessage, exception.getMessage());
    }
}
