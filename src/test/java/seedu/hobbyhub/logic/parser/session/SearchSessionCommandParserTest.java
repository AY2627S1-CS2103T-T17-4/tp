package seedu.hobbyhub.logic.parser.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.logic.commands.session.SearchSessionCommand;
import seedu.hobbyhub.model.AllMatchPredicate;
import seedu.hobbyhub.model.member.Email;
import seedu.hobbyhub.model.session.SessionSearchPredicate;
import seedu.hobbyhub.model.session.SessionSearchPredicate.Field;

public class SearchSessionCommandParserTest {
    private final SearchSessionCommandParser parser = new SearchSessionCommandParser();

    @Test
    public void parse_validCriteria_returnsSearchCommand() {
        assertParseSuccess(parser, "-n \"Weekend Chess\"", command(Field.NAME, "Weekend Chess"));
        assertParseSuccess(parser, "-l Community Center", command(Field.LOCATION, "Community Center"));
        assertParseSuccess(parser, "-t 2026-10-24", command(Field.DATE, "2026-10-24"));
        assertParseSuccess(parser, "-t 2028-02-29", command(Field.DATE, "2028-02-29"));
        assertParseSuccess(parser, "-done TRUE", command(Field.COMPLETED, "true"));
        assertParseSuccess(parser, "-done false", command(Field.COMPLETED, "false"));
        assertParseSuccess(parser, "-c ALICE@example.com", command(Field.EMAIL, "alice@example.com"));
        assertParseSuccess(parser, "\t-l\tCommunity\t", command(Field.LOCATION, "Community"));
        assertParseSuccess(parser, "-n \"Chess -n Meetup\"", command(Field.NAME, "Chess -n Meetup"));
    }

    @Test
    public void parse_missingUnknownOrRepeatedOptions_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SearchSessionCommand.MESSAGE_USAGE);
        for (String args : List.of("", "  ", "Chess", "stray -n Chess", "-n", "-l \"\"", "-t", "-done",
                "-c", "-h Chess", "-g Club", "-sl Advanced", "-p 91234567", "-a Street",
                "-n Chess -l", "-n Chess -n Golf", "-l Club -x unknown", "-name Chess", "-nChess")) {
            assertParseFailure(parser, args, message);
        }
    }

    @Test
    public void parse_invalidTypedValues_throwsParseException() {
        for (String date : List.of("2026-02-29", "2026-04-31", "2026-13-01", "2026-1-01",
                "2026-10-24T14:00", "tomorrow")) {
            assertParseFailure(parser, "-t " + date,
                    "Search date must be a valid calendar date in YYYY-MM-DD format.");
        }
        for (String status : List.of("yes", "0", "false true")) {
            assertParseFailure(parser, "-done " + status, "Completion status must be true or false.");
        }
        assertParseFailure(parser, "-c alice", Email.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_combinedFields_returnsAllMatchCommand() throws Exception {
        SearchSessionCommand expected = new SearchSessionCommand(new AllMatchPredicate<>(List.of(
                new SessionSearchPredicate(Field.LOCATION, "Club"),
                new SessionSearchPredicate(Field.NAME, "Chess"))));
        assertParseSuccess(parser, "-n Chess -l Club", expected);
        assertEquals(parser.parse("-n Chess -l Club"), parser.parse("-l Club -n Chess"));
        assertParseSuccess(parser, "-n \"Chess -n Meetup\" -done false",
                new SearchSessionCommand(new AllMatchPredicate<>(List.of(
                        new SessionSearchPredicate(Field.COMPLETED, "false"),
                        new SessionSearchPredicate(Field.NAME, "Chess -n Meetup")))));
    }

    @Test
    public void parse_invalidCombinedCriteria_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SearchSessionCommand.MESSAGE_USAGE);
        for (String args : List.of("-n Chess -done false -done true", "-n Chess -c", "-l Hall -n \"\"",
                "-n Chess -sl Advanced", "-n Chess -g Club")) {
            assertParseFailure(parser, args, message);
        }
        assertParseFailure(parser, "-n Chess -t 2026-02-29",
                "Search date must be a valid calendar date in YYYY-MM-DD format.");
        assertParseFailure(parser, "-l Hall -done yes", "Completion status must be true or false.");
        assertParseFailure(parser, "-n Chess -c invalid", Email.MESSAGE_CONSTRAINTS);
    }

    private SearchSessionCommand command(Field field, String query) {
        return new SearchSessionCommand(new SessionSearchPredicate(field, query));
    }
}
