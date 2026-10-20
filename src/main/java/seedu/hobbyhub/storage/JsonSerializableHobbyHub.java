package seedu.hobbyhub.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
     * Emails in a session are matched to members ignoring case, and emails that match no member are dropped.
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

        Map<String, Email> memberEmails = new HashMap<>();
        for (Member member : hobbyHub.getMemberList()) {
            memberEmails.putIfAbsent(member.getEmail().value.toLowerCase(), member.getEmail());
        }
        for (JsonAdaptedSession jsonAdaptedSession : sessions) {
            Session session = resolveMembers(jsonAdaptedSession.toModelType(), memberEmails);
            if (hobbyHub.hasSession(session)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_SESSION);
            }
            hobbyHub.addSession(session);
        }
        return hobbyHub;
    }

    /**
     * Returns a copy of {@code session} whose emails are those of the matching members, compared ignoring case.
     * Emails that match no member are dropped.
     *
     * @param memberEmails the members' emails, keyed by the email in lower case.
     */
    private static Session resolveMembers(Session session, Map<String, Email> memberEmails) {
        Set<Email> resolved = new HashSet<>();
        for (Email email : session.getMembers()) {
            Email memberEmail = memberEmails.get(email.value.toLowerCase());
            if (memberEmail == null) {
                logger.warning("Session " + session.getName() + " refers to unknown member " + email
                        + ". The member is removed from the session.");
            } else {
                resolved.add(memberEmail);
            }
        }
        assert memberEmails.values().containsAll(resolved);
        return new Session(session.getName(), session.getDate(), resolved);
    }

}
