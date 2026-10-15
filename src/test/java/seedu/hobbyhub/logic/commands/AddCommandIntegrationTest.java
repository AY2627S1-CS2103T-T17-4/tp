package seedu.hobbyhub.logic.commands;

import static seedu.hobbyhub.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.hobbyhub.testutil.TypicalMembers.getTypicalHobbyHub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.ModelManager;
import seedu.hobbyhub.model.UserPrefs;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.testutil.MemberBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalHobbyHub(), new UserPrefs());
    }

    @Test
    public void execute_newMember_success() {
        Member validMember = new MemberBuilder().build();

        Model expectedModel = new ModelManager(model.getHobbyHub(), new UserPrefs());
        expectedModel.addMember(validMember);

        assertCommandSuccess(new AddCommand(validMember), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validMember)),
                expectedModel);
    }

    @Test
    public void execute_duplicateMember_throwsCommandException() {
        Member memberInList = model.getHobbyHub().getMemberList().get(0);
        assertCommandFailure(new AddCommand(memberInList), model,
                AddCommand.MESSAGE_DUPLICATE_MEMBER);
    }

}
