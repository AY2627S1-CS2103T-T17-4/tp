package seedu.hobbyhub.model;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import javafx.collections.ObservableList;
import seedu.hobbyhub.commons.util.ToStringBuilder;
import seedu.hobbyhub.model.member.Email;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.member.UniqueMemberList;
import seedu.hobbyhub.model.session.Session;
import seedu.hobbyhub.model.session.UniqueSessionList;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSameMember and .isSameSession comparison).
 */
public class HobbyHub implements ReadOnlyHobbyHub {

    private final UniqueMemberList members = new UniqueMemberList();
    private final UniqueSessionList sessions = new UniqueSessionList();

    public HobbyHub() {}

    /**
     * Creates an HobbyHub using the Members and Sessions in the {@code toBeCopied}
     */
    public HobbyHub(ReadOnlyHobbyHub toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the member list with {@code members}.
     * {@code members} must not contain duplicate members.
     */
    public void setMembers(List<Member> members) {
        this.members.setMembers(members);
        refreshMemberSessions();
    }

    /**
     * Replaces the contents of the session list with {@code sessions}.
     * {@code sessions} must not contain duplicate sessions.
     */
    public void setSessions(List<Session> sessions) {
        this.sessions.setSessions(sessions);
        refreshMemberSessions();
    }

    /**
     * Resets the existing data of this {@code HobbyHub} with {@code newData}.
     */
    public void resetData(ReadOnlyHobbyHub newData) {
        requireNonNull(newData);

        setMembers(newData.getMemberList());
        setSessions(newData.getSessionList());
    }

    //// member-level operations

    /**
     * Returns true if a member with the same identity as {@code member} exists in the HobbyHub.
     */
    public boolean hasMember(Member member) {
        requireNonNull(member);
        return members.contains(member);
    }

    /**
     * Adds a member to the HobbyHub.
     * The member must not already exist in the HobbyHub.
     */
    public void addMember(Member member) {
        members.add(member);
        refreshMemberSessions();
    }

    /**
     * Replaces the given member {@code target} in the list with {@code editedMember}.
     * {@code target} must exist in the HobbyHub.
     * The member identity of {@code editedMember} must not be the same as another existing member in the HobbyHub.
     * If the email changes, every session {@code target} is assigned to is updated to the new email.
     */
    public void setMember(Member target, Member editedMember) {
        requireNonNull(editedMember);

        members.setMember(target, editedMember);

        Email oldEmail = target.getEmail();
        Email newEmail = editedMember.getEmail();
        if (!oldEmail.equals(newEmail)) {
            for (Session session : getSessionsWith(oldEmail)) {
                sessions.setSession(session, session.withoutMember(oldEmail).withMember(newEmail));
            }
        }
        refreshMemberSessions();
    }

    /**
     * Removes {@code key} from this {@code HobbyHub}, and unassigns it from every session it is assigned to.
     * {@code key} must exist in the HobbyHub.
     */
    public void removeMember(Member key) {
        members.remove(key);

        Email email = key.getEmail();
        for (Session session : getSessionsWith(email)) {
            sessions.setSession(session, session.withoutMember(email));
        }
        refreshMemberSessions();
    }

    /**
     * Returns the sessions that the member with the given email is assigned to.
     */
    private List<Session> getSessionsWith(Email email) {
        return sessions.asUnmodifiableObservableList().stream()
                .filter(session -> session.hasMember(email))
                .toList();
    }

    /** Updates each member's stored session list to match the session assignments. */
    private void refreshMemberSessions() {
        for (Member member : List.copyOf(getMemberList())) {
            List<Session> assignedSessions = sessions.asUnmodifiableObservableList().stream()
                    .filter(session -> session.hasMember(member.getEmail()))
                    .toList();
            if (!member.getSessions().equals(assignedSessions)) {
                members.setMember(member, member.withSessions(assignedSessions));
            }
        }
    }

    //// session-level operations

    /**
     * Returns true if a session with the same identity as {@code session} exists in the HobbyHub.
     */
    public boolean hasSession(Session session) {
        requireNonNull(session);
        return sessions.contains(session);
    }

    /**
     * Adds a session to the HobbyHub.
     * The session must not already exist in the HobbyHub.
     */
    public void addSession(Session session) {
        sessions.add(session);
        refreshMemberSessions();
    }

    /**
     * Replaces the given session {@code target} in the list with {@code editedSession}.
     * {@code target} must exist in the HobbyHub.
     * The session identity of {@code editedSession} must not be the same as another existing session in the HobbyHub.
     */
    public void setSession(Session target, Session editedSession) {
        requireNonNull(editedSession);

        sessions.setSession(target, editedSession);
        refreshMemberSessions();
    }

    /**
     * Removes {@code key} from this {@code HobbyHub}.
     * {@code key} must exist in the HobbyHub.
     */
    public void removeSession(Session key) {
        sessions.remove(key);
        refreshMemberSessions();
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("members", members)
                .add("sessions", sessions)
                .toString();
    }

    @Override
    public ObservableList<Member> getMemberList() {
        return members.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Session> getSessionList() {
        return sessions.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof HobbyHub otherHobbyHub)) {
            return false;
        }

        return members.equals(otherHobbyHub.members)
                && sessions.equals(otherHobbyHub.sessions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(members, sessions);
    }
}
