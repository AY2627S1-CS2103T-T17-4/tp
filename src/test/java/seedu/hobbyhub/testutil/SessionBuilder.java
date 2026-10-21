package seedu.hobbyhub.testutil;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import seedu.hobbyhub.model.member.Email;
import seedu.hobbyhub.model.session.Session;
import seedu.hobbyhub.model.session.SessionName;
import seedu.hobbyhub.model.util.SampleDataUtil;

/**
 * A utility class to help with building Session objects.
 */
public class SessionBuilder {

    public static final String DEFAULT_NAME = "Weekend Chess Meetup";
    public static final LocalDateTime DEFAULT_DATE_TIME = LocalDateTime.of(2026, 10, 24, 14, 0);
    public static final String DEFAULT_LOCATION = "Community Center";

    private SessionName name;
    private LocalDateTime dateTime;
    private String location;
    private boolean completed;
    private Set<Email> members;

    /**
     * Creates a {@code SessionBuilder} with the default details and no members.
     */
    public SessionBuilder() {
        name = new SessionName(DEFAULT_NAME);
        dateTime = DEFAULT_DATE_TIME;
        location = DEFAULT_LOCATION;
        completed = false;
        members = new HashSet<>();
    }

    /**
     * Initializes the SessionBuilder with the data of {@code sessionToCopy}.
     */
    public SessionBuilder(Session sessionToCopy) {
        name = sessionToCopy.getName();
        dateTime = sessionToCopy.getDate();
        location = sessionToCopy.getLocation();
        completed = sessionToCopy.isCompleted();
        members = new HashSet<>(sessionToCopy.getMembers());
    }

    /**
     * Sets the {@code SessionName} of the {@code Session} that we are building.
     */
    public SessionBuilder withName(String name) {
        this.name = new SessionName(name);
        return this;
    }

    /**
     * Sets the date of the {@code Session} that we are building.
     */
    public SessionBuilder withDateTime(String dateTime) {
        this.dateTime = LocalDateTime.parse(dateTime);
        return this;
    }

    /** Sets the location of the {@code Session} that we are building. */
    public SessionBuilder withLocation(String location) {
        this.location = location;
        return this;
    }

    /** Sets whether the {@code Session} that we are building is completed. */
    public SessionBuilder withCompleted(boolean completed) {
        this.completed = completed;
        return this;
    }

    /**
     * Sets the member emails of the {@code Session} that we are building.
     */
    public SessionBuilder withMembers(String... emails) {
        this.members = SampleDataUtil.getEmailSet(emails);
        return this;
    }

    public Session build() {
        return new Session(name, dateTime, location, completed, members);
    }

}
