package seedu.hobbyhub.testutil;

import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_ADDRESS_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_GROUP_FRIEND;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_GROUP_HUSBAND;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_HOBBY_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_HOBBY_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.hobbyhub.logic.commands.CommandTestUtil.VALID_PHONE_BOB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.hobbyhub.model.HobbyHub;
import seedu.hobbyhub.model.member.Member;

/**
 * A utility class containing a list of {@code Member} objects to be used in tests.
 */
public class TypicalMembers {

    public static final Member ALICE = new MemberBuilder().withName("Alice Pauline")
            .withAddress("123, Jurong West Ave 6, #08-111").withEmail("alice@example.com")
            .withPhone("94351253").withHobby("Chess")
            .withGroups("friends").build();
    public static final Member BENSON = new MemberBuilder().withName("Benson Meier")
            .withAddress("311, Clementi Ave 2, #02-25")
            .withEmail("johnd@example.com").withPhone("98765432").withHobby("Photography")
            .withGroups("owesMoney", "friends").build();
    public static final Member CARL = new MemberBuilder().withName("Carl Kurz").withPhone("95352563")
            .withEmail("heinz@example.com").withAddress("wall street").withHobby("Cycling").build();
    public static final Member DANIEL = new MemberBuilder().withName("Daniel Meier").withPhone("87652533")
            .withEmail("cornelia@example.com").withAddress("10th street").withHobby("Cooking").withGroups("friends")
            .build();
    public static final Member ELLE = new MemberBuilder().withName("Elle Meyer").withPhone("9482224")
            .withEmail("werner@example.com").withAddress("michegan ave").withHobby("Painting").build();
    public static final Member FIONA = new MemberBuilder().withName("Fiona Kunz").withPhone("9482427")
            .withEmail("lydia@example.com").withAddress("little tokyo").withHobby("Hiking").build();
    public static final Member GEORGE = new MemberBuilder().withName("George Best").withPhone("9482442")
            .withEmail("anna@example.com").withAddress("4th street").withHobby("Gardening").build();

    // Manually added
    public static final Member HOON = new MemberBuilder().withName("Hoon Meier").withPhone("8482424")
            .withEmail("stefan@example.com").withAddress("little india").withHobby("Badminton").build();
    public static final Member IDA = new MemberBuilder().withName("Ida Mueller").withPhone("8482131")
            .withEmail("hans@example.com").withAddress("chicago ave").withHobby("Running").build();

    // Manually added - Member's details found in {@code CommandTestUtil}
    public static final Member AMY = new MemberBuilder().withName(VALID_NAME_AMY).withPhone(VALID_PHONE_AMY)
            .withEmail(VALID_EMAIL_AMY).withAddress(VALID_ADDRESS_AMY).withHobby(VALID_HOBBY_AMY)
            .withGroups(VALID_GROUP_FRIEND).build();
    public static final Member BOB = new MemberBuilder().withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
            .withEmail(VALID_EMAIL_BOB).withAddress(VALID_ADDRESS_BOB).withHobby(VALID_HOBBY_BOB)
            .withGroups(VALID_GROUP_HUSBAND, VALID_GROUP_FRIEND).build();

    public static final String KEYWORD_MATCHING_MEIER = "Meier"; // A keyword that matches MEIER

    private TypicalMembers() {} // prevents instantiation

    /**
     * Returns an {@code HobbyHub} with all the typical members.
     */
    public static HobbyHub getTypicalHobbyHub() {
        HobbyHub ab = new HobbyHub();
        for (Member member : getTypicalMembers()) {
            ab.addMember(member);
        }
        return ab;
    }

    public static List<Member> getTypicalMembers() {
        return new ArrayList<>(Arrays.asList(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE));
    }
}
