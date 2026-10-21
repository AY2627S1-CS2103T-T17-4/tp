package seedu.hobbyhub.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.commons.util.ToStringBuilder;
import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.logic.commands.exceptions.CommandException;
import seedu.hobbyhub.model.ListState;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.session.Session;
import seedu.hobbyhub.model.session.SessionName;

/** Assigns a member from the displayed member list to a session. */
public class AssignCommand extends Command {

    public static final String COMMAND_WORD = "assign";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Assigns a member to a session.\n"
            + "Parameters: INDEX SESSION_NAME\n"
            + "Example: " + COMMAND_WORD + " 1 Weekend Chess Meetup";
    public static final String MESSAGE_MEMBER_LIST_REQUIRED =
            "The assign command is only available while the member list is shown. Use list -m.";
    public static final String MESSAGE_SESSION_NOT_FOUND = "No session named \"%1$s\" exists.";
    public static final String MESSAGE_ALREADY_ASSIGNED = "Member is already assigned to session \"%1$s\".";
    public static final String MESSAGE_ASSIGN_SUCCESS = "Assigned %1$s to session \"%2$s\".";

    private final Index memberIndex;
    private final SessionName sessionName;

    /** Creates an assign command for the member index and session name. */
    public AssignCommand(Index memberIndex, SessionName sessionName) {
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
        if (session.hasMember(member.getEmail())) {
            throw new CommandException(String.format(MESSAGE_ALREADY_ASSIGNED, sessionName));
        }

        model.setSession(session, session.withMember(member.getEmail()));
        return new CommandResult(String.format(MESSAGE_ASSIGN_SUCCESS, member.getName(), sessionName));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AssignCommand otherAssignCommand)) {
            return false;
        }
        return memberIndex.equals(otherAssignCommand.memberIndex)
                && sessionName.equals(otherAssignCommand.sessionName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("memberIndex", memberIndex)
                .add("sessionName", sessionName)
                .toString();
    }
}
