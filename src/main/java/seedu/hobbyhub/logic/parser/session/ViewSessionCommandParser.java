package seedu.hobbyhub.logic.parser.session;

import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.logic.commands.session.ViewSessionCommand;
import seedu.hobbyhub.logic.parser.Parser;
import seedu.hobbyhub.logic.parser.ParserUtil;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ViewSessionCommand object.
 */
public class ViewSessionCommandParser implements Parser<ViewSessionCommand> {

    @Override
    public ViewSessionCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new ViewSessionCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewSessionCommand.MESSAGE_USAGE), pe);
        }
    }
}
