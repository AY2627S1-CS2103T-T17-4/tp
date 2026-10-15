package seedu.hobbyhub.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import seedu.hobbyhub.testutil.TypicalMembers;

public class MemberListPanelTest {

    @Test
    public void constructorAndCellFactory_displayMembersAndHandleEmptyCells() {
        FxTestHelper.runOnFxThread(() -> {
            MemberListPanel panel = new MemberListPanel(FXCollections.observableArrayList(TypicalMembers.ALICE));
            ListView<?> listView = (ListView<?>) panel.getRoot().lookup(".list-view");
            MemberListPanel.MemberListViewCell cell = panel.new MemberListViewCell();

            assertEquals(1, listView.getItems().size());
            assertTrue(listView.getCellFactory().call(null) instanceof MemberListPanel.MemberListViewCell);

            cell.updateIndex(0);
            cell.updateItem(TypicalMembers.ALICE, false);
            assertTrue(cell.getGraphic() instanceof javafx.scene.layout.Region);

            cell.updateItem(null, true);
            assertNull(cell.getGraphic());
            assertNull(cell.getText());
        });
    }
}
