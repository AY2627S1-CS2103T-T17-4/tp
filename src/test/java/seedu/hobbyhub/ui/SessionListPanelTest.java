package seedu.hobbyhub.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import seedu.hobbyhub.testutil.TypicalSessions;

public class SessionListPanelTest {

    @Test
    public void constructorAndCellFactory_displaySessionsAndHandleEmptyCells() {
        FxTestHelper.runOnFxThread(() -> {
            SessionListPanel panel = new SessionListPanel(
                    FXCollections.observableArrayList(TypicalSessions.CHESS_MEETUP));
            ListView<?> listView = (ListView<?>) panel.getRoot().lookup(".list-view");
            SessionListPanel.SessionListViewCell cell = panel.new SessionListViewCell();

            assertEquals(1, listView.getItems().size());
            assertTrue(listView.getCellFactory().call(null) instanceof SessionListPanel.SessionListViewCell);

            cell.updateIndex(0);
            cell.updateItem(TypicalSessions.CHESS_MEETUP, false);
            assertTrue(cell.getGraphic() instanceof javafx.scene.layout.Region);

            cell.updateItem(null, true);
            assertNull(cell.getGraphic());
            assertNull(cell.getText());
        });
    }
}
