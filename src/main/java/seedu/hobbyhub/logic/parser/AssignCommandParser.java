package seedu.hobbyhub.logic.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.logic.commands.AssignCommand;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.model.session.SessionName;

/** Parses arguments for the {@code assign} command. */
public class AssignCommandParser implements Parser<AssignCommand> {

    private static final Pattern ARGUMENT_FORMAT = Pattern.compile("(?<index>\\S+)\\s+(?<sessionName>.+)");

    @Override
    public AssignCommand parse(String args) throws ParseException {
        Matcher matcher = ARGUMENT_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                    AssignCommand.MESSAGE_USAGE));
        }

        try {
            Index memberIndex = ParserUtil.parseIndex(matcher.group("index"));
            String sessionName = matcher.group("sessionName").trim();
            if (!SessionName.isValidSessionName(sessionName)) {
                throw new ParseException(SessionName.MESSAGE_CONSTRAINTS);
            }
            return new AssignCommand(memberIndex, new SessionName(sessionName));
        } catch (ParseException pe) {
            throw new ParseException(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
                    AssignCommand.MESSAGE_USAGE + "\n" + pe.getMessage()), pe);
        }
    }
}
