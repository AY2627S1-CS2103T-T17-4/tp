package seedu.hobbyhub.model.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.logic.commands.EditCommand.EditMemberDescriptor;
import seedu.hobbyhub.testutil.MemberBuilder;

public class MultipleHobbiesTest {
    private final Hobby chess = new Hobby("Chess", SkillLevel.ADVANCED);
    private final Hobby tennis = new Hobby("Table Tennis", SkillLevel.INTERMEDIATE);

    @Test
    public void hobby_defaultSkillAndExplicitSkills() {
        assertEquals(SkillLevel.BEGINNER, new Hobby("Chess").getSkillLevel());
        assertEquals("Chess (Advanced)", chess.toString());
        assertEquals("Beginner", SkillLevel.BEGINNER.toString());
        assertEquals("Intermediate", SkillLevel.INTERMEDIATE.toString());
        assertEquals(chess, new Hobby("Chess", SkillLevel.ADVANCED));
        assertEquals(chess.hashCode(), new Hobby("Chess", SkillLevel.ADVANCED).hashCode());
        assertNotEquals(chess, new Hobby("Chess"));
        assertThrows(NullPointerException.class, () -> new Hobby("Chess", null));
    }

    @Test
    public void constructor_copiesHobbiesAndExposesImmutableList() {
        Member original = new MemberBuilder().build();
        List<Hobby> hobbies = new ArrayList<>(List.of(chess, tennis));
        Member member = new Member(original.getName(), original.getPhone(), original.getEmail(),
                original.getAddress(), hobbies, original.getTags());
        hobbies.clear();
        assertEquals(List.of(chess, tennis), member.getHobbies());
        assertThrows(UnsupportedOperationException.class, () -> member.getHobbies().clear());
        assertEquals(member, new MemberBuilder(member).build());
        assertNotEquals(member, new MemberBuilder(member).withHobbies(chess).build());
    }

    @Test
    public void constructor_emptyOrDuplicateHobbies_rejected() {
        assertThrows(IllegalArgumentException.class, () -> new MemberBuilder().withHobbies().build());
        assertThrows(IllegalArgumentException.class, () -> new MemberBuilder()
                .withHobbies(chess, new Hobby("chess", SkillLevel.BEGINNER)).build());
        assertThrows(IllegalArgumentException.class, () -> new MemberBuilder()
                .withHobbies(chess, new Hobby("Chess ", SkillLevel.INTERMEDIATE)).build());
    }

    @Test
    public void search_matchesAnyHobbyNameButNotSkillLevel() {
        Member member = new MemberBuilder().withHobbies(chess, tennis).build();
        assertTrue(new HobbyContainsKeywordsPredicate(List.of("tenNis")).test(member));
        assertTrue(new HobbyContainsKeywordsPredicate(List.of("chess")).test(member));
        assertFalse(new HobbyContainsKeywordsPredicate(List.of("advanced")).test(member));
    }

    @Test
    public void edit_unrelatedField_preservesAllHobbiesAndSkills() {
        Member member = new MemberBuilder().withHobbies(chess, tennis).build();
        EditMemberDescriptor edit = new EditMemberDescriptor();
        edit.setPhone(new Phone("91234567"));
        assertEquals(member.getHobbies(), edit.createEditedMember(member).getHobbies());
        edit.setHobby(new Hobby("Cycling"));
        assertEquals(List.of(new Hobby("Cycling")), edit.createEditedMember(member).getHobbies());
    }

    @Test
    public void messages_includeEveryHobbyAndSkill() {
        Member member = new MemberBuilder().withHobbies(chess, tennis).build();
        String message = Messages.format(member);
        assertTrue(message.contains("Chess (Advanced), Table Tennis (Intermediate)"));
    }

}
