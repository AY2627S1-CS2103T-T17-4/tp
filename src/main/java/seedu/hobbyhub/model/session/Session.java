package seedu.hobbyhub.model.session;

import static java.util.Objects.requireNonNull;
import static seedu.hobbyhub.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.hobbyhub.commons.util.ToStringBuilder;
import seedu.hobbyhub.model.member.Email;

/**
 * Represents a Session in the HobbyHub: an activity at a date and time that members can be assigned to.
 * Members are referred to by their email, which is unique ignoring case.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Session {

    private final SessionName name;
    private final LocalDateTime date;
    private final String location;
    private final boolean completed;
    private final Set<Email> members = new HashSet<>();

    /**
     * Every field must be present and not null.
     */
    public Session(SessionName name, LocalDateTime date, String location, boolean completed, Set<Email> members) {
        requireAllNonNull(name, date, location, members);
        requireAllNonNull(members);
        this.name = name;
        this.date = date;
        this.location = location;
        this.completed = completed;
        this.members.addAll(members);
    }

    public SessionName getName() {
        return name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public boolean isCompleted() {
        return completed;
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
     * Emails are compared ignoring case, the same way members are.
     */
    public boolean hasMember(Email email) {
        requireNonNull(email);
        return findMember(email).isPresent();
    }

    /**
     * Returns the stored email that matches {@code email} ignoring case, if any.
     */
    private Optional<Email> findMember(Email email) {
        return members.stream()
                .filter(member -> member.value.equalsIgnoreCase(email.value))
                .findFirst();
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
        return new Session(name, date, location, completed, updatedMembers);
    }

    /**
     * Returns a copy of this session with its completion status set to {@code isCompleted}.
     */
    public Session withCompleted(boolean isCompleted) {
        return new Session(name, date, location, isCompleted, members);
    }

    /**
     * Returns a copy of this session with the member with the given email removed, matching ignoring case.
     * The member must be assigned to this session.
     */
    public Session withoutMember(Email email) {
        requireNonNull(email);
        assert hasMember(email) : "Callers should check hasMember before removing a member";

        Set<Email> updatedMembers = new HashSet<>(members);
        updatedMembers.remove(findMember(email).get());
        return new Session(name, date, location, completed, updatedMembers);
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
     * Returns true if both sessions have the same name, date, location, completion state and members.
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
                && location.equals(otherSession.location)
                && completed == otherSession.completed
                && members.equals(otherSession.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, date, location, completed, members);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("date", date)
                .add("location", location)
                .add("completed", completed)
                .add("members", members)
                .toString();
    }

}
