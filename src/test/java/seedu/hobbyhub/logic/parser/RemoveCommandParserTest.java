package seedu.hobbyhub.logic.parser;

import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.logic.commands.RemoveCommand;
import seedu.hobbyhub.model.member.Email;

public class RemoveCommandParserTest {
    private final RemoveCommandParser parser = new RemoveCommandParser();

    @Test
    public void parse_validContact_returnsRemoveCommand() {
        RemoveCommand expected = new RemoveCommand(new Email("John@email.com"));
        assertParseSuccess(parser, " -c John@email.com ", expected);
        assertParseSuccess(parser, "\t-c\tJohn@email.com\t", expected);
        assertParseSuccess(parser, "-c \"John@email.com\"", expected);
    }

    @Test
    public void parse_missingContact_fails() {
        for (String input : new String[] {"", " ", "-c", "-c \"\""}) {
            assertParseFailure(parser, input, RemoveCommandParser.MESSAGE_REQUIRED);
        }
    }

    @Test
    public void parse_repeatedContact_fails() {
        assertParseFailure(parser, "-c -c john@email.com", RemoveCommandParser.MESSAGE_REPEATED);
        assertParseFailure(parser, "-c john@email.com -c jane@email.com", RemoveCommandParser.MESSAGE_REPEATED);
    }

    @Test
    public void parse_unexpectedArguments_fails() {
        for (String input : new String[] {"john@email.com", "-n John", "-C john@email.com",
            "-cjohn@email.com", "-c john@email.com extra", "-c john@email.com -n John", "-c -h"}) {
            assertParseFailure(parser, input, RemoveCommandParser.MESSAGE_UNEXPECTED);
        }
    }

    @Test
    public void parse_invalidEmail_fails() {
        for (String input : new String[] {"-c abc", "-c john@", "-c \"john@email.com"}) {
            assertParseFailure(parser, input, RemoveCommandParser.MESSAGE_INVALID_EMAIL);
        }
    }
}
