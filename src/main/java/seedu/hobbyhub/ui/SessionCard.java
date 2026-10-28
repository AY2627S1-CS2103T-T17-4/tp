package seedu.hobbyhub.ui;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.hobbyhub.model.session.Session;

/**
 * A UI component that displays a summary of a {@code Session}.
 */
public class SessionCard extends UiPart<Region> {

    private static final String FXML = "SessionListCard.fxml";
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern(
            "EEE, d MMM yyyy 'at' h:mm a", Locale.ENGLISH);

    public final Session session;

    @FXML
    private HBox cardPane;
    @FXML
    private Label id;
    @FXML
    private Label name;
    @FXML
    private FlowPane sessionDetails;

    /**
     * Creates a {@code SessionCard} with the given {@code Session} and index to display.
     */
    public SessionCard(Session session, int displayedIndex) {
        super(FXML);
        this.session = session;
        id.setText(displayedIndex + ". ");
        name.setText(session.getName().fullName);
        addDetail(session.getDate().format(DATE_TIME_FORMAT), "session-date");
        addDetail(session.getLocation(), "session-location");
        addDetail(session.isCompleted() ? "Completed" : "Not completed", session.isCompleted()
                ? "session-status-complete" : "session-status-open");
        int count = session.getMembers().size();
        addDetail(count + (count == 1 ? " member" : " members"), "session-member-count");
    }

    private void addDetail(String value, String styleClass) {
        Label detail = new Label(value);
        detail.setWrapText(true);
        detail.setMaxWidth(360);
        detail.getStyleClass().addAll("session-tag", styleClass);
        sessionDetails.getChildren().add(detail);
    }
}
