package seedu.hobbyhub.model.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.hobbyhub.testutil.Assert.assertThrows;
import static seedu.hobbyhub.testutil.TypicalMembers.ALICE;
import static seedu.hobbyhub.testutil.TypicalMembers.BOB;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.model.member.exceptions.DuplicateMemberException;
import seedu.hobbyhub.model.member.exceptions.MemberNotFoundException;
import seedu.hobbyhub.testutil.MemberBuilder;

public class UniqueMemberListTest {

    private final UniqueMemberList uniqueMemberList = new UniqueMemberList();

    @Test
    public void contains_nullMember_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueMemberList.contains(null));
    }

    @Test
    public void contains_memberNotInList_returnsFalse() {
        assertFalse(uniqueMemberList.contains(ALICE));
    }

    @Test
    public void contains_memberInList_returnsTrue() {
        uniqueMemberList.add(ALICE);
        assertTrue(uniqueMemberList.contains(ALICE));
    }

    @Test
    public void contains_memberWithSameIdentityFieldsInList_returnsTrue() {
        uniqueMemberList.add(ALICE);
        Member editedAlice = new MemberBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(uniqueMemberList.contains(editedAlice));
    }

    @Test
    public void add_nullMember_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueMemberList.add(null));
    }

    @Test
    public void add_duplicateMember_throwsDuplicateMemberException() {
        uniqueMemberList.add(ALICE);
        assertThrows(DuplicateMemberException.class, () -> uniqueMemberList.add(ALICE));
    }

    @Test
    public void setMember_nullTargetMember_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueMemberList.setMember(null, ALICE));
    }

    @Test
    public void setMember_nullEditedMember_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueMemberList.setMember(ALICE, null));
    }

    @Test
    public void setMember_targetMemberNotInList_throwsMemberNotFoundException() {
        assertThrows(MemberNotFoundException.class, () -> uniqueMemberList.setMember(ALICE, ALICE));
    }

    @Test
    public void setMember_editedMemberIsSameMember_success() {
        uniqueMemberList.add(ALICE);
        uniqueMemberList.setMember(ALICE, ALICE);
        UniqueMemberList expectedUniqueMemberList = new UniqueMemberList();
        expectedUniqueMemberList.add(ALICE);
        assertEquals(expectedUniqueMemberList, uniqueMemberList);
    }

    @Test
    public void setMember_editedMemberHasSameIdentity_success() {
        uniqueMemberList.add(ALICE);
        Member editedAlice = new MemberBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        uniqueMemberList.setMember(ALICE, editedAlice);
        UniqueMemberList expectedUniqueMemberList = new UniqueMemberList();
        expectedUniqueMemberList.add(editedAlice);
        assertEquals(expectedUniqueMemberList, uniqueMemberList);
    }

    @Test
    public void setMember_editedMemberHasDifferentIdentity_success() {
        uniqueMemberList.add(ALICE);
        uniqueMemberList.setMember(ALICE, BOB);
        UniqueMemberList expectedUniqueMemberList = new UniqueMemberList();
        expectedUniqueMemberList.add(BOB);
        assertEquals(expectedUniqueMemberList, uniqueMemberList);
    }

    @Test
    public void setMember_editedMemberHasNonUniqueIdentity_throwsDuplicateMemberException() {
        uniqueMemberList.add(ALICE);
        uniqueMemberList.add(BOB);
        assertThrows(DuplicateMemberException.class, () -> uniqueMemberList.setMember(ALICE, BOB));
    }

    @Test
    public void remove_nullMember_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueMemberList.remove(null));
    }

    @Test
    public void remove_memberDoesNotExist_throwsMemberNotFoundException() {
        assertThrows(MemberNotFoundException.class, () -> uniqueMemberList.remove(ALICE));
    }

    @Test
    public void remove_existingMember_removesMember() {
        uniqueMemberList.add(ALICE);
        uniqueMemberList.remove(ALICE);
        UniqueMemberList expectedUniqueMemberList = new UniqueMemberList();
        assertEquals(expectedUniqueMemberList, uniqueMemberList);
    }

    @Test
    public void setMembers_nullUniqueMemberList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueMemberList.setMembers((UniqueMemberList) null));
    }

    @Test
    public void setMembers_uniqueMemberList_replacesOwnListWithProvidedUniqueMemberList() {
        uniqueMemberList.add(ALICE);
        UniqueMemberList expectedUniqueMemberList = new UniqueMemberList();
        expectedUniqueMemberList.add(BOB);
        uniqueMemberList.setMembers(expectedUniqueMemberList);
        assertEquals(expectedUniqueMemberList, uniqueMemberList);
    }

    @Test
    public void setMembers_nullList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueMemberList.setMembers((List<Member>) null));
    }

    @Test
    public void setMembers_list_replacesOwnListWithProvidedList() {
        uniqueMemberList.add(ALICE);
        List<Member> memberList = List.of(BOB);
        uniqueMemberList.setMembers(memberList);
        UniqueMemberList expectedUniqueMemberList = new UniqueMemberList();
        expectedUniqueMemberList.add(BOB);
        assertEquals(expectedUniqueMemberList, uniqueMemberList);
    }

    @Test
    public void setMembers_listWithDuplicateMembers_throwsDuplicateMemberException() {
        List<Member> listWithDuplicateMembers = List.of(ALICE, ALICE);
        assertThrows(DuplicateMemberException.class, () -> uniqueMemberList.setMembers(listWithDuplicateMembers));
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, ()
            -> uniqueMemberList.asUnmodifiableObservableList().remove(0));
    }

    @Test
    public void iteratorAndEqualityMethods() {
        uniqueMemberList.add(ALICE);

        UniqueMemberList sameMembers = new UniqueMemberList();
        sameMembers.add(ALICE);

        assertEquals(ALICE, uniqueMemberList.iterator().next());
        assertEquals(uniqueMemberList, uniqueMemberList);
        assertEquals(uniqueMemberList, sameMembers);
        assertEquals(uniqueMemberList.hashCode(), sameMembers.hashCode());
        assertFalse(uniqueMemberList.equals(null));
        assertFalse(uniqueMemberList.equals(ALICE));
    }

    @Test
    public void toStringMethod() {
        assertEquals(uniqueMemberList.asUnmodifiableObservableList().toString(), uniqueMemberList.toString());
    }
}
