package seedu.hobbyhub.model.member;

import static seedu.hobbyhub.commons.util.AppUtil.checkArgument;
import static seedu.hobbyhub.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import seedu.hobbyhub.commons.util.ToStringBuilder;
import seedu.hobbyhub.model.group.Group;
import seedu.hobbyhub.model.session.Session;

/**
 * Represents a Member in the HobbyHub.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Member {
    public static final String MESSAGE_HOBBIES_REQUIRED = "A member must have at least one hobby.";
    public static final String MESSAGE_DUPLICATE_HOBBY = "Hobby names must be unique, ignoring capitalization.";

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    private final List<Hobby> hobbies;

    // Data fields
    private final Address address;
    private final Set<Group> groups = new HashSet<>();
    private final List<Session> sessions;

    /**
     * Every field must be present and not null.
     */
    public Member(Name name, Phone phone, Email email, Address address, Hobby hobby, Set<Group> groups) {
        this(name, phone, email, address, List.of(hobby), groups);
    }

    /**
     * Constructs a member with at least one hobby and no repeated hobby names, ignoring case.
     * The hobby list is copied and its order is preserved for display.
     */
    public Member(Name name, Phone phone, Email email, Address address, List<Hobby> hobbies, Set<Group> groups) {
        this(name, phone, email, address, hobbies, groups, List.of());
    }

    /**
     * Constructs a member with at least one hobby and the sessions they are assigned to.
     */
    public Member(Name name, Phone phone, Email email, Address address, List<Hobby> hobbies, Set<Group> groups,
            List<Session> sessions) {
        requireAllNonNull(name, phone, email, address, hobbies, groups, sessions);
        requireAllNonNull(hobbies);
        requireAllNonNull(sessions);
        checkArgument(!hobbies.isEmpty(), MESSAGE_HOBBIES_REQUIRED);
        long uniqueNames = hobbies.stream()
                .map(hobby -> hobby.value.trim().toLowerCase(Locale.ROOT)).distinct().count();
        checkArgument(uniqueNames == hobbies.size(), MESSAGE_DUPLICATE_HOBBY);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.hobbies = List.copyOf(hobbies);
        this.groups.addAll(groups);
        this.sessions = List.copyOf(sessions);
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * Returns the first hobby for compatibility with single-hobby callers.
     * Callers working with the complete member profile should use {@link #getHobbies()}.
     */
    public Hobby getHobby() {
        return hobbies.getFirst();
    }

    /** Returns the immutable list of all hobbies and their skill levels. */
    public List<Hobby> getHobbies() {
        return hobbies;
    }

    /**
     * Returns an immutable group set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Group> getGroups() {
        return Collections.unmodifiableSet(groups);
    }

    /** Returns the immutable list of sessions assigned to this member. */
    public List<Session> getSessions() {
        return sessions;
    }

    /** Returns a copy of this member with the given assigned sessions. */
    public Member withSessions(List<Session> sessions) {
        return new Member(name, phone, email, address, hobbies, groups, sessions);
    }

    /**
     * Returns true if both members have the same email, ignoring case.
     * This defines a weaker notion of equality between two members.
     */
    public boolean isSameMember(Member otherMember) {
        if (otherMember == this) {
            return true;
        }

        return otherMember != null
                && otherMember.getEmail().value.equalsIgnoreCase(getEmail().value);
    }

    /**
     * Returns true if both members have the same identity and data fields.
     * This defines a stronger notion of equality between two members.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Member otherMember)) {
            return false;
        }

        return name.equals(otherMember.name)
                && phone.equals(otherMember.phone)
                && email.equals(otherMember.email)
                && address.equals(otherMember.address)
                && hobbies.equals(otherMember.hobbies)
                && groups.equals(otherMember.groups)
                && sessions.equals(otherMember.sessions);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, hobbies, groups, sessions);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("hobbies", hobbies)
                .add("groups", groups)
                .add("sessions", sessions)
                .toString();
    }

}
