package seedu.hobbyhub.logic.parser.session;

import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.logic.commands.session.MarkSessionCommand;
import seedu.hobbyhub.logic.parser.Parser;
import seedu.hobbyhub.logic.parser.ParserUtil;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;

/**
 * Parses the {@code mark} or {@code unmark} command while the session list is showing.
 */
public class MarkSessionCommandParser implements Parser<MarkSessionCommand> {

    private final boolean isCompleted;

    /**
     * Creates a parser for {@code mark} if {@code isCompleted} is true, or for {@code unmark} otherwise.
     */
    public MarkSessionCommandParser(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    @Override
    public MarkSessionCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new MarkSessionCommand(index, isCompleted);
        } catch (ParseException pe) {
            String usage = isCompleted ? MarkSessionCommand.MESSAGE_USAGE : MarkSessionCommand.MESSAGE_UNMARK_USAGE;
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage), pe);
        }
    }
}
