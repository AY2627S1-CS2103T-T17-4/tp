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
 * Tests combined searches across parsing, model filtering and other commands.
 */
public class CombinedSearchIntegrationTest {
    private final HobbyHubParser parser = new HobbyHubParser();
    private final Session morning = new SessionBuilder().withName("Morning Chess")
            .withDateTime("2026-10-24T00:00").withLocation("Community Hall")
            .withMembers("alice@example.com").build();
    private final Session evening = new SessionBuilder().withName("Evening Tennis")
            .withDateTime("2026-10-24T23:59").withLocation("Sports Court").withCompleted(true)
            .withMembers("alice@example.com", "bob@example.com").build();
    private final Session nextDay = new SessionBuilder().withName("Sunday Chess")
            .withDateTime("2026-10-25T00:00").withLocation("Community Hall").withMembers().build();
    private final Member alice = new MemberBuilder().withName("Alice Lee").withEmail("alice@example.com")
            .withGroups("Weekend Club")
            .withHobbies(new Hobby("Chess", SkillLevel.BEGINNER), new Hobby("Tennis", SkillLevel.ADVANCED)).build()
            .withSessions(List.of(morning, evening));
    private final Member bob = new MemberBuilder().withName("Bob Tan").withEmail("bob@example.com")
            .withGroups("Chess").withHobbies(new Hobby("Chess", SkillLevel.INTERMEDIATE)).build()
            .withSessions(List.of(evening));
    private final Member carol = new MemberBuilder().withName("Carol Lee").withEmail("carol@example.com")
            .withGroups().withHobby("Painting").build();

    @Test
    public void searchMembers_combinedCriteria_requireEveryCondition() throws Exception {
        Model model = createModel();
        HobbyHub before = new HobbyHub(model.getHobbyHub());
        Map<String, List<Member>> searches = Map.of(
                "search -n ALICE -g Weekend", List.of(alice),
                "search -h Chess -g Weekend", List.of(alice),
                "search -h Chess -sl Intermediate", List.of(bob),
                "search -h Chess -sl Advanced", List.of(),
                "search -h Tennis -sl Advanced", List.of(alice),
                "search -g Weekend -sl Advanced", List.of(alice),
                "search -n Alice -c bob@example.com", List.of(),
                "search -g Chess -h Tennis", List.of(),
                "search -n ali -h TEN -g \"Weekend Club\" -c ALICE@example.com -sl ADVANCED", List.of(alice));
        for (var entry : searches.entrySet()) {
            assertMembers(model, entry.getKey(), entry.getValue());
            assertEquals(before, model.getHobbyHub());
            assertEquals(ListState.MEMBERS, model.getListState());
            assertEquals(List.of(morning, evening, nextDay), model.getFilteredSessionList());
        }
    }

    @Test
    public void searchSessions_combinedCriteria_requireEveryCondition() throws Exception {
        Model model = createModel();
        assertMembers(model, "search -n Alice -g Weekend", List.of(alice));
        execute(model, "list -s");
        HobbyHub before = new HobbyHub(model.getHobbyHub());
        Map<String, List<Session>> searches = Map.of(
                "search -n CHESS -l hall", List.of(morning, nextDay),
                "search -l Hall -t 2026-10-24", List.of(morning),
                "search -t 2026-10-24 -done false", List.of(morning),
                "search -t 2026-10-24 -c ALICE@example.com", List.of(morning, evening),
                "search -done false -c alice@example.com", List.of(morning),
                "search -n Chess -c bob@example.com", List.of(),
                "search -n Tennis -c bob@example.com", List.of(evening),
                "search -done false -t 2026-10-25", List.of(nextDay),
                "search -n Tennis -l Court -t 2026-10-24 -done true -c bob@example.com", List.of(evening),
                "search -n Chess -l Hall -t 2026-10-24 -done FALSE -c alice@example.com", List.of(morning));
        for (var entry : searches.entrySet()) {
            assertSessions(model, entry.getKey(), entry.getValue());
            assertEquals(before, model.getHobbyHub());
            assertEquals(ListState.SESSIONS, model.getListState());
            assertEquals(List.of(alice), model.getFilteredMemberList());
        }
    }

    @Test
    public void search_flagOrderDoesNotChangeResults() throws Exception {
        Model model = createModel();
        assertMembers(model, "search -sl Advanced -c alice@example.com -g Weekend -h Tennis -n Alice", List.of(alice));
        assertMembers(model, "search -n Alice -h Tennis -g Weekend -c alice@example.com -sl Advanced", List.of(alice));
        execute(model, "list -s");
        assertSessions(model, "search -c bob@example.com -done true -t 2026-10-24 -l Court -n Tennis",
                List.of(evening));
        assertSessions(model, "search -n Tennis -l Court -t 2026-10-24 -done true -c bob@example.com",
                List.of(evening));
    }

    @Test
    public void search_replacesPreviousFilterAndListRestoresAll() throws Exception {
        Model model = createModel();
        assertMembers(model, "search -n Alice -g Weekend", List.of(alice));
        assertMembers(model, "search -n Bob -g Chess", List.of(bob));
        assertMembers(model, "search -n Alice -sl Intermediate", List.of());
        assertMembers(model, "search -h Chess -sl Beginner", List.of(alice));
        execute(model, "list");
        assertEquals(List.of(alice, bob, carol), model.getFilteredMemberList());
        execute(model, "list -s");
        assertSessions(model, "search -n Chess -done false", List.of(morning, nextDay));
        assertSessions(model, "search -n Tennis -done true", List.of(evening));
        assertSessions(model, "search -n missing -l Hall", List.of());
        assertSessions(model, "search -n Chess -t 2026-10-25", List.of(nextDay));
        execute(model, "list");
        assertEquals(List.of(morning, evening, nextDay), model.getFilteredSessionList());
    }

    @Test
    public void search_invalidCriteria_preserveFiltersAndData() throws Exception {
        Model model = createModel();
        assertMembers(model, "search -n Alice -g Weekend", List.of(alice));
        HobbyHub before = new HobbyHub(model.getHobbyHub());
        for (String command : List.of("search -n Bob -sl Expert", "search -n Bob -c invalid",
                "search -n Bob -g", "search -g Chess -g Weekend", "search -h Chess -sl Advanced -x unknown",
                "search -n Bob -done true", "search -n Bob -p 91234567", "search -n Bob -a Street")) {
            assertThrows(ParseException.class, () -> execute(model, command));
            assertEquals(List.of(alice), model.getFilteredMemberList());
            assertEquals(ListState.MEMBERS, model.getListState());
            assertEquals(before, model.getHobbyHub());
        }
        execute(model, "list -s");
        assertSessions(model, "search -n Tennis -done true", List.of(evening));
        for (String command : List.of("search -n Chess -t 2026-02-29", "search -n Chess -done yes",
                "search -n Chess -c invalid", "search -l Hall -l Court", "search -n Chess -h Chess")) {
            assertThrows(ParseException.class, () -> execute(model, command));
            assertEquals(List.of(evening), model.getFilteredSessionList());
            assertEquals(ListState.SESSIONS, model.getListState());
            assertEquals(before, model.getHobbyHub());
        }
    }

    @Test
    public void search_combinedMemberResults_canBeAssignedByDisplayedIndex() throws Exception {
        Model model = createModel();
        assertMembers(model, "search -n Bob -sl Intermediate", List.of(bob));
        execute(model, "assign 1 Morning Chess");
        Session updated = new SessionBuilder(morning).withMembers("alice@example.com", "bob@example.com").build();
        execute(model, "list -s");
        assertSessions(model, "search -n Morning -c bob@example.com", List.of(updated));
    }

    @Test
    public void search_combinedSessionResults_canBeEditedByDisplayedIndex() throws Exception {
        Model model = createModel();
        execute(model, "list -s");
        assertSessions(model, "search -done true -c bob@example.com", List.of(evening));
        execute(model, "edit 1 -l New Court -t 2026-11-01T15:00");
        Session updated = new SessionBuilder(evening).withLocation("New Court")
                .withDateTime("2026-11-01T15:00").build();
        assertSessions(model, "search -n Tennis -t 2026-10-24", List.of());
        assertSessions(model, "search -l New Court -t 2026-11-01 -c bob@example.com", List.of(updated));
        assertEquals(List.of(updated), model.getSessionsOf(bob));
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

    private void assertMembers(Model model, String command, List<Member> expected) throws Exception {
        CommandResult result = execute(model, command);
        assertEquals(expected, model.getFilteredMemberList(), command);
        assertEquals(String.format(Messages.MESSAGE_MEMBERS_LISTED_OVERVIEW, expected.size()),
                result.getFeedbackToUser());
    }

    private void assertSessions(Model model, String command, List<Session> expected) throws Exception {
        CommandResult result = execute(model, command);
        assertEquals(expected, model.getFilteredSessionList(), command);
        assertEquals(String.format(Messages.MESSAGE_SESSIONS_LISTED_OVERVIEW, expected.size()),
                result.getFeedbackToUser());
    }

    private CommandResult execute(Model model, String input) throws Exception {
        return parser.parseCommand(input, model.getListState()).execute(model);
    }
}
