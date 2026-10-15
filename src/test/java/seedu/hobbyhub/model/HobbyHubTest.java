package seedu.hobbyhub.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.hobbyhub.testutil.Assert.assertThrows;
import static seedu.hobbyhub.testutil.TypicalMembers.ALICE;
import static seedu.hobbyhub.testutil.TypicalMembers.getTypicalHobbyHub;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.member.exceptions.DuplicateMemberException;
import seedu.hobbyhub.testutil.MemberBuilder;

public class HobbyHubTest {

    private final HobbyHub hobbyHub = new HobbyHub();

    @Test
    public void constructor() {
        assertEquals(List.of(), hobbyHub.getMemberList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> hobbyHub.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyHobbyHub_replacesData() {
        HobbyHub newData = getTypicalHobbyHub();
        hobbyHub.resetData(newData);
        assertEquals(newData, hobbyHub);
    }

    @Test
    public void resetData_withDuplicateMembers_throwsDuplicateMemberException() {
        // Two members with the same identity fields
        Member editedAlice = new MemberBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Member> newMembers = List.of(ALICE, editedAlice);
        HobbyHubStub newData = new HobbyHubStub(newMembers);

        assertThrows(DuplicateMemberException.class, () -> hobbyHub.resetData(newData));
    }

    @Test
    public void hasMember_nullMember_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> hobbyHub.hasMember(null));
    }

    @Test
    public void hasMember_memberNotInHobbyHub_returnsFalse() {
        assertFalse(hobbyHub.hasMember(ALICE));
    }

    @Test
    public void hasMember_memberInHobbyHub_returnsTrue() {
        hobbyHub.addMember(ALICE);
        assertTrue(hobbyHub.hasMember(ALICE));
    }

    @Test
    public void hasMember_memberWithSameIdentityFieldsInHobbyHub_returnsTrue() {
        hobbyHub.addMember(ALICE);
        Member editedAlice = new MemberBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(hobbyHub.hasMember(editedAlice));
    }

    @Test
    public void getMemberList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> hobbyHub.getMemberList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = HobbyHub.class.getCanonicalName() + "{members=" + hobbyHub.getMemberList() + "}";
        assertEquals(expected, hobbyHub.toString());
    }

    /**
     * A stub ReadOnlyHobbyHub whose members list can violate interface constraints.
     */
    private static class HobbyHubStub implements ReadOnlyHobbyHub {
        private final ObservableList<Member> members = FXCollections.observableArrayList();

        HobbyHubStub(Collection<Member> members) {
            this.members.setAll(members);
        }

        @Override
        public ObservableList<Member> getMemberList() {
            return members;
        }
    }

}
