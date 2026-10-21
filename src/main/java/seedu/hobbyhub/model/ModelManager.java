package seedu.hobbyhub.model;

import static java.util.Objects.requireNonNull;
import static seedu.hobbyhub.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.hobbyhub.commons.core.GuiSettings;
import seedu.hobbyhub.commons.core.LogsCenter;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.session.Session;

/**
 * Represents the in-memory model of the HobbyHub data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final HobbyHub hobbyHub;
    private final UserPrefs userPrefs;
    private final FilteredList<Member> filteredMembers;
    private final FilteredList<Session> filteredSessions;
    private ListState listState = ListState.MEMBERS;

    /**
     * Initializes a ModelManager with the given hobbyHub and userPrefs.
     */
    public ModelManager(ReadOnlyHobbyHub hobbyHub, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(hobbyHub, userPrefs);

        logger.fine("Initializing with HobbyHub: " + hobbyHub + " and user prefs " + userPrefs);

        this.hobbyHub = new HobbyHub(hobbyHub);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredMembers = new FilteredList<>(this.hobbyHub.getMemberList());
        filteredSessions = new FilteredList<>(this.hobbyHub.getSessionList());
    }

    public ModelManager() {
        this(new HobbyHub(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== HobbyHub ================================================================================

    @Override
    public void setHobbyHub(ReadOnlyHobbyHub hobbyHub) {
        this.hobbyHub.resetData(hobbyHub);
    }

    @Override
    public ReadOnlyHobbyHub getHobbyHub() {
        return hobbyHub;
    }

    @Override
    public boolean hasMember(Member member) {
        requireNonNull(member);
        return hobbyHub.hasMember(member);
    }

    @Override
    public void deleteMember(Member target) {
        hobbyHub.removeMember(target);
    }

    @Override
    public void addMember(Member member) {
        hobbyHub.addMember(member);
        updateFilteredMemberList(PREDICATE_SHOW_ALL_MEMBERS);
    }

    @Override
    public void setMember(Member target, Member editedMember) {
        requireAllNonNull(target, editedMember);

        hobbyHub.setMember(target, editedMember);
    }

    //=========== Filtered Member List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Member} backed by the internal list of
     * {@code hobbyHub}
     */
    @Override
    public ObservableList<Member> getFilteredMemberList() {
        return filteredMembers;
    }

    @Override
    public void updateFilteredMemberList(Predicate<Member> predicate) {
        requireNonNull(predicate);
        filteredMembers.setPredicate(predicate);
    }

    //=========== Sessions ===================================================================================

    @Override
    public boolean hasSession(Session session) {
        requireNonNull(session);
        return hobbyHub.hasSession(session);
    }

    @Override
    public void deleteSession(Session target) {
        hobbyHub.removeSession(target);
        logger.fine("Deleted session: " + target.getName());
    }

    @Override
    public void addSession(Session session) {
        hobbyHub.addSession(session);
        updateFilteredSessionList(PREDICATE_SHOW_ALL_SESSIONS);
        logger.fine("Added session: " + session.getName());
    }

    @Override
    public void setSession(Session target, Session editedSession) {
        requireAllNonNull(target, editedSession);

        hobbyHub.setSession(target, editedSession);
    }

    @Override
    public List<Session> getSessionsOf(Member member) {
        requireNonNull(member);
        return hobbyHub.getSessionList().stream()
                .filter(session -> session.hasMember(member.getEmail()))
                .toList();
    }

    //=========== Filtered Session List Accessors ============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Session} backed by the internal list of
     * {@code hobbyHub}
     */
    @Override
    public ObservableList<Session> getFilteredSessionList() {
        return filteredSessions;
    }

    @Override
    public void updateFilteredSessionList(Predicate<Session> predicate) {
        requireNonNull(predicate);
        filteredSessions.setPredicate(predicate);
    }

    //=========== List State =================================================================================

    @Override
    public ListState getListState() {
        return listState;
    }

    @Override
    public void setListState(ListState listState) {
        requireNonNull(listState);
        logger.fine("Switching to the " + listState + " list");
        this.listState = listState;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return hobbyHub.equals(otherModelManager.hobbyHub)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredMembers.equals(otherModelManager.filteredMembers)
                && filteredSessions.equals(otherModelManager.filteredSessions)
                && listState == otherModelManager.listState;
    }

}
