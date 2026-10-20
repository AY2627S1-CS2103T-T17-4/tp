package seedu.hobbyhub.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.hobbyhub.logic.commands.ListCommand;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.model.ListState;

/**
 * Parses input arguments and creates a new ListCommand object.
 */
public class ListCommandParser implements Parser<ListCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ListCommand
     * and returns a ListCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public ListCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();

        return switch (trimmedArgs) {
            case "" -> new ListCommand();
            case ListCommand.FLAG_MEMBERS -> new ListCommand(ListState.MEMBERS);
            case ListCommand.FLAG_SESSIONS -> new ListCommand(ListState.SESSIONS);
            default -> throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        };
    }
}
