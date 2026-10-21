package seedu.hobbyhub.model.member;

/**
 * The supported skill levels for a member's hobby.
 */
public enum SkillLevel {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced");

    private final String label;

    SkillLevel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
