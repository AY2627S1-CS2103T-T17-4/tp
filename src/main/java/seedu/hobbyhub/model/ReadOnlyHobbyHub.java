package seedu.hobbyhub.model;

import javafx.collections.ObservableList;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.session.Session;

/**
 * Unmodifiable view of an HobbyHub
 */
public interface ReadOnlyHobbyHub {

    /**
     * Returns an unmodifiable view of the members list.
     * This list will not contain any duplicate members.
     */
    ObservableList<Member> getMemberList();

    /**
     * Returns an unmodifiable view of the sessions list.
     * This list will not contain any duplicate sessions.
     */
    ObservableList<Session> getSessionList();

}
