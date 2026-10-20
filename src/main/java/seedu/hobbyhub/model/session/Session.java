package seedu.hobbyhub.model.session;

import static java.util.Objects.requireNonNull;
import static seedu.hobbyhub.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.hobbyhub.commons.util.ToStringBuilder;
import seedu.hobbyhub.model.member.Email;

/**
 * Represents a Session in the HobbyHub: an activity on a date that members can be assigned to.
 * Members are referred to by their email, which is unique and cannot be edited.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Session {

    private final SessionName name;
    private final LocalDate date;
    private final Set<Email> members = new HashSet<>();

    /**
     * Every field must be present and not null.
     */
    public Session(SessionName name, LocalDate date, Set<Email> members) {
        requireAllNonNull(name, date, members);
        requireAllNonNull(members);
        this.name = name;
        this.date = date;
        this.members.addAll(members);
    }

    public SessionName getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns an immutable set of the emails of the members assigned to this session,
     * which throws {@code UnsupportedOperationException} if modification is attempted.
     */
    public Set<Email> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    /**
     * Returns true if the member with the given email is assigned to this session.
     */
    public boolean hasMember(Email email) {
        requireNonNull(email);
        return members.contains(email);
    }

    /**
     * Returns a copy of this session with the member with the given email added.
     * The member must not already be assigned to this session.
     */
    public Session withMember(Email email) {
        requireNonNull(email);
        assert !hasMember(email) : "Callers should check hasMember before adding a member";

        Set<Email> updatedMembers = new HashSet<>(members);
        updatedMembers.add(email);
        return new Session(name, date, updatedMembers);
    }

    /**
     * Returns a copy of this session with the member with the given email removed.
     * The member must be assigned to this session.
     */
    public Session withoutMember(Email email) {
        requireNonNull(email);
        assert hasMember(email) : "Callers should check hasMember before removing a member";

        Set<Email> updatedMembers = new HashSet<>(members);
        updatedMembers.remove(email);
        return new Session(name, date, updatedMembers);
    }

    /**
     * Returns true if both sessions have the same name, ignoring case.
     * This defines a weaker notion of equality between two sessions.
     */
    public boolean isSameSession(Session otherSession) {
        if (otherSession == this) {
            return true;
        }

        return otherSession != null
                && otherSession.getName().equals(getName());
    }

    /**
     * Returns true if both sessions have the same name, date and members.
     * This defines a stronger notion of equality between two sessions.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Session otherSession)) {
            return false;
        }

        return name.equals(otherSession.name)
                && date.equals(otherSession.date)
                && members.equals(otherSession.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, date, members);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("date", date)
                .add("members", members)
                .toString();
    }

}
