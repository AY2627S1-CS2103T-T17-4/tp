package seedu.hobbyhub.logic.commands;

import static seedu.hobbyhub.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.hobbyhub.testutil.TypicalMembers.getTypicalHobbyHub;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.model.HobbyHub;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.ModelManager;
import seedu.hobbyhub.model.UserPrefs;

public class ClearCommandTest {

    @Test
    public void execute_emptyHobbyHub_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nonEmptyHobbyHub_success() {
        Model model = new ModelManager(getTypicalHobbyHub(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalHobbyHub(), new UserPrefs());
        expectedModel.setHobbyHub(new HobbyHub());

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

}
