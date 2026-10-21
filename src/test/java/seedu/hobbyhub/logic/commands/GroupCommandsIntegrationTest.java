package seedu.hobbyhub.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.logic.parser.HobbyHubParser;
import seedu.hobbyhub.logic.parser.ParserUtil;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.model.HobbyHub;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.ModelManager;
import seedu.hobbyhub.model.UserPrefs;
import seedu.hobbyhub.model.group.Group;
import seedu.hobbyhub.model.member.Hobby;
import seedu.hobbyhub.model.member.SkillLevel;
import seedu.hobbyhub.testutil.TypicalSessions;

public class GroupCommandsIntegrationTest {
    private final HobbyHubParser parser = new HobbyHubParser();

    @Test
    public void add_optionalGroupsAreIndependentOfHobbies() throws Exception {
        Model model = new ModelManager();
        parser.parseCommand("add -n Alice -p 91234567 -c alice@example.com -a Main Street"
                + " -h Chess:Advanced -g \"Weekend Club\" -g Volunteers -g Volunteers").execute(model);
        var alice = model.getHobbyHub().getMemberList().getFirst();
        assertEquals(Set.of(new Group("Weekend Club"), new Group("Volunteers")), alice.getGroups());
        assertEquals(List.of(new Hobby("Chess", SkillLevel.ADVANCED)), alice.getHobbies());
        assertTrue(Messages.format(alice).contains("Groups:"));
        parser.parseCommand("add -n Bob -p 92345678 -c bob@example.com -a Other Street -h Chess").execute(model);
        assertTrue(model.getHobbyHub().getMemberList().getLast().getGroups().isEmpty());
    }

    @Test
    public void edit_replacesClearsOrPreservesGroupsWithoutChangingHobbiesOrSessions() throws Exception {
        Model model = new ModelManager(TypicalSessions.getTypicalHobbyHubWithSessions(), new UserPrefs());
        var sessions = List.copyOf(model.getHobbyHub().getSessionList());
        parser.parseCommand("edit 1 -h Chess:Advanced -h Tennis:Intermediate").execute(model);
        var hobbies = model.getHobbyHub().getMemberList().getFirst().getHobbies();
        parser.parseCommand("edit 1 -g \"Weekend Club\" -g Volunteers").execute(model);
        assertEquals(Set.of(new Group("Weekend Club"), new Group("Volunteers")),
                model.getHobbyHub().getMemberList().getFirst().getGroups());
        parser.parseCommand("edit 1 -p 91234567").execute(model);
        assertEquals(2, model.getHobbyHub().getMemberList().getFirst().getGroups().size());
        parser.parseCommand("edit 1 -g").execute(model);
        assertTrue(model.getHobbyHub().getMemberList().getFirst().getGroups().isEmpty());
        assertEquals(hobbies, model.getHobbyHub().getMemberList().getFirst().getHobbies());
        assertEquals(sessions, model.getHobbyHub().getSessionList());
    }

    @Test
    public void invalidGroupsOrLegacyOption_doNotChangeData() {
        Model model = new ModelManager(TypicalSessions.getTypicalHobbyHubWithSessions(), new UserPrefs());
        HobbyHub before = new HobbyHub(model.getHobbyHub());
        for (String command : List.of("edit 1 -t friends", "edit 1 -g Club -g", "edit 1 -g @Club",
            "edit 1 -g \"Weekend Club", "add -n New -p 91234567 -c new@example.com -a Street -h Chess -t friends")) {
            assertThrows(ParseException.class, () -> parser.parseCommand(command).execute(model));
            assertEquals(before, model.getHobbyHub());
        }
    }

    @Test
    public void parseGroup_acceptsQuotesAndRejectsMalformedValues() throws Exception {
        assertEquals(new Group("Weekend Club"), ParserUtil.parseGroup(" \" Weekend Club \" "));
        for (String value : List.of("", "\"\"", "\"Weekend Club", "Club\"", "Club:Advanced")) {
            assertThrows(ParseException.class, () -> ParserUtil.parseGroup(value));
        }
    }
}
