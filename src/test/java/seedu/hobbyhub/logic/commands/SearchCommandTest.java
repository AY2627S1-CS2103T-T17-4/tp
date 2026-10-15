package seedu.hobbyhub.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.logic.Messages.MESSAGE_MEMBERS_LISTED_OVERVIEW;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.hobbyhub.testutil.TypicalMembers.ALICE;
import static seedu.hobbyhub.testutil.TypicalMembers.CARL;
import static seedu.hobbyhub.testutil.TypicalMembers.ELLE;
import static seedu.hobbyhub.testutil.TypicalMembers.FIONA;
import static seedu.hobbyhub.testutil.TypicalMembers.getTypicalHobbyHub;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.ModelManager;
import seedu.hobbyhub.model.UserPrefs;
import seedu.hobbyhub.model.member.HobbyContainsKeywordsPredicate;
import seedu.hobbyhub.model.member.NameContainsKeywordsPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code SearchCommand}.
 */
public class SearchCommandTest {
    private Model model = new ModelManager(getTypicalHobbyHub(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalHobbyHub(), new UserPrefs());

    @Test
    public void equals() {
        NameContainsKeywordsPredicate firstPredicate =
                new NameContainsKeywordsPredicate(List.of("first"));
        NameContainsKeywordsPredicate secondPredicate =
                new NameContainsKeywordsPredicate(List.of("second"));

        SearchCommand searchFirstCommand = new SearchCommand(firstPredicate);
        SearchCommand searchSecondCommand = new SearchCommand(secondPredicate);

        // same object -> returns true
        assertTrue(searchFirstCommand.equals(searchFirstCommand));

        // same values -> returns true
        SearchCommand searchFirstCommandCopy = new SearchCommand(firstPredicate);
        assertTrue(searchFirstCommand.equals(searchFirstCommandCopy));

        // different types -> returns false
        assertFalse(searchFirstCommand.equals(1));

        // null -> returns false
        assertFalse(searchFirstCommand.equals(null));

        // different member -> returns false
        assertFalse(searchFirstCommand.equals(searchSecondCommand));
    }

    @Test
    public void execute_zeroKeywords_noMemberFound() {
        String expectedMessage = String.format(MESSAGE_MEMBERS_LISTED_OVERVIEW, 0);
        NameContainsKeywordsPredicate predicate = preparePredicate(" ");
        SearchCommand command = new SearchCommand(predicate);
        expectedModel.updateFilteredMemberList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(), model.getFilteredMemberList());
    }

    @Test
    public void execute_multipleKeywords_multipleMembersFound() {
        String expectedMessage = String.format(MESSAGE_MEMBERS_LISTED_OVERVIEW, 3);
        NameContainsKeywordsPredicate predicate = preparePredicate("Kurz Elle Kunz");
        SearchCommand command = new SearchCommand(predicate);
        expectedModel.updateFilteredMemberList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(CARL, ELLE, FIONA), model.getFilteredMemberList());
    }

    @Test
    public void execute_hobbyPredicate_memberFound() {
        String expectedMessage = String.format(MESSAGE_MEMBERS_LISTED_OVERVIEW, 1);
        HobbyContainsKeywordsPredicate predicate = new HobbyContainsKeywordsPredicate(List.of("chess"));
        SearchCommand command = new SearchCommand(predicate);
        expectedModel.updateFilteredMemberList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(ALICE), model.getFilteredMemberList());
    }

    @Test
    public void toStringMethod() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("keyword"));
        SearchCommand searchCommand = new SearchCommand(predicate);
        String expected = SearchCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, searchCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameContainsKeywordsPredicate preparePredicate(String userInput) {
        return new NameContainsKeywordsPredicate(List.of(userInput.split("\\s+")));
    }
}
