package seedu.hobbyhub.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.hobbyhub.model.member.Hobby;
import seedu.hobbyhub.model.member.SkillLevel;
import seedu.hobbyhub.testutil.MemberBuilder;

public class MemberCardTest {

    @Test
    public void constructor_populatesAllMemberFieldsAndSortedGroups() {
        FxTestHelper.runOnFxThread(() -> {
            var member = new MemberBuilder().withName("Alex").withPhone("91234567")
                    .withEmail("alex@example.com").withAddress("North Street").withHobby("Chess")
                    .withGroups("zebra", "apple").build();
            MemberCard card = new MemberCard(member, 2);
            HBox root = (HBox) card.getRoot();

            assertEquals(member, card.member);
            assertEquals("2. ", ((Label) root.lookup("#id")).getText());
            assertEquals("Alex", ((Label) root.lookup("#name")).getText());
            assertEquals("Chess (Beginner)", ((Label) root.lookup("#hobby")).getText());
            assertEquals("Available: None", ((Label) root.lookup("#availability")).getText());

            FlowPane groups = (FlowPane) root.lookup("#groups");
            List<String> groupNames = groups.getChildren().stream()
                    .map(node -> ((Label) node).getText())
                    .collect(Collectors.toList());
            assertEquals(List.of("apple", "zebra"), groupNames);
        });
    }

    @Test
    public void constructor_longName_wrapsInsteadOfTruncating() {
        FxTestHelper.runOnFxThread(() -> {
            var member = new MemberBuilder().withName("Alexandria Constantinople Bartholomew Montgomery").build();
            MemberCard card = new MemberCard(member, 1);
            Label nameLabel = (Label) card.getRoot().lookup("#name");
            assertTrue(nameLabel.isWrapText());
            assertEquals(Region.USE_PREF_SIZE, nameLabel.getMinHeight());
        });
    }

    @Test
    public void constructor_displaysAllHobbiesAndSkills() {
        FxTestHelper.runOnFxThread(() -> {
            var member = new MemberBuilder().withHobbies(new Hobby("Chess", SkillLevel.ADVANCED),
                    new Hobby("Table Tennis", SkillLevel.INTERMEDIATE)).build();
            MemberCard card = new MemberCard(member, 1);
            Label hobbyLabel = (Label) card.getRoot().lookup("#hobby");
            assertEquals("Chess (Advanced), Table Tennis (Intermediate)", hobbyLabel.getText());
        });
    }

    @Test
    public void constructor_displaysAvailabilityInCalendarOrder() {
        FxTestHelper.runOnFxThread(() -> {
            var member = new MemberBuilder()
                    .withAvailability(DayOfWeek.SUNDAY, DayOfWeek.TUESDAY).build();
            MemberCard card = new MemberCard(member, 1);
            Label availability = (Label) card.getRoot().lookup("#availability");

            assertEquals("Available: Tuesday, Sunday", availability.getText());
        });
    }


    @Test
    public void constructor_displaysMultiwordGroupsSortedSeparatelyFromHobbies() {
        FxTestHelper.runOnFxThread(() -> {
            var member = new MemberBuilder().withGroups("Weekend Club", "Volunteers")
                    .withHobbies(new Hobby("Chess", SkillLevel.ADVANCED)).build();
            MemberCard card = new MemberCard(member, 1);
            FlowPane groups = (FlowPane) card.getRoot().lookup("#groups");
            assertEquals(List.of("Volunteers", "Weekend Club"), groups.getChildren().stream()
                    .map(node -> ((Label) node).getText()).toList());
            Label hobbyLabel = (Label) card.getRoot().lookup("#hobby");
            assertEquals("Chess (Advanced)", hobbyLabel.getText());
        });
    }

}
