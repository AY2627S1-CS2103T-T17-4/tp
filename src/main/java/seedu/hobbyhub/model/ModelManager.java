package seedu.hobbyhub.model;

import static java.util.Objects.requireNonNull;
import static seedu.hobbyhub.commons.util.CollectionUtil.requireAllNonNull;

import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.hobbyhub.commons.core.GuiSettings;
import seedu.hobbyhub.commons.core.LogsCenter;
import seedu.hobbyhub.model.member.Member;

/**
 * Represents the in-memory model of the HobbyHub data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final HobbyHub hobbyHub;
    private final UserPrefs userPrefs;
    private final FilteredList<Member> filteredMembers;

    /**
     * Initializes a ModelManager with the given hobbyHub and userPrefs.
     */
    public ModelManager(ReadOnlyHobbyHub hobbyHub, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(hobbyHub, userPrefs);

        logger.fine("Initializing with HobbyHub: " + hobbyHub + " and user prefs " + userPrefs);

        this.hobbyHub = new HobbyHub(hobbyHub);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredMembers = new FilteredList<>(this.hobbyHub.getMemberList());
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
                && filteredMembers.equals(otherModelManager.filteredMembers);
    }

}
