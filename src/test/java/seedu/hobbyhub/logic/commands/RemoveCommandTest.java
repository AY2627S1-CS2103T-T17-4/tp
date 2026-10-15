package seedu.hobbyhub.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.ModelManager;
import seedu.hobbyhub.model.UserPrefs;
import seedu.hobbyhub.model.member.Email;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.testutil.MemberBuilder;

public class RemoveCommandTest {
    private final Model model = new ModelManager();
    private final Member member = new MemberBuilder().withEmail("john@email.com").build();

    @Test
    public void execute_matchingContact_removesOnlyMatchingMember() {
        model.addMember(member);
        Member other = new MemberBuilder().withName("Jane Doe").withEmail("jane@email.com").build();
        model.addMember(other);
        Model expected = new ModelManager(model.getHobbyHub(), new UserPrefs());
        expected.deleteMember(member);
        assertCommandSuccess(new RemoveCommand(member.getEmail()), model,
                String.format(RemoveCommand.MESSAGE_SUCCESS, member.getName(), member.getEmail()), expected);
    }

    @Test
    public void execute_caseInsensitiveContactHiddenByFilter_success() {
        model.addMember(member);
        model.updateFilteredMemberList(member -> false);
        Model expected = new ModelManager();
        expected.updateFilteredMemberList(member -> false);
        assertCommandSuccess(new RemoveCommand(new Email("JOHN@EMAIL.COM")), model,
                String.format(RemoveCommand.MESSAGE_SUCCESS, member.getName(), member.getEmail()), expected);
    }

    @Test
    public void execute_unknownContact_failsWithoutChangingData() {
        model.addMember(member);
        Email email = new Email("unknown@email.com");
        assertCommandFailure(new RemoveCommand(email), model, String.format(RemoveCommand.MESSAGE_NOT_FOUND, email));
    }

    @Test
    public void execute_emptyHobbyHub_fails() {
        assertCommandFailure(new RemoveCommand(member.getEmail()), model,
                String.format(RemoveCommand.MESSAGE_NOT_FOUND, member.getEmail()));
    }

    @Test
    public void execute_duplicateContact_failsWithoutRemovingAnyone() {
        model.addMember(member);
        model.addMember(new MemberBuilder().withName("Jane Doe").withEmail("JOHN@EMAIL.COM").build());
        assertCommandFailure(new RemoveCommand(member.getEmail()), model,
                String.format(RemoveCommand.MESSAGE_AMBIGUOUS, member.getEmail()));
    }

    @Test
    public void constructor_nullEmail_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RemoveCommand(null));
    }

    @Test
    public void equals() {
        RemoveCommand command = new RemoveCommand(member.getEmail());
        assertTrue(command.equals(command));
        assertEquals(command, new RemoveCommand(member.getEmail()));
        assertFalse(command.equals(new RemoveCommand(new Email("jane@email.com"))));
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
    }

    @Test
    public void toStringMethod() {
        RemoveCommand command = new RemoveCommand(member.getEmail());
        assertEquals(RemoveCommand.class.getCanonicalName() + "{email=" + member.getEmail() + "}", command.toString());
    }
}
