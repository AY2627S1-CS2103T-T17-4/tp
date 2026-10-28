package seedu.hobbyhub.logic.parser;

import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.GROUP_DESC_FRIEND;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.GROUP_DESC_HUSBAND;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.HOBBY_DESC_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.INVALID_GROUP_DESC;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.INVALID_HOBBY_DESC;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_ADDRESS_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_GROUP_FRIEND;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_GROUP_HUSBAND;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_HOBBY_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.hobbyhub.testutil.TypicalIndexes.INDEX_FIRST_MEMBER;
import static seedu.hobbyhub.testutil.TypicalIndexes.INDEX_SECOND_MEMBER;
import static seedu.hobbyhub.testutil.TypicalIndexes.INDEX_THIRD_MEMBER;

import java.time.DayOfWeek;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.logic.commands.EditCommand;
import seedu.hobbyhub.logic.commands.EditCommand.EditMemberDescriptor;
import seedu.hobbyhub.model.group.Group;
import seedu.hobbyhub.model.member.Address;
import seedu.hobbyhub.model.member.Availability;
import seedu.hobbyhub.model.member.Email;
import seedu.hobbyhub.model.member.Hobby;
import seedu.hobbyhub.model.member.Name;
import seedu.hobbyhub.model.member.Phone;
import seedu.hobbyhub.testutil.EditMemberDescriptorBuilder;

public class EditCommandParserTest {

    private static final String GROUP_EMPTY = " " + PREFIX_GROUP;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, VALID_NAME_AMY, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "1", EditCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1" + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS); // invalid phone
        assertParseFailure(parser, "1" + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS); // invalid email
        assertParseFailure(parser, "1" + INVALID_ADDRESS_DESC, Address.MESSAGE_CONSTRAINTS); // invalid address
        assertParseFailure(parser, "1" + INVALID_GROUP_DESC, Group.MESSAGE_CONSTRAINTS); // invalid group
        assertParseFailure(parser, "1" + INVALID_HOBBY_DESC, Hobby.MESSAGE_CONSTRAINTS); // invalid hobby
        assertParseFailure(parser, "1 -d Funday", Availability.MESSAGE_CONSTRAINTS);

        // invalid phone followed by valid email
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC + EMAIL_DESC_AMY, Phone.MESSAGE_CONSTRAINTS);

        // while parsing {@code PREFIX_GROUP} alone will reset the groups of the {@code Member} being edited,
        // parsing it together with a valid group results in error
        assertParseFailure(parser, "1" + GROUP_DESC_FRIEND + GROUP_DESC_HUSBAND + GROUP_EMPTY,
                Group.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + GROUP_DESC_FRIEND + GROUP_EMPTY + GROUP_DESC_HUSBAND,
                Group.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + GROUP_EMPTY + GROUP_DESC_FRIEND + GROUP_DESC_HUSBAND,
                Group.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, "1" + INVALID_NAME_DESC + INVALID_EMAIL_DESC + VALID_ADDRESS_AMY + VALID_PHONE_AMY,
                Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_MEMBER;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + GROUP_DESC_HUSBAND
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + NAME_DESC_AMY + GROUP_DESC_FRIEND + HOBBY_DESC_AMY;

        EditMemberDescriptor descriptor = new EditMemberDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_AMY).withAddress(VALID_ADDRESS_AMY)
                .withGroups(VALID_GROUP_HUSBAND, VALID_GROUP_FRIEND).withHobby(VALID_HOBBY_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);

        // availability
        userInput = targetIndex.getOneBased() + " -d Monday -d Friday";
        descriptor = new EditMemberDescriptorBuilder()
                .withAvailability(DayOfWeek.MONDAY, DayOfWeek.FRIDAY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_MEMBER;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + EMAIL_DESC_AMY;

        EditMemberDescriptor descriptor = new EditMemberDescriptorBuilder().withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = INDEX_THIRD_MEMBER;
        String userInput = targetIndex.getOneBased() + NAME_DESC_AMY;
        EditMemberDescriptor descriptor = new EditMemberDescriptorBuilder().withName(VALID_NAME_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = targetIndex.getOneBased() + PHONE_DESC_AMY;
        descriptor = new EditMemberDescriptorBuilder().withPhone(VALID_PHONE_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // email
        userInput = targetIndex.getOneBased() + EMAIL_DESC_AMY;
        descriptor = new EditMemberDescriptorBuilder().withEmail(VALID_EMAIL_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // address
        userInput = targetIndex.getOneBased() + ADDRESS_DESC_AMY;
        descriptor = new EditMemberDescriptorBuilder().withAddress(VALID_ADDRESS_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // groups
        userInput = targetIndex.getOneBased() + GROUP_DESC_FRIEND;
        descriptor = new EditMemberDescriptorBuilder().withGroups(VALID_GROUP_FRIEND).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // hobby
        userInput = targetIndex.getOneBased() + HOBBY_DESC_AMY;
        descriptor = new EditMemberDescriptorBuilder().withHobby(VALID_HOBBY_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        // More extensive testing of duplicate parameter detections is done in
        // AddCommandParserTest#parse_repeatedNonGroupValue_failure()

        // valid followed by invalid
        Index targetIndex = INDEX_FIRST_MEMBER;
        String userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + PHONE_DESC_BOB;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid followed by valid
        userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + INVALID_PHONE_DESC;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // multiple valid fields repeated
        userInput = targetIndex.getOneBased() + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY
                + GROUP_DESC_FRIEND + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY + GROUP_DESC_FRIEND
                + PHONE_DESC_BOB + ADDRESS_DESC_BOB + EMAIL_DESC_BOB + GROUP_DESC_HUSBAND;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));

        // multiple invalid values
        userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC
                + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));
    }

    @Test
    public void parse_resetGroups_success() {
        Index targetIndex = INDEX_THIRD_MEMBER;
        String userInput = targetIndex.getOneBased() + GROUP_EMPTY;

        EditMemberDescriptor descriptor = new EditMemberDescriptorBuilder().withGroups().build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_resetAvailability_success() {
        EditMemberDescriptor descriptor = new EditMemberDescriptorBuilder().withAvailability().build();
        assertParseSuccess(parser, "1 -d", new EditCommand(INDEX_FIRST_MEMBER, descriptor));
    }
}
