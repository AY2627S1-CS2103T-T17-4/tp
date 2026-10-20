package seedu.hobbyhub.testutil;

import seedu.hobbyhub.model.HobbyHub;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.session.Session;

/**
 * A utility class to help with building HobbyHub objects.
 * Example usage: <br>
 *     {@code HobbyHub ab = new HobbyHubBuilder().withMember("John", "Doe").build();}
 */
public class HobbyHubBuilder {

    private HobbyHub hobbyHub;

    public HobbyHubBuilder() {
        hobbyHub = new HobbyHub();
    }

    public HobbyHubBuilder(HobbyHub hobbyHub) {
        this.hobbyHub = hobbyHub;
    }

    /**
     * Adds a new {@code Member} to the {@code HobbyHub} that we are building.
     */
    public HobbyHubBuilder withMember(Member member) {
        hobbyHub.addMember(member);
        return this;
    }

    /**
     * Adds a new {@code Session} to the {@code HobbyHub} that we are building.
     */
    public HobbyHubBuilder withSession(Session session) {
        hobbyHub.addSession(session);
        return this;
    }

    public HobbyHub build() {
        return hobbyHub;
    }
}
