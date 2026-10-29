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
import static seedu.hobbyhub.testutil.TypicalSessions.COMPLETED_CHESS_MEETUP;
import static seedu.hobbyhub.testutil.TypicalSessions.CYCLING_TRIP;

import java.time.DayOfWeek;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.ModelManager;
import seedu.hobbyhub.model.UserPrefs;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.testutil.MemberBuilder;

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

        CommandResult expectedResult = new CommandResult(ViewCommand.formatMember(member),
                ViewCommand.createViewDetails(member));
        assertCommandSuccess(viewCommand, model, expectedResult, expectedModel);
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

        CommandResult expectedResult = new CommandResult(ViewCommand.formatMember(member),
                ViewCommand.createViewDetails(member));
        assertCommandSuccess(viewCommand, model, expectedResult, expectedModel);
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
        assertTrue(details.contains("Availability: None"));
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
    public void createViewDetails_memberWithNoGroupsOrSessions_showsNone() {
        ViewDetails details = ViewCommand.createViewDetails(CARL);

        assertEquals(ViewDetails.RecordType.MEMBER, details.recordType());
        assertEquals(CARL.getEmail().value, details.recordId());
        assertEquals("None", details.fields().get(3).value());
        assertEquals("None", details.fields().get(4).value());
        assertEquals("None", details.fields().get(5).value());
    }

    @Test
    public void createViewDetails_memberWithGroupsAndSessions_listsThem() {
        Member member = ALICE.withSessions(List.of(CHESS_MEETUP));

        ViewDetails details = ViewCommand.createViewDetails(member);

        assertTrue(details.fields().get(3).value().contains("friends"));
        assertTrue(details.fields().get(5).value().contains(CHESS_MEETUP.getName().toString()));
    }

    @Test
    public void createViewDetails_memberWithAvailability_listsWeekdays() {
        Member member = new MemberBuilder().withAvailability(DayOfWeek.MONDAY, DayOfWeek.THURSDAY).build();

        ViewDetails details = ViewCommand.createViewDetails(member);

        assertEquals("Monday, Thursday", details.fields().get(4).value());
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

    @Test
    public void formatMember_withDoneAndNotDoneSessions_appendsDoneCorrectly() {
        // Assuming CHESS_MEETUP is done and CYCLING_TRIP is not done
        // Adjust the method name/setup based on how your Session class handles 'done' status
        Member member = ALICE.withSessions(List.of(COMPLETED_CHESS_MEETUP, CYCLING_TRIP));

        String details = ViewCommand.formatMember(member);

        // Done session should have " (Done)" appended
        assertTrue(details.contains(COMPLETED_CHESS_MEETUP.getName() + " (" + COMPLETED_CHESS_MEETUP.getDate()
                + " @ " + COMPLETED_CHESS_MEETUP.getLocation() + ") -- Done"));

        // Pending session should NOT have " (Done)" appended
        assertTrue(details.contains(CYCLING_TRIP.getName() + " (" + CYCLING_TRIP.getDate()
                + " @ " + CYCLING_TRIP.getLocation() + ")"));
        assertFalse(details.contains(CYCLING_TRIP.getName() + " (" + CYCLING_TRIP.getDate()
                + " @ " + CYCLING_TRIP.getLocation() + ") -- Done"));
    }
}
