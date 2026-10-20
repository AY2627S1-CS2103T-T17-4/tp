package seedu.hobbyhub.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.hobbyhub.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.hobbyhub.commons.core.LogsCenter;
import seedu.hobbyhub.logic.commands.AddCommand;
import seedu.hobbyhub.logic.commands.ClearCommand;
import seedu.hobbyhub.logic.commands.Command;
import seedu.hobbyhub.logic.commands.DeleteCommand;
import seedu.hobbyhub.logic.commands.EditCommand;
import seedu.hobbyhub.logic.commands.ExitCommand;
import seedu.hobbyhub.logic.commands.HelpCommand;
import seedu.hobbyhub.logic.commands.ListCommand;
import seedu.hobbyhub.logic.commands.SearchCommand;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.logic.parser.session.AddSessionCommandParser;
import seedu.hobbyhub.logic.parser.session.DeleteSessionCommandParser;
import seedu.hobbyhub.logic.parser.session.EditSessionCommandParser;
import seedu.hobbyhub.logic.parser.session.SearchSessionCommandParser;
import seedu.hobbyhub.model.ListState;

/**
 * Parses user input.
 */
public class HobbyHubParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");
    private static final Logger logger = LogsCenter.getLogger(HobbyHubParser.class);

    /**
     * Parses user input into command for execution.
     *
     * Commands that act on a list ({@code add}, {@code edit}, {@code delete} and {@code search})
     * are parsed for the list that is currently showing.
     *
     * @param userInput full user input string
     * @param listState the list currently shown to the user
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform to the expected format
     */
    public Command parseCommand(String userInput, ListState listState) throws ParseException {
        requireNonNull(listState);
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        // Note to developers: Change LOG_LEVEL in LogsCenter to enable lower level (i.e., FINE, FINER and lower)
        // log messages such as the one below.
        // Lower level log messages are used sparingly to minimize noise in the code.
        logger.fine("Command word: " + commandWord + "; Arguments: " + arguments + "; List: " + listState);

        return switch (commandWord) {
            case AddCommand.COMMAND_WORD ->
                parseForList(listState, new AddCommandParser(), new AddSessionCommandParser(), arguments);
            case EditCommand.COMMAND_WORD ->
                parseForList(listState, new EditCommandParser(), new EditSessionCommandParser(), arguments);
            case DeleteCommand.COMMAND_WORD ->
                parseForList(listState, new DeleteCommandParser(), new DeleteSessionCommandParser(), arguments);
            case SearchCommand.COMMAND_WORD ->
                parseForList(listState, new SearchCommandParser(), new SearchSessionCommandParser(), arguments);
            case ClearCommand.COMMAND_WORD -> new ClearCommand();
            case ListCommand.COMMAND_WORD -> new ListCommandParser().parse(arguments);
            case ExitCommand.COMMAND_WORD -> new ExitCommand();
            case HelpCommand.COMMAND_WORD -> new HelpCommand();
            default -> {
                logger.finer("This user input caused a ParseException: " + userInput);
                throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
            }
        };
    }

    /**
     * Parses {@code arguments} with {@code memberParser} if the member list is showing,
     * or with {@code sessionParser} if the session list is showing.
     */
    private static Command parseForList(ListState listState, Parser<? extends Command> memberParser,
            Parser<? extends Command> sessionParser, String arguments) throws ParseException {
        Parser<? extends Command> parser = listState == ListState.MEMBERS ? memberParser : sessionParser;
        return parser.parse(arguments);
    }

}
