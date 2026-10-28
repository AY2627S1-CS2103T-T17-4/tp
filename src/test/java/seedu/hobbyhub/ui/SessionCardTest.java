package seedu.hobbyhub.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import seedu.hobbyhub.model.session.Session;
import seedu.hobbyhub.testutil.SessionBuilder;

public class SessionCardTest {

    @Test
    public void constructor_populatesNameAndDetailBadges() {
        FxTestHelper.runOnFxThread(() -> {
            Session session = new SessionBuilder().withName("Golf Meet").withDateTime("2026-10-24T14:00")
                    .withLocation("Community Center").withCompleted(true)
                    .withMembers("alex@example.com", "bernice@example.com").build();
            SessionCard card = new SessionCard(session, 3);
            HBox root = (HBox) card.getRoot();

            assertEquals(session, card.session);
            assertEquals("3. ", ((Label) root.lookup("#id")).getText());
            assertEquals("Golf Meet", ((Label) root.lookup("#name")).getText());
            FlowPane details = (FlowPane) root.lookup("#sessionDetails");
            List<Label> badges = details.getChildren().stream().map(Label.class::cast).toList();

            assertEquals(List.of("Sat, 24 Oct 2026 at 2:00 PM", "Community Center", "Completed", "2 members"),
                    badges.stream().map(Label::getText).toList());
            assertTrue(badges.get(2).getStyleClass().contains("session-status-complete"));
        });
    }

    @Test
    public void constructor_oneMember_usesSingular() {
        FxTestHelper.runOnFxThread(() -> {
            Session session = new SessionBuilder().withMembers("alex@example.com").build();
            HBox root = (HBox) new SessionCard(session, 1).getRoot();
            FlowPane details = (FlowPane) root.lookup("#sessionDetails");
            List<Label> badges = details.getChildren().stream().map(Label.class::cast).toList();
            assertEquals("1 member", badges.get(3).getText());
            assertTrue(badges.get(2).getStyleClass().contains("session-status-open"));
        });
    }

    @Test
    public void constructor_noMembers_showsPluralCount() {
        FxTestHelper.runOnFxThread(() -> {
            Session session = new SessionBuilder().build();
            HBox root = (HBox) new SessionCard(session, 1).getRoot();
            FlowPane details = (FlowPane) root.lookup("#sessionDetails");
            List<Label> badges = details.getChildren().stream().map(Label.class::cast).toList();

            assertEquals("0 members", badges.get(3).getText());
        });
    }
}
