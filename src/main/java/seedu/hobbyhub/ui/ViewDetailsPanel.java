package seedu.hobbyhub.ui;

import static java.util.Objects.requireNonNull;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.hobbyhub.logic.commands.ViewDetails;

/** Displays the structured profile returned by a {@code view} command. */
public class ViewDetailsPanel extends UiPart<Region> {

    private static final String FXML = "ViewDetailsPanel.fxml";

    @FXML
    private Label title;

    @FXML
    private Label subtitle;

    @FXML
    private VBox fields;

    public ViewDetailsPanel() {
        super(FXML);
    }

    /** Shows the given record details. */
    public void showDetails(ViewDetails details) {
        requireNonNull(details);
        title.setText(details.title());
        subtitle.setText(details.subtitle());
        fields.getChildren().clear();
        details.fields().forEach(field -> {
            Label fieldName = new Label(field.label().toUpperCase());
            fieldName.getStyleClass().add("detail-label");
            Label fieldValue = new Label(field.value());
            fieldValue.getStyleClass().add("detail-value");
            fieldValue.setWrapText(true);
            fieldValue.setMaxWidth(Double.MAX_VALUE);
            VBox fieldBox = new VBox(4, fieldName, fieldValue);
            fieldBox.getStyleClass().add("detail-field");
            fields.getChildren().add(fieldBox);
        });
    }

    /** Clears the profile and restores its instructional placeholder. */
    public void clear() {
        title.setText("Nothing selected");
        subtitle.setText("Use view INDEX to inspect a record.");
        fields.getChildren().clear();
    }
}
