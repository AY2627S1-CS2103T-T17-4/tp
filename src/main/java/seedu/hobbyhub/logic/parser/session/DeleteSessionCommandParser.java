package seedu.hobbyhub.logic.parser.session;

import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.logic.commands.session.DeleteSessionCommand;
import seedu.hobbyhub.logic.parser.Parser;
import seedu.hobbyhub.logic.parser.ParserUtil;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;

/**
 * Parses the {@code delete} command while the session list is showing.
 * Parses the index of the session to delete.
 */
public class DeleteSessionCommandParser implements Parser<DeleteSessionCommand> {

    @Override
    public DeleteSessionCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new DeleteSessionCommand(index);
        } catch (ParseException exception) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    DeleteSessionCommand.MESSAGE_USAGE), exception);
        }
    }
}
