package seedu.hobbyhub.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import seedu.hobbyhub.logic.commands.ViewDetails;

public class ViewDetailsPanelTest {

    @Test
    public void constructor_showsEmptyState() {
        FxTestHelper.runOnFxThread(() -> {
            ViewDetailsPanel panel = new ViewDetailsPanel();
            Scene scene = prepareScene(panel);

            assertEquals("Nothing selected", ((Label) scene.lookup("#title")).getText());
            assertEquals("Use view INDEX to inspect a record.", ((Label) scene.lookup("#subtitle")).getText());
            assertTrue(((VBox) scene.lookup("#fields")).getChildren().isEmpty());
        });
    }

    @Test
    public void showDetails_populatesTitleSubtitleAndFields() {
        FxTestHelper.runOnFxThread(() -> {
            ViewDetailsPanel panel = new ViewDetailsPanel();
            Scene scene = prepareScene(panel);
            ViewDetails details = new ViewDetails("Alex Yeoh", "alex@example.com", List.of(
                    new ViewDetails.Field("Phone", "12345678"),
                    new ViewDetails.Field("Hobbies", "Chess\nTennis")),
                    ViewDetails.RecordType.MEMBER, "alex@example.com");

            panel.showDetails(details);

            assertEquals("Alex Yeoh", ((Label) scene.lookup("#title")).getText());
            assertEquals("alex@example.com", ((Label) scene.lookup("#subtitle")).getText());
            VBox fields = (VBox) scene.lookup("#fields");
            assertEquals(2, fields.getChildren().size());
            List<String> displayedText = fields.getChildren().stream()
                    .flatMap(node -> ((VBox) node).getChildren().stream())
                    .map(node -> ((Label) node).getText())
                    .toList();
            assertEquals(List.of("PHONE", "12345678", "HOBBIES", "Chess\nTennis"), displayedText);
        });
    }

    @Test
    public void clear_restoresEmptyState() {
        FxTestHelper.runOnFxThread(() -> {
            ViewDetailsPanel panel = new ViewDetailsPanel();
            Scene scene = prepareScene(panel);
            panel.showDetails(new ViewDetails("Alex Yeoh", "alex@example.com",
                    List.of(new ViewDetails.Field("Phone", "12345678")),
                    ViewDetails.RecordType.MEMBER, "alex@example.com"));

            panel.clear();

            assertEquals("Nothing selected", ((Label) scene.lookup("#title")).getText());
            assertEquals("Use view INDEX to inspect a record.", ((Label) scene.lookup("#subtitle")).getText());
            assertTrue(((VBox) scene.lookup("#fields")).getChildren().isEmpty());
        });
    }

    private static Scene prepareScene(ViewDetailsPanel panel) {
        Scene scene = new Scene(panel.getRoot(), 400, 500);
        scene.getRoot().applyCss();
        scene.getRoot().layout();
        return scene;
    }
}
