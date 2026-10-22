package seedu.hobbyhub.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.showMemberAtIndex;
import static seedu.hobbyhub.testutil.TypicalIndexes.INDEX_FIRST_MEMBER;
import static seedu.hobbyhub.testutil.TypicalIndexes.INDEX_SECOND_MEMBER;
import static seedu.hobbyhub.testutil.TypicalMembers.ALICE;
import static seedu.hobbyhub.testutil.TypicalMembers.CARL;
import static seedu.hobbyhub.testutil.TypicalMembers.getTypicalHobbyHub;
import static seedu.hobbyhub.testutil.TypicalSessions.CHESS_MEETUP;
import static seedu.hobbyhub.testutil.TypicalSessions.CYCLING_TRIP;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.ModelManager;
import seedu.hobbyhub.model.UserPrefs;
import seedu.hobbyhub.model.member.Member;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code ViewCommand}.
 */
public class ViewCommandTest {

    private Model model = new ModelManager(getTypicalHobbyHub(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Member member = model.getFilteredMemberList().get(INDEX_FIRST_MEMBER.getZeroBased());
        ViewCommand viewCommand = new ViewCommand(INDEX_FIRST_MEMBER);

        Model expectedModel = new ModelManager(model.getHobbyHub(), new UserPrefs());

        assertCommandSuccess(viewCommand, model, ViewCommand.formatMember(member), expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredMemberList().size() + 1);
        ViewCommand viewCommand = new ViewCommand(outOfBoundIndex);

        assertCommandFailure(viewCommand, model, Messages.MESSAGE_INVALID_MEMBER_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showMemberAtIndex(model, INDEX_SECOND_MEMBER);
        Member member = model.getFilteredMemberList().get(INDEX_FIRST_MEMBER.getZeroBased());
        ViewCommand viewCommand = new ViewCommand(INDEX_FIRST_MEMBER);

        Model expectedModel = new ModelManager(model.getHobbyHub(), new UserPrefs());
        showMemberAtIndex(expectedModel, INDEX_SECOND_MEMBER);

        assertCommandSuccess(viewCommand, model, ViewCommand.formatMember(member), expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showMemberAtIndex(model, INDEX_FIRST_MEMBER);

        Index outOfBoundIndex = INDEX_SECOND_MEMBER;
        // ensures that outOfBoundIndex is still in bounds of HobbyHub list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getHobbyHub().getMemberList().size());

        assertCommandFailure(new ViewCommand(outOfBoundIndex), model,
                Messages.MESSAGE_INVALID_MEMBER_DISPLAYED_INDEX);
    }

    @Test
    public void formatMember_noGroupsNoSessions_showsNone() {
        // precondition: CARL has no groups and no sessions
        assertTrue(CARL.getGroups().isEmpty());
        assertTrue(CARL.getSessions().isEmpty());

        String details = ViewCommand.formatMember(CARL);
        assertTrue(details.contains("Groups: none"));
        assertTrue(details.contains("Sessions: none"));
    }

    @Test
    public void formatMember_withGroupsAndSessions_listsThem() {
        Member member = ALICE.withSessions(List.of(CHESS_MEETUP, CYCLING_TRIP));

        String details = ViewCommand.formatMember(member);
        assertTrue(details.contains("Groups: [friends]"));
        assertTrue(details.contains(CHESS_MEETUP.getName() + " (" + CHESS_MEETUP.getDate()
                    + " @ " + CHESS_MEETUP.getLocation() + ")"));
        // multiple sessions joined with "; "
        assertTrue(details.contains("; " + CYCLING_TRIP.getName()));
    }

    @Test
    public void equals() {
        ViewCommand viewFirstCommand = new ViewCommand(INDEX_FIRST_MEMBER);
        ViewCommand viewSecondCommand = new ViewCommand(INDEX_SECOND_MEMBER);

        // same object -> returns true
        assertTrue(viewFirstCommand.equals(viewFirstCommand));

        // same values -> returns true
        assertTrue(viewFirstCommand.equals(new ViewCommand(INDEX_FIRST_MEMBER)));

        // different types -> returns false
        assertFalse(viewFirstCommand.equals(1));

        // null -> returns false
        assertFalse(viewFirstCommand.equals(null));

        // different index -> returns false
        assertFalse(viewFirstCommand.equals(viewSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        ViewCommand viewCommand = new ViewCommand(targetIndex);
        String expected = ViewCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, viewCommand.toString());
    }
}
