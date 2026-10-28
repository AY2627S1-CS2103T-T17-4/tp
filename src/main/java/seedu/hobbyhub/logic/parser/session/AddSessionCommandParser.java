package seedu.hobbyhub.logic.parser.session;

import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_DATETIME;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_LOCATION;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.Set;

import seedu.hobbyhub.logic.commands.session.AddSessionCommand;
import seedu.hobbyhub.logic.parser.ArgumentMultimap;
import seedu.hobbyhub.logic.parser.ArgumentTokenizer;
import seedu.hobbyhub.logic.parser.Parser;
import seedu.hobbyhub.logic.parser.ParserUtil;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.model.session.Session;

/**
 * Parses the {@code add} command while the session list is showing.
 */
public class AddSessionCommandParser implements Parser<AddSessionCommand> {

    @Override
    public AddSessionCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenizeWithExactPrefixes(
                args, PREFIX_NAME, PREFIX_LOCATION, PREFIX_DATETIME);

        if (!areRequiredFieldsPresent(argMultimap) || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddSessionCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_LOCATION, PREFIX_DATETIME);

        Session session = new Session(
                ParserUtil.parseSessionName(argMultimap.getValue(PREFIX_NAME).orElseThrow()),
                ParserUtil.parseSessionDateTime(argMultimap.getValue(PREFIX_DATETIME).orElseThrow()),
                ParserUtil.parseSessionLocation(argMultimap.getValue(PREFIX_LOCATION).orElseThrow()),
                false,
                Set.of());

        return new AddSessionCommand(session);
    }

    /** Returns true if all required session fields are present. */
    private static boolean areRequiredFieldsPresent(ArgumentMultimap argumentMultimap) {
        if (argumentMultimap.getValue(PREFIX_NAME).isEmpty()) {
            return false;
        }
        if (argumentMultimap.getValue(PREFIX_LOCATION).isEmpty()) {
            return false;
        }
        return argumentMultimap.getValue(PREFIX_DATETIME).isPresent();
    }
}
