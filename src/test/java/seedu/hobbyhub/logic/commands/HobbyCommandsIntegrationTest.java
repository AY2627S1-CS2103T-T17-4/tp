package seedu.hobbyhub.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.hobbyhub.logic.parser.HobbyHubParser;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.model.HobbyHub;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.ModelManager;
import seedu.hobbyhub.model.UserPrefs;
import seedu.hobbyhub.model.member.Hobby;
import seedu.hobbyhub.model.member.SkillLevel;
import seedu.hobbyhub.storage.JsonHobbyHubStorage;
import seedu.hobbyhub.testutil.TypicalSessions;

public class HobbyCommandsIntegrationTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void addAndEditHobbies_preserveSessionsAndPersistSkills() throws Exception {
        Model model = new ModelManager(TypicalSessions.getTypicalHobbyHubWithSessions(), new UserPrefs());
        var originalSessions = List.copyOf(model.getHobbyHub().getSessionList());
        HobbyHubParser parser = new HobbyHubParser();
        parser.parseCommand("add -n New Member -p 91234567 -c new@example.com -a Main Street"
                + " -h \"Table Tennis:Intermediate\" -h Chess:Advanced").execute(model);
        var added = model.getHobbyHub().getMemberList().getLast();
        assertEquals(List.of(new Hobby("Table Tennis", SkillLevel.INTERMEDIATE),
                new Hobby("Chess", SkillLevel.ADVANCED)), added.getHobbies());

        parser.parseCommand("edit 1 -h Chess:Advanced -h Cycling:Intermediate").execute(model);
        var edited = model.getHobbyHub().getMemberList().getFirst();
        assertEquals(List.of(new Hobby("Chess", SkillLevel.ADVANCED),
                new Hobby("Cycling", SkillLevel.INTERMEDIATE)), edited.getHobbies());
        parser.parseCommand("edit 1 -p 91234567").execute(model);
        assertEquals(edited.getHobbies(), model.getHobbyHub().getMemberList().getFirst().getHobbies());
        assertEquals(originalSessions, model.getHobbyHub().getSessionList());

        var storage = new JsonHobbyHubStorage(temporaryFolder.resolve("hobbies.json"));
        storage.saveHobbyHub(model.getHobbyHub());
        assertEquals(model.getHobbyHub(), storage.readHobbyHub().orElseThrow());
    }

    @Test
    public void invalidLaterHobby_doesNotPartiallyChangeMemberOrSessions() {
        Model model = new ModelManager(TypicalSessions.getTypicalHobbyHubWithSessions(), new UserPrefs());
        HobbyHub before = new HobbyHub(model.getHobbyHub());
        assertThrows(ParseException.class, () -> new HobbyHubParser()
                .parseCommand("edit 1 -h Chess:Advanced -h Tennis:Expert").execute(model));
        assertEquals(before, model.getHobbyHub());
    }
}
