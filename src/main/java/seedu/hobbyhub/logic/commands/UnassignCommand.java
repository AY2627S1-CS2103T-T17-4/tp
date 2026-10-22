package seedu.hobbyhub.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.commons.util.ToStringBuilder;
import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.logic.commands.exceptions.CommandException;
import seedu.hobbyhub.model.ListState;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.session.Session;
import seedu.hobbyhub.model.session.SessionName;

/** Unassigns a member from a session using the displayed member list. */
public class UnassignCommand extends Command {

    public static final String COMMAND_WORD = "unassign";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Unassigns a member from a session.\n"
            + "Parameters: INDEX SESSION_NAME\n"
            + "Example: " + COMMAND_WORD + " 2 Weekend Chess Meetup";
    public static final String MESSAGE_MEMBER_LIST_REQUIRED =
            "The unassign command is only available while the member list is shown. Use list -m.";
    public static final String MESSAGE_SESSION_NOT_FOUND = "No session named \"%1$s\" exists.";
    public static final String MESSAGE_NOT_ASSIGNED = "Member is not assigned to session \"%1$s\".";
    public static final String MESSAGE_UNASSIGN_SUCCESS = "Unassigned %1$s from session \"%2$s\".";

    private final Index memberIndex;
    private final SessionName sessionName;

    /** Creates an unassign command for the member index and session name. */
    public UnassignCommand(Index memberIndex, SessionName sessionName) {
        requireNonNull(memberIndex);
        requireNonNull(sessionName);
        this.memberIndex = memberIndex;
        this.sessionName = sessionName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (model.getListState() != ListState.MEMBERS) {
            throw new CommandException(MESSAGE_MEMBER_LIST_REQUIRED);
        }

        List<Member> displayedMembers = model.getFilteredMemberList();
        if (memberIndex.getZeroBased() >= displayedMembers.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_MEMBER_DISPLAYED_INDEX);
        }

        Member member = displayedMembers.get(memberIndex.getZeroBased());
        Session session = model.getHobbyHub().getSessionList().stream()
                .filter(candidate -> candidate.getName().equals(sessionName))
                .findFirst()
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_SESSION_NOT_FOUND, sessionName)));
        if (!session.hasMember(member.getEmail())) {
            throw new CommandException(String.format(MESSAGE_NOT_ASSIGNED, sessionName));
        }

        model.setSession(session, session.withoutMember(member.getEmail()));
        return new CommandResult(String.format(MESSAGE_UNASSIGN_SUCCESS, member.getName(), sessionName));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UnassignCommand otherUnassignCommand)) {
            return false;
        }
        return memberIndex.equals(otherUnassignCommand.memberIndex)
                && sessionName.equals(otherUnassignCommand.sessionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberIndex.getZeroBased(), sessionName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("memberIndex", memberIndex)
                .add("sessionName", sessionName)
                .toString();
    }
}
