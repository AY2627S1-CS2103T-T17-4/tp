package seedu.hobbyhub.logic.parser.session;

import static seedu.hobbyhub.logic.Messages.MESSAGE_SESSION_COMMAND_UNAVAILABLE;

import seedu.hobbyhub.logic.commands.Command;
import seedu.hobbyhub.logic.parser.Parser;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;

/**
 * Parses the {@code add} command while the session list is showing.
 * This is a placeholder until session add is implemented: it rejects every input.
 */
public class AddSessionCommandParser implements Parser<Command> {

    @Override
    public Command parse(String args) throws ParseException {
        throw new ParseException(String.format(MESSAGE_SESSION_COMMAND_UNAVAILABLE, "add"));
    }
}
