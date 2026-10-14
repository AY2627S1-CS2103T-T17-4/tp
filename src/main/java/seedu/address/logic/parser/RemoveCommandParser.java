package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;

import seedu.address.logic.commands.RemoveCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the single contact option accepted by the remove command.
 */
public class RemoveCommandParser implements Parser<RemoveCommand> {
    public static final String MESSAGE_REQUIRED = "Invalid command format! remove requires a contact.";
    public static final String MESSAGE_REPEATED = "Invalid command format! -c can only be specified once.";
    public static final String MESSAGE_UNEXPECTED = "Invalid command format! remove accepts only -c EMAIL.";
    public static final String MESSAGE_INVALID_EMAIL =
            "Invalid contact! Contact must be a valid email, e.g. john@example.com.";

    @Override
    public RemoveCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(MESSAGE_REQUIRED);
        }
        String[] tokens = trimmedArgs.split("\\s+");
        String contactOption = PREFIX_EMAIL.getPrefix();
        int contactCount = 0;
        for (String token : tokens) {
            if (token.equals(contactOption)) {
                contactCount++;
            }
        }
        if (contactCount > 1) {
            throw new ParseException(MESSAGE_REPEATED);
        }
        if (!tokens[0].equals(contactOption)) {
            throw new ParseException(MESSAGE_UNEXPECTED);
        }
        if (tokens.length == 1) {
            throw new ParseException(MESSAGE_REQUIRED);
        }
        if (tokens.length != 2 || tokens[1].startsWith("-")) {
            throw new ParseException(MESSAGE_UNEXPECTED);
        }
        String value = tokens[1];
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        if (value.isEmpty()) {
            throw new ParseException(MESSAGE_REQUIRED);
        }
        try {
            return new RemoveCommand(ParserUtil.parseEmail(value));
        } catch (ParseException e) {
            throw new ParseException(MESSAGE_INVALID_EMAIL, e);
        }
    }
}
