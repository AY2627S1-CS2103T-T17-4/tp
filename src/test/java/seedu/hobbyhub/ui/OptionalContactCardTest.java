package seedu.hobbyhub.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import seedu.hobbyhub.testutil.MemberBuilder;

public class OptionalContactCardTest {
    @Test
    public void missingContactDetails_displayNotProvided() {
        List<String> labels = FxTestHelper.callOnFxThread(() -> {
            var member = new MemberBuilder().withPhone("").withAddress("").build();
            var card = new MemberCard(member, 1);
            Label phone = (Label) card.getRoot().lookup("#phone");
            Label address = (Label) card.getRoot().lookup("#address");
            return List.of(phone.getText(), address.getText());
        });
        assertEquals(List.of("Not provided", "Not provided"), labels);
    }
}
