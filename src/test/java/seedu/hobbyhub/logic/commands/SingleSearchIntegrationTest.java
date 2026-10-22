package seedu.hobbyhub.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.logic.parser.HobbyHubParser;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.model.HobbyHub;
import seedu.hobbyhub.model.ListState;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.ModelManager;
import seedu.hobbyhub.model.member.Hobby;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.member.SkillLevel;
import seedu.hobbyhub.model.session.Session;
import seedu.hobbyhub.testutil.MemberBuilder;
import seedu.hobbyhub.testutil.SessionBuilder;

/**
 * Tests search parsing, predicates, list routing and execution together against real model data.
 */
public class SingleSearchIntegrationTest {
    private final HobbyHubParser parser = new HobbyHubParser();
    private final Session morning = new SessionBuilder().withName("Morning Chess")
            .withDateTime("2026-10-24T00:00").withLocation("Community Center")
            .withMembers("alice@example.com").build();
    private final Session evening = new SessionBuilder().withName("Evening Tennis")
            .withDateTime("2026-10-24T23:59").withLocation("Sports Hall").withCompleted(true)
            .withMembers("alice@example.com", "bob@example.com").build();
    private final Session nextDay = new SessionBuilder().withName("Sunday Chess")
            .withDateTime("2026-10-25T00:00").withLocation("Community Center").withMembers().build();

    // Expected members include the session assignments populated by the model.
    private final Member alice = new MemberBuilder().withName("Alice Lee").withEmail("alice@example.com")
            .withGroups("Weekend Club")
            .withHobbies(new Hobby("Chess", SkillLevel.BEGINNER), new Hobby("Tennis", SkillLevel.ADVANCED)).build()
            .withSessions(List.of(morning, evening));
    private final Member bob = new MemberBuilder().withName("Bob Tan").withEmail("bob@example.com")
            .withGroups("Chess").withHobbies(new Hobby("Chess", SkillLevel.INTERMEDIATE)).build()
            .withSessions(List.of(evening));
    private final Member carol = new MemberBuilder().withName("Carol Lee").withEmail("carol@example.com")
            .withGroups().withHobbies(new Hobby("Painting", SkillLevel.BEGINNER)).build();

    @Test
    public void searchMembers_eachField_returnsMatchingMembersAndCount() throws Exception {
        Model model = createModel();
        Map<String, List<Member>> searches = Map.of(
                "search -n LEE", List.of(alice, carol),
                "search -h CHES", List.of(alice, bob),
                "search -h tennis", List.of(alice),
                "search -g WEEKEND", List.of(alice),
                "search -g chess", List.of(bob),
                "search -c ALICE@EXAMPLE.COM", List.of(alice),
                "search -sl advanced", List.of(alice),
                "search -sl Intermediate", List.of(bob),
                "search -sl Beginner", List.of(alice, carol));
        HobbyHub before = new HobbyHub(model.getHobbyHub());
        for (var entry : searches.entrySet()) {
            CommandResult result = execute(model, entry.getKey());
            assertEquals(entry.getValue(), model.getFilteredMemberList(), entry.getKey());
            assertEquals(String.format(Messages.MESSAGE_MEMBERS_LISTED_OVERVIEW, entry.getValue().size()),
                    result.getFeedbackToUser());
            assertEquals(ListState.MEMBERS, model.getListState());
            assertEquals(before, model.getHobbyHub());
            assertEquals(List.of(morning, evening, nextDay), model.getFilteredSessionList());
        }
    }

    @Test
    public void searchSessions_eachField_returnsMatchingSessionsAndCount() throws Exception {
        Model model = createModel();
        execute(model, "list -s");
        Map<String, List<Session>> searches = Map.of(
                "search -n CHES", List.of(morning, nextDay),
                "search -l COMMUNITY", List.of(morning, nextDay),
                "search -t 2026-10-24", List.of(morning, evening),
                "search -t 2026-10-25", List.of(nextDay),
                "search -done TRUE", List.of(evening),
                "search -done false", List.of(morning, nextDay),
                "search -c ALICE@EXAMPLE.COM", List.of(morning, evening),
                "search -c bob@example.com", List.of(evening),
                "search -c nobody@example.com", List.of());
        HobbyHub before = new HobbyHub(model.getHobbyHub());
        for (var entry : searches.entrySet()) {
            CommandResult result = execute(model, entry.getKey());
            assertEquals(entry.getValue(), model.getFilteredSessionList(), entry.getKey());
            assertEquals(String.format(Messages.MESSAGE_SESSIONS_LISTED_OVERVIEW, entry.getValue().size()),
                    result.getFeedbackToUser());
            assertEquals(ListState.SESSIONS, model.getListState());
            assertEquals(before, model.getHobbyHub());
            assertEquals(List.of(alice, bob, carol), model.getFilteredMemberList());
        }
    }

    @Test
    public void search_replacesPreviousFilterAndListRestoresResults() throws Exception {
        Model model = createModel();
        execute(model, "search -n nobody");
        assertEquals(List.of(), model.getFilteredMemberList());
        execute(model, "search -n Bob");
        assertEquals(List.of(bob), model.getFilteredMemberList());
        execute(model, "list");
        assertEquals(List.of(alice, bob, carol), model.getFilteredMemberList());
        execute(model, "list -s");
        execute(model, "search -n nobody");
        assertEquals(List.of(), model.getFilteredSessionList());
        execute(model, "search -n Tennis");
        assertEquals(List.of(evening), model.getFilteredSessionList());
        execute(model, "list");
        assertEquals(List.of(morning, evening, nextDay), model.getFilteredSessionList());
        assertEquals(ListState.SESSIONS, model.getListState());
    }

    @Test
    public void search_nameAndEmailUseActiveList() throws Exception {
        Model model = createModel();
        execute(model, "search -n Chess");
        assertEquals(List.of(), model.getFilteredMemberList());
        execute(model, "search -c alice@example.com");
        assertEquals(List.of(alice), model.getFilteredMemberList());
        execute(model, "list -s");
        execute(model, "search -n Chess");
        assertEquals(List.of(morning, nextDay), model.getFilteredSessionList());
        execute(model, "search -c alice@example.com");
        assertEquals(List.of(morning, evening), model.getFilteredSessionList());
        execute(model, "list -m");
        execute(model, "search -n Alice");
        assertEquals(List.of(alice), model.getFilteredMemberList());
    }

    @Test
    public void search_invalidCriteria_preserveDataAndExistingFilters() throws Exception {
        Model model = createModel();
        execute(model, "search -n Alice");
        HobbyHub before = new HobbyHub(model.getHobbyHub());
        for (String command : List.of("search -n Bob -n Alice", "search -sl Expert", "search -c alice",
                "search -l Community", "search -p 91234567", "search -a Street")) {
            assertThrows(ParseException.class, () -> execute(model, command));
            assertEquals(List.of(alice), model.getFilteredMemberList());
            assertEquals(before, model.getHobbyHub());
        }
        execute(model, "list -s");
        execute(model, "search -n Tennis");
        for (String command : List.of("search -done false -done true", "search -t 2026-02-29",
                "search -done yes", "search -g Chess", "search -c alice")) {
            assertThrows(ParseException.class, () -> execute(model, command));
            assertEquals(List.of(evening), model.getFilteredSessionList());
            assertEquals(before, model.getHobbyHub());
        }
    }

    @Test
    public void search_emptyData_returnsZeroResults() throws Exception {
        Model model = new ModelManager();
        assertEquals(String.format(Messages.MESSAGE_MEMBERS_LISTED_OVERVIEW, 0),
                execute(model, "search -g Club").getFeedbackToUser());
        execute(model, "list -s");
        assertEquals(String.format(Messages.MESSAGE_SESSIONS_LISTED_OVERVIEW, 0),
                execute(model, "search -done false").getFeedbackToUser());
    }

    @Test
    public void search_emailMatchesCompleteAddressOnly() throws Exception {
        Model model = createModel();
        execute(model, "search -c lice@example.com");
        assertEquals(List.of(), model.getFilteredMemberList());
        execute(model, "list -s");
        execute(model, "search -c lice@example.com");
        assertEquals(List.of(), model.getFilteredSessionList());
    }

    private Model createModel() {
        Model model = new ModelManager();
        model.addMember(alice);
        model.addMember(bob);
        model.addMember(carol);
        model.addSession(morning);
        model.addSession(evening);
        model.addSession(nextDay);
        return model;
    }

    private CommandResult execute(Model model, String input) throws Exception {
        return parser.parseCommand(input, model.getListState()).execute(model);
    }
}
