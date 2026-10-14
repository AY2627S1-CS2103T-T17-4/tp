package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SEARCH_HOBBY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SEARCH_NAME;

import java.util.List;

import seedu.address.logic.commands.SearchCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.HobbyContainsKeywordsPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new SearchCommand object
 */
public class SearchCommandParser implements Parser<SearchCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the SearchCommand
     * and returns a SearchCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public SearchCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(
                args, PREFIX_SEARCH_NAME, PREFIX_SEARCH_HOBBY);

        boolean hasNamePrefix = argMultimap.getValue(PREFIX_SEARCH_NAME).isPresent();
        boolean hasHobbyPrefix = argMultimap.getValue(PREFIX_SEARCH_HOBBY).isPresent();

        if (hasNamePrefix == hasHobbyPrefix || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SearchCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_SEARCH_NAME, PREFIX_SEARCH_HOBBY);

        String query = hasNamePrefix
                ? argMultimap.getValue(PREFIX_SEARCH_NAME).orElseThrow()
                : argMultimap.getValue(PREFIX_SEARCH_HOBBY).orElseThrow();
        query = stripOptionalQuotes(query);

        if (query.isBlank()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SearchCommand.MESSAGE_USAGE));
        }

        List<String> keywords = List.of(query);
        return hasNamePrefix
                ? new SearchCommand(new NameContainsKeywordsPredicate(keywords))
                : new SearchCommand(new HobbyContainsKeywordsPredicate(keywords));
    }

    /** Removes one matching pair of surrounding double quotes from a query, if present. */
    private static String stripOptionalQuotes(String query) {
        String trimmedQuery = query.trim();
        if (trimmedQuery.length() >= 2
                && trimmedQuery.startsWith("\"")
                && trimmedQuery.endsWith("\"")) {
            return trimmedQuery.substring(1, trimmedQuery.length() - 1).trim();
        }
        return trimmedQuery;
    }

}
