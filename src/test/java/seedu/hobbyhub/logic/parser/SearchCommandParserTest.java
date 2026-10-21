package seedu.hobbyhub.logic.parser;

import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.logic.commands.SearchCommand;
import seedu.hobbyhub.model.member.Email;
import seedu.hobbyhub.model.member.HobbyContainsKeywordsPredicate;
import seedu.hobbyhub.model.member.MemberSearchPredicate;
import seedu.hobbyhub.model.member.MemberSearchPredicate.Field;
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

    @Test
    public void parse_newFields_returnsSearchCommand() {
        assertParseSuccess(parser, " -g \"Weekend Club\"",
                new SearchCommand(new MemberSearchPredicate(Field.GROUP, "Weekend Club")));
        assertParseSuccess(parser, " -c ALICE@example.com",
                new SearchCommand(new MemberSearchPredicate(Field.EMAIL, "ALICE@example.com")));
        for (String level : List.of("Beginner", "Intermediate", "Advanced", "advanced")) {
            assertParseSuccess(parser, " -sl " + level,
                    new SearchCommand(new MemberSearchPredicate(Field.SKILL_LEVEL, level)));
        }
    }

    @Test
    public void parse_invalidOptions_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SearchCommand.MESSAGE_USAGE);
        for (String args : List.of("-g", "-sl", "-c", "-n \" \"", "-x Alice", "-p 91234567",
                "-a Street", "-l Club", "-n Alice -g Club", "-sl Advanced -h Chess",
                "-g Club -g Other", "-n Alice -x unknown", "-n -x unknown", "-name Alice", "-nAlice")) {
            assertParseFailure(parser, args, expectedMessage);
        }
        assertParseFailure(parser, "-c invalid", Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "-sl Expert", "Skill level must be Beginner, Intermediate, or Advanced.");
    }

    @Test
    public void parse_whitespaceAndQuotedOptions_preservesQuery() {
        assertParseSuccess(parser, "\t-n\tAlice Lee\t",
                new SearchCommand(new NameContainsKeywordsPredicate(List.of("Alice Lee"))));
        assertParseSuccess(parser, "-n \"Alice -g Lee\"",
                new SearchCommand(new NameContainsKeywordsPredicate(List.of("Alice -g Lee"))));
        assertParseSuccess(parser, "-n \"-x value\"",
                new SearchCommand(new NameContainsKeywordsPredicate(List.of("-x value"))));
        assertParseSuccess(parser, "-n Jean-Luc",
                new SearchCommand(new NameContainsKeywordsPredicate(List.of("Jean-Luc"))));
    }

}
