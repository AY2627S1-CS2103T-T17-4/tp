package seedu.hobbyhub.model.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_HOBBY_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.hobbyhub.testutil.Assert.assertThrows;
import static seedu.hobbyhub.testutil.TypicalMembers.ALICE;
import static seedu.hobbyhub.testutil.TypicalMembers.BOB;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.testutil.MemberBuilder;

public class MemberTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Member member = new MemberBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> member.getTags().remove(0));
    }

    @Test
    public void isSameMember() {
        // same object -> returns true
        assertTrue(ALICE.isSameMember(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSameMember(null));

        // same email, all other attributes different -> returns true
        Member editedAlice = new MemberBuilder(ALICE).withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND).build();
        assertTrue(ALICE.isSameMember(editedAlice));

        // different email, all other attributes same -> returns false
        editedAlice = new MemberBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.isSameMember(editedAlice));

        // email differs in case, all other attributes same -> returns true
        Member editedBob = new MemberBuilder(BOB).withEmail(VALID_EMAIL_BOB.toUpperCase()).build();
        assertTrue(BOB.isSameMember(editedBob));

        // same name, different email -> returns false
        editedBob = new MemberBuilder(BOB).withEmail(VALID_EMAIL_AMY).build();
        assertFalse(BOB.isSameMember(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Member aliceCopy = new MemberBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different member -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Member editedAlice = new MemberBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new MemberBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new MemberBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new MemberBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different hobby -> returns false
        editedAlice = new MemberBuilder(ALICE).withHobby(VALID_HOBBY_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new MemberBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void toStringMethod() {
        String expected = Member.class.getCanonicalName() + "{name=" + ALICE.getName() + ", phone=" + ALICE.getPhone()
                + ", email=" + ALICE.getEmail() + ", address=" + ALICE.getAddress()
                + ", hobby=" + ALICE.getHobby() + ", tags=" + ALICE.getTags() + "}";
        assertEquals(expected, ALICE.toString());
    }
}
