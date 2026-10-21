package seedu.hobbyhub.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import seedu.hobbyhub.model.session.Session;
import seedu.hobbyhub.testutil.SessionBuilder;

public class SessionCardTest {

    @Test
    public void constructor_populatesNameDateAndMemberCount() {
        FxTestHelper.runOnFxThread(() -> {
            Session session = new SessionBuilder().withName("Golf Meet").withDateTime("2026-10-24T14:00")
                    .withLocation("Community Center").withCompleted(true)
                    .withMembers("alex@example.com", "bernice@example.com").build();
            SessionCard card = new SessionCard(session, 3);
            HBox root = (HBox) card.getRoot();

            assertEquals(session, card.session);
            assertEquals("3. ", ((Label) root.lookup("#id")).getText());
            assertEquals("Golf Meet", ((Label) root.lookup("#name")).getText());
            assertEquals("Sat, 24 Oct 2026 at 2:00 PM", ((Label) root.lookup("#date")).getText());
            assertEquals("Community Center", ((Label) root.lookup("#locationLabel")).getText());
            assertEquals("Completed", ((Label) root.lookup("#completed")).getText());
            assertEquals("2 members", ((Label) root.lookup("#memberCount")).getText());
        });
    }

    @Test
    public void constructor_oneMember_usesSingular() {
        FxTestHelper.runOnFxThread(() -> {
            Session session = new SessionBuilder().withMembers("alex@example.com").build();
            HBox root = (HBox) new SessionCard(session, 1).getRoot();
            assertEquals("1 member", ((Label) root.lookup("#memberCount")).getText());
        });
    }
}
