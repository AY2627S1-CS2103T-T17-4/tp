package seedu.hobbyhub.model;

import java.util.List;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.hobbyhub.commons.core.GuiSettings;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.session.Session;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Member> PREDICATE_SHOW_ALL_MEMBERS = unused -> true;

    /** {@code Predicate} that always evaluates to true */
    Predicate<Session> PREDICATE_SHOW_ALL_SESSIONS = unused -> true;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces HobbyHub data with the data in {@code hobbyHub}.
     */
    void setHobbyHub(ReadOnlyHobbyHub hobbyHub);

    /** Returns the HobbyHub */
    ReadOnlyHobbyHub getHobbyHub();

    /**
     * Returns true if a member with the same identity as {@code member} exists in the HobbyHub.
     */
    boolean hasMember(Member member);

    /**
     * Deletes the given member.
     * The member must exist in the HobbyHub.
     */
    void deleteMember(Member target);

    /**
     * Adds the given member.
     * {@code member} must not already exist in the HobbyHub.
     */
    void addMember(Member member);

    /**
     * Replaces the given member {@code target} with {@code editedMember}.
     * {@code target} must exist in the HobbyHub.
     * The member identity of {@code editedMember} must not be the same as another existing member in the HobbyHub.
     */
    void setMember(Member target, Member editedMember);

    /** Returns an unmodifiable view of the filtered member list */
    ObservableList<Member> getFilteredMemberList();

    /**
     * Updates the filter of the filtered member list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredMemberList(Predicate<Member> predicate);

    /**
     * Returns true if a session with the same identity as {@code session} exists in the HobbyHub.
     */
    boolean hasSession(Session session);

    /**
     * Deletes the given session.
     * The session must exist in the HobbyHub.
     */
    void deleteSession(Session target);

    /**
     * Adds the given session.
     * {@code session} must not already exist in the HobbyHub.
     */
    void addSession(Session session);

    /**
     * Replaces the given session {@code target} with {@code editedSession}.
     * {@code target} must exist in the HobbyHub.
     * The session identity of {@code editedSession} must not be the same as another existing session in the HobbyHub.
     */
    void setSession(Session target, Session editedSession);

    /** Returns an unmodifiable view of the filtered session list */
    ObservableList<Session> getFilteredSessionList();

    /**
     * Updates the filter of the filtered session list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredSessionList(Predicate<Session> predicate);

    /**
     * Returns the sessions that the given member is assigned to.
     */
    List<Session> getSessionsOf(Member member);

    /**
     * Returns the list currently shown to the user.
     */
    ListState getListState();

    /**
     * Sets the list shown to the user.
     * @throws NullPointerException if {@code listState} is null.
     */
    void setListState(ListState listState);
}
