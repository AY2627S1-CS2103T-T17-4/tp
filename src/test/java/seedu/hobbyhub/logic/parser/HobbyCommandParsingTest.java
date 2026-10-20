package seedu.hobbyhub.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.hobbyhub.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.hobbyhub.testutil.Assert.assertThrows;
import static seedu.hobbyhub.testutil.TypicalIndexes.INDEX_FIRST_MEMBER;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.logic.commands.AddCommand;
import seedu.hobbyhub.logic.commands.EditCommand;
import seedu.hobbyhub.logic.commands.EditCommand.EditMemberDescriptor;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.model.member.Hobby;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.member.SkillLevel;
import seedu.hobbyhub.testutil.MemberBuilder;
import seedu.hobbyhub.testutil.MemberUtil;

public class HobbyCommandParsingTest {
    private static final String REQUIRED_FIELDS =
            " -n Amy Bee -p 85355255 -c amy@gmail.com -a 123, Jurong West Ave 6, #08-111";
    private final Hobby chess = new Hobby("Chess", SkillLevel.ADVANCED);
    private final Hobby tennis = new Hobby("Table Tennis", SkillLevel.INTERMEDIATE);

    @Test
    public void parseHobby_acceptsQuotedNamesWhitespaceAndCaseInsensitiveSkill() throws Exception {
        assertEquals(tennis, ParserUtil.parseHobby(" \" Table Tennis : iNtErMeDiAtE \" "));
        assertEquals(chess, ParserUtil.parseHobby("Chess:Advanced"));
        assertEquals(new Hobby("Chess"), ParserUtil.parseHobby("Chess"));
        assertEquals(new Hobby("Chess"), ParserUtil.parseHobby("\"Chess\""));
        assertEquals(new Hobby("Chess"), ParserUtil.parseHobby("Chess:Beginner"));
    }

    @Test
    public void parseHobby_rejectsMissingPartsInvalidSkillsAndUnbalancedQuotes() {
        for (String input : List.of("Chess:", "Chess:Expert", "Chess:   ")) {
            assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_SKILL, () -> ParserUtil.parseHobby(input));
        }
        for (String input : List.of(":Beginner", "", "\"\"", "@Chess:Beginner")) {
            assertThrows(ParseException.class, Hobby.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseHobby(input));
        }
        for (String input : List.of("Chess:Advanced:Extra", "\"Chess:Advanced", "Chess:Advanced\"",
            "\"Chess\":\"Advanced\"", "\"Chess:Advanced\" extra")) {
            assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_HOBBY_FORMAT, ()
                -> ParserUtil.parseHobby(input));
        }
    }

    @Test
    public void parseHobbies_rejectsDuplicateNamesAndEmptyLists() {
        assertThrows(ParseException.class, Member.MESSAGE_HOBBIES_REQUIRED, () -> ParserUtil.parseHobbies(List.of()));
        assertThrows(ParseException.class, Member.MESSAGE_DUPLICATE_HOBBY, ()
            -> ParserUtil.parseHobbies(List.of("Chess:Beginner", " chess :Advanced")));
    }

    @Test
    public void add_acceptsRepeatedHobbiesAndPreservesInputOrder() {
        Member member = new MemberBuilder().withHobbies(chess, tennis).build();
        assertParseSuccess(new AddCommandParser(), REQUIRED_FIELDS
                + " -h Chess:Advanced -h \"Table Tennis:Intermediate\"", new AddCommand(member));
        assertParseSuccess(new AddCommandParser(), REQUIRED_FIELDS
                + " -h Chess:Advanced -h \"Table Tennis:Intermediate\" -t friends",
                new AddCommand(new MemberBuilder(member).withTags("friends").build()));
    }

    @Test
    public void add_rejectsDuplicateNamesAndInvalidEarlierHobbies() {
        assertParseFailure(new AddCommandParser(), REQUIRED_FIELDS + " -h Chess:Beginner -h chess:Advanced",
                Member.MESSAGE_DUPLICATE_HOBBY);
        assertParseFailure(new AddCommandParser(), REQUIRED_FIELDS + " -h Chess:Expert -h Tennis:Beginner",
                ParserUtil.MESSAGE_INVALID_SKILL);
    }

    @Test
    public void edit_acceptsAllHobbiesRatherThanOnlyTheLast() {
        EditMemberDescriptor descriptor = new EditMemberDescriptor().setHobbies(List.of(chess, tennis));
        assertParseSuccess(new EditCommandParser(), "1 -h Chess:Advanced -h \"Table Tennis:Intermediate\"",
                new EditCommand(INDEX_FIRST_MEMBER, descriptor));
        assertParseFailure(new EditCommandParser(), "1 -h Chess:Advanced -h chess:Beginner",
                Member.MESSAGE_DUPLICATE_HOBBY);
        assertParseFailure(new EditCommandParser(), "1 -h Chess:Expert -h Tennis:Beginner",
                ParserUtil.MESSAGE_INVALID_SKILL);
        assertParseFailure(new EditCommandParser(), "1 -h", Hobby.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void quotedHobby_doesNotInterpretOptionLikeTextAsAnotherField() {
        Hobby hobby = new Hobby("Chess -club", SkillLevel.ADVANCED);
        assertParseSuccess(new AddCommandParser(), REQUIRED_FIELDS + " -h \"Chess -club:Advanced\"",
                new AddCommand(new MemberBuilder().withHobbies(hobby).build()));
    }

    @Test
    public void generatedCommands_preserveAllHobbiesAndSkills() throws Exception {
        Member member = new MemberBuilder().withHobbies(chess, tennis).build();
        assertEquals(new AddCommand(member), new HobbyHubParser().parseCommand(MemberUtil.getAddCommand(member)));
        EditMemberDescriptor descriptor = new EditMemberDescriptor().setHobbies(member.getHobbies());
        assertEquals(new EditCommand(INDEX_FIRST_MEMBER, descriptor), new HobbyHubParser().parseCommand(
                "edit 1 " + MemberUtil.getEditMemberDescriptorDetails(descriptor)));
    }

    @Test
    public void descriptor_copiesCompleteImmutableList() {
        List<Hobby> hobbies = new ArrayList<>(List.of(chess, tennis));
        EditMemberDescriptor original = new EditMemberDescriptor().setHobbies(hobbies);
        EditMemberDescriptor copy = new EditMemberDescriptor(original);
        hobbies.clear();
        assertTrue(copy.isAnyFieldEdited());
        assertEquals(original, copy);
        assertEquals(List.of(chess, tennis), copy.getHobbies().orElseThrow());
        assertThrows(UnsupportedOperationException.class, () -> copy.getHobbies().orElseThrow().clear());
        original.setHobby(chess);
        assertFalse(original.equals(copy));
        assertThrows(IllegalArgumentException.class, () -> original.setHobbies(List.of()));
        assertThrows(IllegalArgumentException.class, ()
            -> original.setHobbies(List.of(chess, new Hobby("chess"))));
        original.setHobbies(null);
        assertFalse(original.isAnyFieldEdited());
    }
}
