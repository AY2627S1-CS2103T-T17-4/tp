package seedu.hobbyhub.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.hobbyhub.logic.Messages.MESSAGE_SESSION_COMMAND_UNAVAILABLE;
import static seedu.hobbyhub.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.hobbyhub.model.ListState.MEMBERS;
import static seedu.hobbyhub.model.ListState.SESSIONS;
import static seedu.hobbyhub.testutil.Assert.assertThrows;
import static seedu.hobbyhub.testutil.TypicalIndexes.INDEX_FIRST_MEMBER;
import static seedu.hobbyhub.testutil.TypicalIndexes.INDEX_SECOND_MEMBER;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.logic.commands.AddCommand;
import seedu.hobbyhub.logic.commands.AssignCommand;
import seedu.hobbyhub.logic.commands.ClearCommand;
import seedu.hobbyhub.logic.commands.DeleteCommand;
import seedu.hobbyhub.logic.commands.EditCommand;
import seedu.hobbyhub.logic.commands.EditCommand.EditMemberDescriptor;
import seedu.hobbyhub.logic.commands.ExitCommand;
import seedu.hobbyhub.logic.commands.HelpCommand;
import seedu.hobbyhub.logic.commands.ListCommand;
import seedu.hobbyhub.logic.commands.SearchCommand;
import seedu.hobbyhub.logic.commands.UnassignCommand;
import seedu.hobbyhub.logic.commands.session.AddSessionCommand;
import seedu.hobbyhub.logic.commands.session.DeleteSessionCommand;
import seedu.hobbyhub.logic.commands.session.EditSessionCommand;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.model.ListState;
import seedu.hobbyhub.model.member.HobbyContainsKeywordsPredicate;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.member.NameContainsKeywordsPredicate;
import seedu.hobbyhub.model.session.SessionName;
import seedu.hobbyhub.testutil.EditMemberDescriptorBuilder;
import seedu.hobbyhub.testutil.MemberBuilder;
import seedu.hobbyhub.testutil.MemberUtil;

public class HobbyHubParserTest {

    private final HobbyHubParser parser = new HobbyHubParser();

    @Test
    public void parseCommand_add() throws Exception {
        Member member = new MemberBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(MemberUtil.getAddCommand(member), MEMBERS);
        assertEquals(new AddCommand(member), command);
    }

    @Test
    public void parseCommand_remove_throwsUnknownCommand() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, ()
            -> parser.parseCommand("remove -c john@email.com", MEMBERS));
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD, MEMBERS) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3", MEMBERS) instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_MEMBER.getOneBased(), MEMBERS);
        assertEquals(new DeleteCommand(INDEX_FIRST_MEMBER), command);
    }

    @Test
    public void parseCommand_assignInMemberList() throws Exception {
        AssignCommand command = (AssignCommand) parser.parseCommand(
                AssignCommand.COMMAND_WORD + " 1 Weekend Chess Meetup", MEMBERS);

        assertEquals(new AssignCommand(INDEX_FIRST_MEMBER, new SessionName("Weekend Chess Meetup")), command);
    }

    @Test
    public void parseCommand_assignInSessionList_throwsParseException() {
        String userInput = AssignCommand.COMMAND_WORD + " 1 Weekend Chess Meetup";
        assertThrows(ParseException.class, AssignCommand.MESSAGE_MEMBER_LIST_REQUIRED, () -> parser.parseCommand(
                userInput, SESSIONS));
    }

    @Test
    public void parseCommand_unassignInMemberList() throws Exception {
        UnassignCommand command = (UnassignCommand) parser.parseCommand(
                UnassignCommand.COMMAND_WORD + " 2 Weekend Chess Meetup", MEMBERS);

        assertEquals(new UnassignCommand(INDEX_SECOND_MEMBER,
                new SessionName("Weekend Chess Meetup")), command);
    }

    @Test
    public void parseCommand_unassignInSessionList_throwsParseException() {
        String userInput = UnassignCommand.COMMAND_WORD + " 2 Weekend Chess Meetup";
        assertThrows(ParseException.class, UnassignCommand.MESSAGE_MEMBER_LIST_REQUIRED, () -> parser.parseCommand(
                userInput, SESSIONS));
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Member member = new MemberBuilder().build();
        EditMemberDescriptor descriptor = new EditMemberDescriptorBuilder(member).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_MEMBER.getOneBased() + " " + MemberUtil.getEditMemberDescriptorDetails(descriptor),
                MEMBERS);
        assertEquals(new EditCommand(INDEX_FIRST_MEMBER, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD, MEMBERS) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3", MEMBERS) instanceof ExitCommand);
    }

    @Test
    public void parseCommand_search() throws Exception {
        SearchCommand nameSearch = (SearchCommand) parser.parseCommand(
                SearchCommand.COMMAND_WORD + " -n foo", MEMBERS);
        assertEquals(new SearchCommand(new NameContainsKeywordsPredicate(List.of("foo"))), nameSearch);

        SearchCommand hobbySearch = (SearchCommand) parser.parseCommand(
                SearchCommand.COMMAND_WORD + " -h chess", MEMBERS);
        assertEquals(new SearchCommand(new HobbyContainsKeywordsPredicate(List.of("chess"))), hobbySearch);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD, MEMBERS) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3", MEMBERS) instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertEquals(new ListCommand(), parser.parseCommand(ListCommand.COMMAND_WORD, MEMBERS));
        assertEquals(new ListCommand(ListState.MEMBERS),
                parser.parseCommand(ListCommand.COMMAND_WORD + " -m", SESSIONS));
        assertEquals(new ListCommand(ListState.SESSIONS),
                parser.parseCommand(ListCommand.COMMAND_WORD + " -s", MEMBERS));
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(ListCommand.COMMAND_WORD + " 3", MEMBERS));
    }

    @Test
    public void parseCommand_listCommandsInSessionList_useSessionParsers() throws Exception {
        assertTrue(parser.parseCommand(AddCommand.COMMAND_WORD
                + " -n Golf Meet -l Golf Club -t 2026-10-24T14:00", SESSIONS) instanceof AddSessionCommand);
        assertTrue(parser.parseCommand(EditCommand.COMMAND_WORD + " 1 -l Golf Club", SESSIONS)
                instanceof EditSessionCommand);
        assertEquals(new DeleteSessionCommand(INDEX_FIRST_MEMBER),
                parser.parseCommand(DeleteCommand.COMMAND_WORD + " 1", SESSIONS));
        assertThrows(ParseException.class, String.format(MESSAGE_SESSION_COMMAND_UNAVAILABLE, "search"), ()
            -> parser.parseCommand(SearchCommand.COMMAND_WORD + " -n golf", SESSIONS));
    }

    @Test
    public void parseCommand_otherCommandsInSessionList_sameAsInMemberList() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD, SESSIONS) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD, SESSIONS) instanceof ExitCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD, SESSIONS) instanceof HelpCommand);
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, ()
            -> parser.parseCommand("unknownCommand", SESSIONS));
    }

    @Test
    public void parseCommand_nullListState_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parseCommand(ListCommand.COMMAND_WORD, null));
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand("", MEMBERS));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, ()
            -> parser.parseCommand("unknownCommand", MEMBERS));
    }

    @Test
    public void parseCommand_oldFindCommandWord_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("find Alice", MEMBERS));
    }
}
