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
    private static final Set<String> SUPPORTED_OPTIONS = Set.of("-n", "-p", "-c", "-a", "-h", "-g", "-t");
    private static final String MESSAGE_UNKNOWN_OPTION =
            "Unknown add option. Use only -n, -p, -c, -a, -h, or -g.";
    private static final String MESSAGE_EMPTY_HOBBY =
            "Each -h option needs a hobby value. Use -h \"HOBBY:SKILL\".";

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        if (hasUnsupportedOption(args)) {
            throw new ParseException(MESSAGE_UNKNOWN_OPTION);
        }

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                        PREFIX_HOBBY, PREFIX_GROUP, PREFIX_LEGACY_TAG);

        if (argMultimap.getValue(PREFIX_LEGACY_TAG).isPresent()) {
            throw new ParseException("The -t option has been replaced by -g for groups.");
        }

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_EMAIL, PREFIX_HOBBY)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS);
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Phone phone = argMultimap.getValue(PREFIX_PHONE).isPresent()
                ? ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get()) : Phone.empty();
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
        Address address = argMultimap.getValue(PREFIX_ADDRESS).isPresent()
                ? ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get()) : Address.empty();
        List<String> hobbyValues = argMultimap.getAllValues(PREFIX_HOBBY);
        if (hobbyValues.stream().anyMatch(String::isBlank)) {
            throw new ParseException(MESSAGE_EMPTY_HOBBY);
        }
        List<Hobby> hobbies = ParserUtil.parseHobbies(hobbyValues);
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

    /** Returns whether an option-like token outside quoted text is unsupported or ambiguously repeated. */
    private static boolean hasUnsupportedOption(String args) {
        boolean insideQuotes = false;
        for (int i = 0; i < args.length(); i++) {
            char currentCharacter = args.charAt(i);
            if (currentCharacter == '"') {
                insideQuotes = !insideQuotes;
                continue;
            }
            if (insideQuotes || currentCharacter != '-' || !isAtTokenStart(args, i)) {
                continue;
            }

            int optionEnd = i + 1;
            while (optionEnd < args.length() && Character.isLetter(args.charAt(optionEnd))) {
                optionEnd++;
            }
            if (optionEnd == i + 1) {
                return true;
            }

            String option = args.substring(i, optionEnd);
            if (!startsWithSupportedOption(option) || isRepeatedOptionLetter(option)) {
                return true;
            }
            i = optionEnd - 1;
        }
        return false;
    }

    /** Returns whether the hyphen at {@code index} begins a whitespace-delimited token. */
    private static boolean isAtTokenStart(String args, int index) {
        return index == 0 || Character.isWhitespace(args.charAt(index - 1));
    }

    /** Returns whether a compact option token starts with a supported option prefix. */
    private static boolean startsWithSupportedOption(String option) {
        return SUPPORTED_OPTIONS.stream().anyMatch(option::startsWith);
    }

    /** Returns whether a compact token repeats its option letter, such as {@code -hh}. */
    private static boolean isRepeatedOptionLetter(String option) {
        return option.length() == 3 && option.charAt(1) == option.charAt(2);
    }

}
