package seedu.hobbyhub.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.hobbyhub.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.hobbyhub.testutil.Assert.assertThrows;
import static seedu.hobbyhub.testutil.TypicalIndexes.INDEX_FIRST_MEMBER;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.logic.commands.AddCommand;
import seedu.hobbyhub.logic.commands.ClearCommand;
import seedu.hobbyhub.logic.commands.DeleteCommand;
import seedu.hobbyhub.logic.commands.EditCommand;
import seedu.hobbyhub.logic.commands.EditCommand.EditMemberDescriptor;
import seedu.hobbyhub.logic.commands.ExitCommand;
import seedu.hobbyhub.logic.commands.HelpCommand;
import seedu.hobbyhub.logic.commands.ListCommand;
import seedu.hobbyhub.logic.commands.RemoveCommand;
import seedu.hobbyhub.logic.commands.SearchCommand;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.model.member.Email;
import seedu.hobbyhub.model.member.HobbyContainsKeywordsPredicate;
import seedu.hobbyhub.model.member.NameContainsKeywordsPredicate;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.testutil.EditMemberDescriptorBuilder;
import seedu.hobbyhub.testutil.MemberBuilder;
import seedu.hobbyhub.testutil.MemberUtil;

public class HobbyHubParserTest {

    private final HobbyHubParser parser = new HobbyHubParser();

    @Test
    public void parseCommand_add() throws Exception {
        Member member = new MemberBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(MemberUtil.getAddCommand(member));
        assertEquals(new AddCommand(member), command);
    }

    @Test
    public void parseCommand_remove() throws Exception {
        assertEquals(new RemoveCommand(new Email("john@email.com")),
                parser.parseCommand("remove -c john@email.com"));
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_MEMBER.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_MEMBER), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Member member = new MemberBuilder().build();
        EditMemberDescriptor descriptor = new EditMemberDescriptorBuilder(member).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_MEMBER.getOneBased() + " " + MemberUtil.getEditMemberDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_MEMBER, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_search() throws Exception {
        SearchCommand nameSearch = (SearchCommand) parser.parseCommand(
                SearchCommand.COMMAND_WORD + " -n foo");
        assertEquals(new SearchCommand(new NameContainsKeywordsPredicate(List.of("foo"))), nameSearch);

        SearchCommand hobbySearch = (SearchCommand) parser.parseCommand(
                SearchCommand.COMMAND_WORD + " -h chess");
        assertEquals(new SearchCommand(new HobbyContainsKeywordsPredicate(List.of("chess"))), hobbySearch);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }

    @Test
    public void parseCommand_oldFindCommandWord_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("find Alice"));
    }
}
