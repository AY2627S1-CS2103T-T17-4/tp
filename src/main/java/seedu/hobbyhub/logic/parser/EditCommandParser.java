package seedu.hobbyhub.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_AVAILABILITY;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_HOBBY;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_LEGACY_TAG;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.logic.commands.EditCommand;
import seedu.hobbyhub.logic.commands.EditCommand.EditMemberDescriptor;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.model.member.Address;
import seedu.hobbyhub.model.member.Availability;
import seedu.hobbyhub.model.member.Phone;

/**
 * Parses input arguments and creates a new EditCommand object.
 * Optional fields (phone, address and groups) are replaced by the values given and cleared when their prefix is
 * given without a value. Required fields (name, email and hobbies) cannot be cleared.
 */
public class EditCommandParser implements Parser<EditCommand> {

    public static final String MESSAGE_UNKNOWN_OPTION =
            "Unknown edit option. Use only -n, -p, -c, -a, -h, -g, or -d.";

    private static final Prefix[] SUPPORTED_OPTIONS = {PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
        PREFIX_GROUP, PREFIX_HOBBY, PREFIX_AVAILABILITY, PREFIX_LEGACY_TAG};

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentTokenizer.verifyNoUnknownOptions(args, MESSAGE_UNKNOWN_OPTION, SUPPORTED_OPTIONS);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, SUPPORTED_OPTIONS);

        if (argMultimap.getValue(PREFIX_LEGACY_TAG).isPresent()) {
            throw new ParseException("The -t option has been replaced by -g for groups.");
        }

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS);

        EditMemberDescriptor editMemberDescriptor = new EditMemberDescriptor();

        // Required fields: a blank value is invalid, so these can be replaced but never cleared.
        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editMemberDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            editMemberDescriptor.setEmail(ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get()));
        }
        if (argMultimap.getValue(PREFIX_HOBBY).isPresent()) {
            editMemberDescriptor.setHobbies(ParserUtil.parseHobbies(argMultimap.getAllValues(PREFIX_HOBBY)));
        }

        // Optional fields: a value replaces the field, and a prefix without a value clears it.
        applyOptionalField(argMultimap, PREFIX_PHONE, ParserUtil::parsePhone, Phone.empty(),
                editMemberDescriptor::setPhone);
        applyOptionalField(argMultimap, PREFIX_ADDRESS, ParserUtil::parseAddress, Address.empty(),
                editMemberDescriptor::setAddress);
        OptionalFieldUtil.parseSet(argMultimap.getAllValues(PREFIX_GROUP), ParserUtil::parseGroups)
                .ifPresent(editMemberDescriptor::setGroups);
        parseAvailabilityForEdit(argMultimap.getAllValues(PREFIX_AVAILABILITY))
                .ifPresent(editMemberDescriptor::setAvailability);

        if (!editMemberDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editMemberDescriptor);
    }

    /**
     * Applies the single-valued optional field identified by {@code prefix} through {@code setter}, if the prefix
     * is present in {@code argMultimap}. A blank value clears the field by applying {@code emptyValue}, and any
     * other value is parsed with {@code parser}.
     *
     * @throws ParseException if the value is not blank and is invalid.
     */
    private static <T> void applyOptionalField(ArgumentMultimap argMultimap, Prefix prefix,
            OptionalFieldUtil.ThrowingFunction<String, T> parser, T emptyValue, Consumer<T> setter)
            throws ParseException {
        Optional<String> value = argMultimap.getValue(prefix);
        if (value.isPresent()) {
            setter.accept(OptionalFieldUtil.parseScalar(value.get(), parser, emptyValue));
        }
    }

    /** Parses availability options, with one empty {@code -d} clearing all selected weekdays. */
    private Optional<Availability> parseAvailabilityForEdit(Collection<String> days) throws ParseException {
        requireNonNull(days);
        if (days.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> selectedDays = days.size() == 1 && days.contains("")
                ? Collections.emptySet() : days;
        return Optional.of(ParserUtil.parseAvailability(selectedDays));
    }

}
