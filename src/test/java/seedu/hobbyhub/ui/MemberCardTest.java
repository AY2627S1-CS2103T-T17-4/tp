package seedu.hobbyhub.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import seedu.hobbyhub.testutil.MemberBuilder;

public class MemberCardTest {

    @Test
    public void constructor_populatesAllMemberFieldsAndSortedTags() {
        FxTestHelper.runOnFxThread(() -> {
            var member = new MemberBuilder().withName("Alex").withPhone("91234567")
                    .withEmail("alex@example.com").withAddress("North Street").withHobby("Chess")
                    .withTags("zebra", "apple").build();
            MemberCard card = new MemberCard(member, 2);
            HBox root = (HBox) card.getRoot();

            assertEquals(member, card.member);
            assertEquals("2. ", ((Label) root.lookup("#id")).getText());
            assertEquals("Alex", ((Label) root.lookup("#name")).getText());
            assertEquals("91234567", ((Label) root.lookup("#phone")).getText());
            assertEquals("North Street", ((Label) root.lookup("#address")).getText());
            assertEquals("alex@example.com", ((Label) root.lookup("#email")).getText());
            assertEquals("Chess", ((Label) root.lookup("#hobby")).getText());

            FlowPane tags = (FlowPane) root.lookup("#tags");
            List<String> tagNames = tags.getChildren().stream()
                    .map(node -> ((Label) node).getText())
                    .collect(Collectors.toList());
            assertEquals(List.of("apple", "zebra"), tagNames);
        });
    }
}
