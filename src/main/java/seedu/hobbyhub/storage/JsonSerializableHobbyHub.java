package seedu.hobbyhub.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.hobbyhub.commons.core.LogsCenter;
import seedu.hobbyhub.commons.exceptions.IllegalValueException;
import seedu.hobbyhub.model.HobbyHub;
import seedu.hobbyhub.model.ReadOnlyHobbyHub;
import seedu.hobbyhub.model.member.Email;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.session.Session;

/**
 * An Immutable HobbyHub that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableHobbyHub {

    public static final String MESSAGE_DUPLICATE_MEMBER = "Members list contains duplicate member(s).";
    public static final String MESSAGE_DUPLICATE_SESSION = "Sessions list contains duplicate session(s).";

    private static final Logger logger = LogsCenter.getLogger(JsonSerializableHobbyHub.class);

    @JsonProperty("persons")
    private final List<JsonAdaptedMember> members = new ArrayList<>();

    @JsonProperty("sessions")
    private final List<JsonAdaptedSession> sessions = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableHobbyHub} with the given members and sessions.
     * A missing {@code sessions} list (e.g. in data files from older versions) is treated as empty.
     */
    @JsonCreator
    public JsonSerializableHobbyHub(@JsonProperty("persons") List<JsonAdaptedMember> members,
            @JsonProperty("sessions") List<JsonAdaptedSession> sessions) {
        this.members.addAll(members);
        if (sessions != null) {
            this.sessions.addAll(sessions);
        }
    }

    /**
     * Converts a given {@code ReadOnlyHobbyHub} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableHobbyHub}.
     */
    public JsonSerializableHobbyHub(ReadOnlyHobbyHub source) {
        members.addAll(source.getMemberList().stream().map(JsonAdaptedMember::new).collect(Collectors.toList()));
        sessions.addAll(source.getSessionList().stream().map(JsonAdaptedSession::new).collect(Collectors.toList()));
    }

    /**
     * Converts this HobbyHub into the model's {@code HobbyHub} object.
     * Emails in a session that do not belong to any member are dropped.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public HobbyHub toModelType() throws IllegalValueException {
        HobbyHub hobbyHub = new HobbyHub();
        for (JsonAdaptedMember jsonAdaptedMember : members) {
            Member member = jsonAdaptedMember.toModelType();
            if (hobbyHub.hasMember(member)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_MEMBER);
            }
            hobbyHub.addMember(member);
        }

        Set<Email> memberEmails = hobbyHub.getMemberList().stream()
                .map(Member::getEmail)
                .collect(Collectors.toSet());
        for (JsonAdaptedSession jsonAdaptedSession : sessions) {
            Session session = removeUnknownMembers(jsonAdaptedSession.toModelType(), memberEmails);
            if (hobbyHub.hasSession(session)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_SESSION);
            }
            hobbyHub.addSession(session);
        }
        return hobbyHub;
    }

    /**
     * Returns a copy of {@code session} without the emails that are not in {@code memberEmails}.
     */
    private static Session removeUnknownMembers(Session session, Set<Email> memberEmails) {
        Session result = session;
        for (Email email : session.getMembers()) {
            if (!memberEmails.contains(email)) {
                logger.warning("Session " + session.getName() + " refers to unknown member " + email
                        + ". The member is removed from the session.");
                result = result.withoutMember(email);
            }
        }
        assert result.getMembers().stream().allMatch(memberEmails::contains);
        return result;
    }

}
