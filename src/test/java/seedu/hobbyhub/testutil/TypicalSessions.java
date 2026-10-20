package seedu.hobbyhub.testutil;

import static seedu.hobbyhub.testutil.TypicalMembers.ALICE;
import static seedu.hobbyhub.testutil.TypicalMembers.BENSON;
import static seedu.hobbyhub.testutil.TypicalMembers.CARL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.hobbyhub.model.HobbyHub;
import seedu.hobbyhub.model.session.Session;

/**
 * A utility class containing a list of {@code Session} objects to be used in tests.
 * The sessions only refer to members in {@link TypicalMembers#getTypicalMembers()}.
 */
public class TypicalSessions {

    public static final Session CHESS_MEETUP = new SessionBuilder().withName("Weekend Chess Meetup")
            .withDate("2026-10-24").withMembers(ALICE.getEmail().value, BENSON.getEmail().value).build();
    public static final Session CYCLING_TRIP = new SessionBuilder().withName("Sunday Cycling Trip")
            .withDate("2026-10-25").withMembers(BENSON.getEmail().value, CARL.getEmail().value).build();

    // Not in the typical HobbyHub
    public static final Session BOARD_GAME_NIGHT = new SessionBuilder().withName("Board Game Night")
            .withDate("2026-10-30").withMembers(ALICE.getEmail().value).build();

    private TypicalSessions() {} // prevents instantiation

    /**
     * Returns a {@code HobbyHub} with all the typical members and typical sessions.
     */
    public static HobbyHub getTypicalHobbyHubWithSessions() {
        HobbyHub hobbyHub = TypicalMembers.getTypicalHobbyHub();
        for (Session session : getTypicalSessions()) {
            hobbyHub.addSession(session);
        }
        return hobbyHub;
    }

    public static List<Session> getTypicalSessions() {
        return new ArrayList<>(Arrays.asList(CHESS_MEETUP, CYCLING_TRIP));
    }
}
