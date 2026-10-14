package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Email;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class RemoveCommandTest {
    private final Model model = new ModelManager();
    private final Person member = new PersonBuilder().withEmail("john@email.com").build();

    @Test
    public void execute_matchingContact_removesOnlyMatchingMember() {
        model.addPerson(member);
        Person other = new PersonBuilder().withName("Jane Doe").withEmail("jane@email.com").build();
        model.addPerson(other);
        Model expected = new ModelManager(model.getAddressBook(), new UserPrefs());
        expected.deletePerson(member);
        assertCommandSuccess(new RemoveCommand(member.getEmail()), model,
                String.format(RemoveCommand.MESSAGE_SUCCESS, member.getName(), member.getEmail()), expected);
    }

    @Test
    public void execute_caseInsensitiveContactHiddenByFilter_success() {
        model.addPerson(member);
        model.updateFilteredPersonList(person -> false);
        Model expected = new ModelManager();
        expected.updateFilteredPersonList(person -> false);
        assertCommandSuccess(new RemoveCommand(new Email("JOHN@EMAIL.COM")), model,
                String.format(RemoveCommand.MESSAGE_SUCCESS, member.getName(), member.getEmail()), expected);
    }

    @Test
    public void execute_unknownContact_failsWithoutChangingData() {
        model.addPerson(member);
        Email email = new Email("unknown@email.com");
        assertCommandFailure(new RemoveCommand(email), model, String.format(RemoveCommand.MESSAGE_NOT_FOUND, email));
    }

    @Test
    public void execute_emptyAddressBook_fails() {
        assertCommandFailure(new RemoveCommand(member.getEmail()), model,
                String.format(RemoveCommand.MESSAGE_NOT_FOUND, member.getEmail()));
    }

    @Test
    public void execute_duplicateContact_failsWithoutRemovingAnyone() {
        model.addPerson(member);
        model.addPerson(new PersonBuilder().withName("Jane Doe").withEmail("JOHN@EMAIL.COM").build());
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
