package seedu.hobbyhub.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.logic.commands.SearchCommand;
import seedu.hobbyhub.model.AllMatchPredicate;
import seedu.hobbyhub.model.member.Email;
import seedu.hobbyhub.model.member.HobbyAndSkillPredicate;
import seedu.hobbyhub.model.member.HobbyContainsKeywordsPredicate;
import seedu.hobbyhub.model.member.MemberSearchPredicate;
import seedu.hobbyhub.model.member.MemberSearchPredicate.Field;
import seedu.hobbyhub.model.member.NameContainsKeywordsPredicate;
import seedu.hobbyhub.model.member.SkillLevel;

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
    public void parse_missingCriterion_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SearchCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " Alice", expectedMessage);
        assertParseFailure(parser, " stray -n Alice", expectedMessage);
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
                "-a Street", "-l Club", "-n Alice -g", "-sl Advanced -h \"\"",
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

    @Test
    public void parse_combinedFields_returnsAllMatchCommand() throws Exception {
        SearchCommand expected = new SearchCommand(new AllMatchPredicate<>(List.of(
                new MemberSearchPredicate(Field.GROUP, "Weekend Club"),
                new NameContainsKeywordsPredicate(List.of("Alice")))));
        assertParseSuccess(parser, "-n Alice -g \"Weekend Club\"", expected);
        assertEquals(parser.parse("-n Alice -g Weekend Club"), parser.parse("-g Weekend Club -n Alice"));
        assertParseSuccess(parser, "-n \"Alice -h Chess\" -g Club", new SearchCommand(new AllMatchPredicate<>(List.of(
                new MemberSearchPredicate(Field.GROUP, "Club"),
                new NameContainsKeywordsPredicate(List.of("Alice -h Chess"))))));
    }

    @Test
    public void parse_hobbyAndSkill_usesSameHobbyPredicate() throws Exception {
        SearchCommand expected = new SearchCommand(new HobbyAndSkillPredicate("Chess", SkillLevel.ADVANCED));
        assertParseSuccess(parser, "-h Chess -sl Advanced", expected);
        assertParseSuccess(parser, "-sl advanced -h Chess", expected);
        SearchCommand grouped = new SearchCommand(new AllMatchPredicate<>(List.of(
                new HobbyAndSkillPredicate("Chess", SkillLevel.ADVANCED),
                new MemberSearchPredicate(Field.GROUP, "Weekend"))));
        assertParseSuccess(parser, "-g Weekend -sl Advanced -h Chess", grouped);
        assertEquals(parser.parse("-n Alice -g Weekend -sl Advanced -h Chess"),
                parser.parse("-h Chess -sl Advanced -g Weekend -n Alice"));
    }

    @Test
    public void parse_invalidCombinedCriteria_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SearchCommand.MESSAGE_USAGE);
        for (String args : List.of("-n Alice -n Bob", "-g Weekend -g Club", "-h Chess -sl Beginner -sl Advanced",
                "-n Alice -c", "-n Alice -g \"\"", "-h Chess -sl", "-n Alice -t 2026-10-24")) {
            assertParseFailure(parser, args, message);
        }
        assertParseFailure(parser, "-n Alice -c invalid", Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "-h Chess -sl Expert", "Skill level must be Beginner, Intermediate, or Advanced.");
        assertParseFailure(parser, "-n Alice -sl Expert", "Skill level must be Beginner, Intermediate, or Advanced.");
    }

}
