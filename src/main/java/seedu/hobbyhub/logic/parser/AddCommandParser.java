package seedu.hobbyhub.logic.parser;

import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_HOBBY;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_LEGACY_TAG;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import seedu.hobbyhub.logic.commands.AddCommand;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.model.group.Group;
import seedu.hobbyhub.model.member.Address;
import seedu.hobbyhub.model.member.Email;
import seedu.hobbyhub.model.member.Hobby;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.member.Name;
import seedu.hobbyhub.model.member.Phone;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                        PREFIX_HOBBY, PREFIX_GROUP, PREFIX_LEGACY_TAG);

        if (argMultimap.getValue(PREFIX_LEGACY_TAG).isPresent()) {
            throw new ParseException("The -t option has been replaced by -g for groups.");
        }

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_ADDRESS, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_HOBBY)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS);
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
        Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get());
        List<Hobby> hobbies = ParserUtil.parseHobbies(argMultimap.getAllValues(PREFIX_HOBBY));
        Set<Group> groupList = ParserUtil.parseGroups(argMultimap.getAllValues(PREFIX_GROUP));

        Member member = new Member(name, phone, email, address, hobbies, groupList);

        return new AddCommand(member);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
