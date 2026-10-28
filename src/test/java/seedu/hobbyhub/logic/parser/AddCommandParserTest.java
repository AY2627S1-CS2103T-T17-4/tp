package seedu.hobbyhub.logic.parser;

import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.GROUP_DESC_FRIEND;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.GROUP_DESC_HUSBAND;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.HOBBY_DESC_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.HOBBY_DESC_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.INVALID_GROUP_DESC;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.INVALID_HOBBY_DESC;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_GROUP_FRIEND;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_GROUP_HUSBAND;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_HOBBY_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.hobbyhub.testutil.TypicalMembers.AMY;
import static seedu.hobbyhub.testutil.TypicalMembers.BOB;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.logic.commands.AddCommand;
import seedu.hobbyhub.model.group.Group;
import seedu.hobbyhub.model.member.Address;
import seedu.hobbyhub.model.member.Email;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.member.Name;
import seedu.hobbyhub.model.member.Phone;
import seedu.hobbyhub.testutil.MemberBuilder;

public class AddCommandParserTest {
    private AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_literalDashPrefixes_success() {
        Member expectedMember = new MemberBuilder().withName("John Doe").withPhone("98765432")
                .withEmail("johnd@example.com").withAddress("John Street").withHobby("Chess")
                .withGroups("friends", "colleagues").build();

        assertParseSuccess(parser, " -n John Doe -p 98765432 -c johnd@example.com -a John Street"
                + " -h Chess -g friends -g colleagues", new AddCommand(expectedMember));
        assertParseSuccess(parser, " -nJohn Doe -p98765432 -cjohnd@example.com -aJohn Street"
                + " -hChess -gfriends -gcolleagues", new AddCommand(expectedMember));
    }

    @Test
    public void parse_legacySlashPrefixes_failure() {
        assertParseFailure(parser, " n/John Doe p/98765432 e/johnd@example.com a/John Street h/Chess",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_incorrectEmailPrefix_failure() {
        assertParseFailure(parser, " -n John Doe -p 98765432 -e johnd@example.com -a John Street -h Chess",
                "Unknown add option. Use only -n, -p, -c, -a, -h, or -g.");
    }

    @Test
    public void parse_allFieldsPresent_success() {
        Member expectedMember = new MemberBuilder(BOB).withGroups(VALID_GROUP_FRIEND).build();

        // whitespace only preamble
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + HOBBY_DESC_BOB + GROUP_DESC_FRIEND, new AddCommand(expectedMember));


        // multiple groups - all accepted
        Member expectedMemberMultipleGroups = new MemberBuilder(BOB).withGroups(VALID_GROUP_FRIEND, VALID_GROUP_HUSBAND)
                .build();
        assertParseSuccess(parser,
                NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB + HOBBY_DESC_BOB
                        + GROUP_DESC_HUSBAND + GROUP_DESC_FRIEND,
                new AddCommand(expectedMemberMultipleGroups));
    }

    @Test
    public void parse_repeatedNonGroupValue_failure() {
        String validExpectedMemberString = NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + HOBBY_DESC_BOB + GROUP_DESC_FRIEND;

        // multiple names
        assertParseFailure(parser, NAME_DESC_AMY + validExpectedMemberString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // multiple phones
        assertParseFailure(parser, PHONE_DESC_AMY + validExpectedMemberString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // multiple emails
        assertParseFailure(parser, EMAIL_DESC_AMY + validExpectedMemberString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // multiple addresses
        assertParseFailure(parser, ADDRESS_DESC_AMY + validExpectedMemberString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // multiple fields repeated
        assertParseFailure(parser,
                validExpectedMemberString + PHONE_DESC_AMY + EMAIL_DESC_AMY + NAME_DESC_AMY + ADDRESS_DESC_AMY
                        + HOBBY_DESC_AMY + validExpectedMemberString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_ADDRESS, PREFIX_EMAIL, PREFIX_PHONE));

        // invalid value followed by valid value

        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + validExpectedMemberString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid email
        assertParseFailure(parser, INVALID_EMAIL_DESC + validExpectedMemberString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // invalid phone
        assertParseFailure(parser, INVALID_PHONE_DESC + validExpectedMemberString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid address
        assertParseFailure(parser, INVALID_ADDRESS_DESC + validExpectedMemberString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // invalid hobby is rejected even when followed by a valid hobby
        assertParseFailure(parser, INVALID_HOBBY_DESC + validExpectedMemberString,
                "Each -h option needs a hobby value. Use -h \"HOBBY:SKILL\".");

        // valid value followed by invalid value

        // invalid name
        assertParseFailure(parser, validExpectedMemberString + INVALID_NAME_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid email
        assertParseFailure(parser, validExpectedMemberString + INVALID_EMAIL_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // invalid phone
        assertParseFailure(parser, validExpectedMemberString + INVALID_PHONE_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid address
        assertParseFailure(parser, validExpectedMemberString + INVALID_ADDRESS_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // invalid hobby is rejected even when preceded by a valid hobby
        assertParseFailure(parser, validExpectedMemberString + INVALID_HOBBY_DESC,
                "Each -h option needs a hobby value. Use -h \"HOBBY:SKILL\".");
    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        // zero groups
        Member expectedMember = new MemberBuilder(AMY).withGroups().build();
        assertParseSuccess(parser, NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY
                        + HOBBY_DESC_AMY,
                new AddCommand(expectedMember));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

        // missing name prefix
        assertParseFailure(parser, VALID_NAME_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + HOBBY_DESC_BOB,
                expectedMessage);

        // missing email prefix
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + VALID_EMAIL_BOB + ADDRESS_DESC_BOB
                + HOBBY_DESC_BOB,
                expectedMessage);

        // missing hobby prefix
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                        + VALID_HOBBY_BOB,
                expectedMessage);

        // all prefixes missing
        assertParseFailure(parser, VALID_NAME_BOB + VALID_PHONE_BOB + VALID_EMAIL_BOB + VALID_ADDRESS_BOB
                        + VALID_HOBBY_BOB,
                expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + HOBBY_DESC_BOB + GROUP_DESC_HUSBAND + GROUP_DESC_FRIEND, Name.MESSAGE_CONSTRAINTS);

        // invalid phone
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_PHONE_DESC + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + HOBBY_DESC_BOB + GROUP_DESC_HUSBAND + GROUP_DESC_FRIEND, Phone.MESSAGE_CONSTRAINTS);

        // invalid email
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + INVALID_EMAIL_DESC + ADDRESS_DESC_BOB
                + HOBBY_DESC_BOB + GROUP_DESC_HUSBAND + GROUP_DESC_FRIEND, Email.MESSAGE_CONSTRAINTS);

        // invalid address
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + INVALID_ADDRESS_DESC
                + HOBBY_DESC_BOB + GROUP_DESC_HUSBAND + GROUP_DESC_FRIEND, Address.MESSAGE_CONSTRAINTS);

        // invalid hobby
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + INVALID_HOBBY_DESC + GROUP_DESC_HUSBAND + GROUP_DESC_FRIEND,
                "Each -h option needs a hobby value. Use -h \"HOBBY:SKILL\".");

        // invalid group
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + HOBBY_DESC_BOB + INVALID_GROUP_DESC + VALID_GROUP_FRIEND, Group.MESSAGE_CONSTRAINTS);

        // two invalid values, only first invalid value reported
        assertParseFailure(parser, INVALID_NAME_DESC + PHONE_DESC_BOB + EMAIL_DESC_BOB + INVALID_ADDRESS_DESC
                        + HOBBY_DESC_BOB,
                Name.MESSAGE_CONSTRAINTS);

        // non-empty preamble
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + HOBBY_DESC_BOB + GROUP_DESC_HUSBAND + GROUP_DESC_FRIEND,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_emptyHobbyValue_failure() {
        assertParseFailure(parser, NAME_DESC_BOB + EMAIL_DESC_BOB + " -h",
                "Each -h option needs a hobby value. Use -h \"HOBBY:SKILL\".");
    }

    @Test
    public void parse_unsupportedOption_failure() {
        String validArguments = NAME_DESC_BOB + EMAIL_DESC_BOB + HOBBY_DESC_BOB;
        String expectedMessage = "Unknown add option. Use only -n, -p, -c, -a, -h, or -g.";

        assertParseFailure(parser, validArguments + " -x", expectedMessage);
        assertParseFailure(parser, validArguments + " -hh", expectedMessage);
        assertParseFailure(parser, validArguments + " -unknown value", expectedMessage);
        assertParseFailure(parser, "-nBob Choo -cbob@example.com -hChess",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_unpairedQuoteInAddress_success() {
        Member expectedMember = new MemberBuilder().withName("Bob Choo").withPhone("")
                .withEmail("bob@example.com").withAddress("Block 5 \"Sunrise").withHobby("Chess").withGroups().build();
        assertParseSuccess(parser, NAME_DESC_BOB + EMAIL_DESC_BOB + " -a Block 5 \"Sunrise -h Chess",
                new AddCommand(expectedMember));
    }

    @Test
    public void parse_standaloneHyphenInValue_success() {
        Member expectedMember = new MemberBuilder().withName("Bob Choo").withPhone("")
                .withEmail("bob@example.com").withAddress("Block 5 - Unit 3").withHobby("Chess").withGroups().build();
        assertParseSuccess(parser, NAME_DESC_BOB + EMAIL_DESC_BOB + " -a Block 5 - Unit 3 -h Chess",
                new AddCommand(expectedMember));
    }

    @Test
    public void parse_optionLikeTextInsideQuotedValue_success() {
        Member expectedMember = new MemberBuilder().withName("Bob Choo").withPhone("")
                .withEmail("bob@example.com").withAddress("").withHobby("Chess -x").withGroups().build();
        assertParseSuccess(parser, NAME_DESC_BOB + EMAIL_DESC_BOB + " -h \"Chess -x\"",
                new AddCommand(expectedMember));
    }

    @Test
    public void parse_hyphenatedNameAndCompactOption_success() {
        Member expectedMember = new MemberBuilder().withName("Anne-Marie").withPhone("")
                .withEmail("anne@example.com").withAddress("").withHobby("Chess").withGroups().build();
        assertParseSuccess(parser, " -n Anne-Marie -c anne@example.com -h Chess", new AddCommand(expectedMember));

        Member singleLetterName = new MemberBuilder().withName("N").withPhone("")
                .withEmail("n@example.com").withAddress("").withHobby("Chess").withGroups().build();
        assertParseSuccess(parser, " -nN -cn@example.com -hChess", new AddCommand(singleLetterName));
    }
}
