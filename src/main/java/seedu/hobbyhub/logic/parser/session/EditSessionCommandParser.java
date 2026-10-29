package seedu.hobbyhub.logic.parser.session;

import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_DATETIME;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_LOCATION;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.logic.commands.session.EditSessionCommand;
import seedu.hobbyhub.logic.parser.ArgumentMultimap;
import seedu.hobbyhub.logic.parser.ArgumentTokenizer;
import seedu.hobbyhub.logic.parser.Parser;
import seedu.hobbyhub.logic.parser.ParserUtil;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;

/**
 * Parses the {@code edit} command while the session list is showing.
 */
public class EditSessionCommandParser implements Parser<EditSessionCommand> {

    public static final String MESSAGE_UNKNOWN_OPTION =
            "Unknown session edit option. Use only -l or -t. A session's name cannot be edited.";

    @Override
    public EditSessionCommand parse(String args) throws ParseException {
        ArgumentTokenizer.verifyNoUnknownOptions(args, MESSAGE_UNKNOWN_OPTION, PREFIX_LOCATION, PREFIX_DATETIME);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_LOCATION, PREFIX_DATETIME);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException exception) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditSessionCommand.MESSAGE_USAGE),
                    exception);
        }

        if (!hasFieldsToEdit(argMultimap)) {
            throw new ParseException(EditSessionCommand.MESSAGE_NOT_EDITED);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_LOCATION, PREFIX_DATETIME);

        return new EditSessionCommand(
                index,
                argMultimap.getValue(PREFIX_LOCATION).isPresent()
                        ? ParserUtil.parseSessionLocation(argMultimap.getValue(PREFIX_LOCATION).orElseThrow()) : null,
                argMultimap.getValue(PREFIX_DATETIME).isPresent()
                        ? ParserUtil.parseSessionDateTime(argMultimap.getValue(PREFIX_DATETIME).orElseThrow()) : null);
    }

    /** Returns true if at least one editable session field is present. */
    private static boolean hasFieldsToEdit(ArgumentMultimap argumentMultimap) {
        return argumentMultimap.getValue(PREFIX_LOCATION).isPresent()
                || argumentMultimap.getValue(PREFIX_DATETIME).isPresent();
    }
}
