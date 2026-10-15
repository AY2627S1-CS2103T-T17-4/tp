package seedu.hobbyhub.logic.parser;

import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.logic.commands.SearchCommand;
import seedu.hobbyhub.model.member.HobbyContainsKeywordsPredicate;
import seedu.hobbyhub.model.member.NameContainsKeywordsPredicate;

public class SearchCommandParserTest {

    private SearchCommandParser parser = new SearchCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, SearchCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_nameArgs_returnsSearchCommand() {
        SearchCommand expectedSearchCommand =
                new SearchCommand(new NameContainsKeywordsPredicate(List.of("Alice")));
        assertParseSuccess(parser, " -n Alice", expectedSearchCommand);

        // Quoted multi-word fragments are accepted.
        assertParseSuccess(parser, " -n \"Alice Bob\"",
                new SearchCommand(new NameContainsKeywordsPredicate(List.of("Alice Bob"))));

        // An unmatched opening quote is treated as part of the search fragment.
        assertParseSuccess(parser, " -n \"Alice",
                new SearchCommand(new NameContainsKeywordsPredicate(List.of("\"Alice"))));
    }

    @Test
    public void parse_hobbyArgs_returnsSearchCommand() {
        SearchCommand expectedSearchCommand =
                new SearchCommand(new HobbyContainsKeywordsPredicate(List.of("Board Games")));
        assertParseSuccess(parser, " -h Board Games", expectedSearchCommand);
    }

    @Test
    public void parse_missingOrMultipleCriteria_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SearchCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " Alice", expectedMessage);
        assertParseFailure(parser, " stray -n Alice", expectedMessage);
        assertParseFailure(parser, " -n Alice -h Chess", expectedMessage);
        assertParseFailure(parser, " -n", expectedMessage);
    }

}
