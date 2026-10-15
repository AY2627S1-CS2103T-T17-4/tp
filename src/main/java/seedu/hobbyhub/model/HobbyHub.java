package seedu.hobbyhub.model;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.collections.ObservableList;
import seedu.hobbyhub.commons.util.ToStringBuilder;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.member.UniqueMemberList;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSameMember comparison).
 */
public class HobbyHub implements ReadOnlyHobbyHub {

    private final UniqueMemberList members = new UniqueMemberList();

    public HobbyHub() {}

    /**
     * Creates an HobbyHub using the Members in the {@code toBeCopied}
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
    }

    /**
     * Resets the existing data of this {@code HobbyHub} with {@code newData}.
     */
    public void resetData(ReadOnlyHobbyHub newData) {
        requireNonNull(newData);

        setMembers(newData.getMemberList());
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
    public void addMember(Member p) {
        members.add(p);
    }

    /**
     * Replaces the given member {@code target} in the list with {@code editedMember}.
     * {@code target} must exist in the HobbyHub.
     * The member identity of {@code editedMember} must not be the same as another existing member in the HobbyHub.
     */
    public void setMember(Member target, Member editedMember) {
        requireNonNull(editedMember);

        members.setMember(target, editedMember);
    }

    /**
     * Removes {@code key} from this {@code HobbyHub}.
     * {@code key} must exist in the HobbyHub.
     */
    public void removeMember(Member key) {
        members.remove(key);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("members", members)
                .toString();
    }

    @Override
    public ObservableList<Member> getMemberList() {
        return members.asUnmodifiableObservableList();
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

        return members.equals(otherHobbyHub.members);
    }

    @Override
    public int hashCode() {
        return members.hashCode();
    }
}
