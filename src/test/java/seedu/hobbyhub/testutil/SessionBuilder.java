package seedu.hobbyhub.testutil;

import java.time.LocalDate;
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
    public static final LocalDate DEFAULT_DATE = LocalDate.of(2026, 10, 24);

    private SessionName name;
    private LocalDate date;
    private Set<Email> members;

    /**
     * Creates a {@code SessionBuilder} with the default details and no members.
     */
    public SessionBuilder() {
        name = new SessionName(DEFAULT_NAME);
        date = DEFAULT_DATE;
        members = new HashSet<>();
    }

    /**
     * Initializes the SessionBuilder with the data of {@code sessionToCopy}.
     */
    public SessionBuilder(Session sessionToCopy) {
        name = sessionToCopy.getName();
        date = sessionToCopy.getDate();
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
    public SessionBuilder withDate(String date) {
        this.date = LocalDate.parse(date);
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
        return new Session(name, date, members);
    }

}
