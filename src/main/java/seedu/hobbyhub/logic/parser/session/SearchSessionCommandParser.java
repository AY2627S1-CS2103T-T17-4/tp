package seedu.hobbyhub.logic.parser.session;

import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_DATETIME;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_LOCATION;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_SEARCH_COMPLETED;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_SEARCH_NAME;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import seedu.hobbyhub.logic.commands.session.SearchSessionCommand;
import seedu.hobbyhub.logic.parser.Parser;
import seedu.hobbyhub.logic.parser.ParserUtil;
import seedu.hobbyhub.logic.parser.Prefix;
import seedu.hobbyhub.logic.parser.SearchArgument;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.model.session.SessionSearchPredicate;
import seedu.hobbyhub.model.session.SessionSearchPredicate.Field;

/**
 * Parses a single session search criterion while the session list is showing.
 */
public class SearchSessionCommandParser implements Parser<SearchSessionCommand> {
    @Override
    public SearchSessionCommand parse(String args) throws ParseException {
        SearchArgument argument = SearchArgument.parse(args, SearchSessionCommand.MESSAGE_USAGE,
                PREFIX_SEARCH_NAME, PREFIX_LOCATION, PREFIX_DATETIME, PREFIX_SEARCH_COMPLETED, PREFIX_EMAIL);
        Prefix prefix = argument.getPrefix();
        String query = argument.getQuery();
        Field field;
        if (prefix.equals(PREFIX_SEARCH_NAME)) {
            field = Field.NAME;
        } else if (prefix.equals(PREFIX_LOCATION)) {
            field = Field.LOCATION;
        } else if (prefix.equals(PREFIX_EMAIL)) {
            field = Field.EMAIL;
            query = ParserUtil.parseEmail(query).value;
        } else if (prefix.equals(PREFIX_DATETIME)) {
            field = Field.DATE;
            try {
                if (!query.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
                    throw new DateTimeParseException("Expected YYYY-MM-DD", query, 0);
                }
                query = LocalDate.parse(query).toString();
            } catch (DateTimeParseException e) {
                throw new ParseException("Search date must be a valid calendar date in YYYY-MM-DD format.");
            }
        } else {
            field = Field.COMPLETED;
            if (!query.equalsIgnoreCase("true") && !query.equalsIgnoreCase("false")) {
                throw new ParseException("Completion status must be true or false.");
            }
        }
        return new SearchSessionCommand(new SessionSearchPredicate(field, query));
    }
}
