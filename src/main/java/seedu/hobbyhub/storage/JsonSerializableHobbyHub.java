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
import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * A HobbyHub that is serializable to JSON format.
 * Invalid members and sessions are discarded when converting to the model, instead of failing the whole conversion.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableHobbyHub {

    public static final String MESSAGE_DUPLICATE_MEMBER = "Members list contains duplicate member(s).";
    public static final String MESSAGE_DUPLICATE_SESSION = "Sessions list contains duplicate session(s).";

    /** Name of the JSON field holding the members. */
    static final String MEMBERS_FIELD = "persons";
    /** Name of the JSON field holding the sessions. */
    static final String SESSIONS_FIELD = "sessions";

    private static final Logger logger = LogsCenter.getLogger(JsonSerializableHobbyHub.class);

    @JsonProperty(MEMBERS_FIELD)
    private final List<JsonAdaptedMember> members = new ArrayList<>();

    @JsonProperty(SESSIONS_FIELD)
    private final List<JsonAdaptedSession> sessions = new ArrayList<>();

    @JsonIgnore
    private int discardedCount;

    /**
     * Constructs a {@code JsonSerializableHobbyHub} with the given members and sessions.
     * A missing {@code members} or {@code sessions} list (e.g. in data files from older versions) is treated
     * as empty.
     */
    @JsonCreator
    public JsonSerializableHobbyHub(@JsonProperty(MEMBERS_FIELD) List<JsonAdaptedMember> members,
            @JsonProperty(SESSIONS_FIELD) List<JsonAdaptedSession> sessions) {
        if (members != null) {
            this.members.addAll(members);
        }
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
     * Returns the number of members and sessions discarded by the latest call to {@link #toModelType()}.
     */
    @JsonIgnore
    public int getDiscardedCount() {
        return discardedCount;
    }

    /**
     * Converts this HobbyHub into the model's {@code HobbyHub} object.
     * Members and sessions that violate data constraints, or duplicate an earlier one, are discarded and logged.
     * Emails in a session are matched to members ignoring case, and emails that match no member are dropped.
     */
    public HobbyHub toModelType() {
        discardedCount = 0;
        HobbyHub hobbyHub = new HobbyHub();
        addMembers(hobbyHub);
        addSessions(hobbyHub);
        return hobbyHub;
    }

    /**
     * Adds every valid, non-duplicate member in {@code members} to {@code hobbyHub}.
     */
    private void addMembers(HobbyHub hobbyHub) {
        for (JsonAdaptedMember adaptedMember : members) {
            try {
                Member member = adaptedMember.toModelType();
                if (hobbyHub.hasMember(member)) {
                    discard("member", MESSAGE_DUPLICATE_MEMBER);
                } else {
                    hobbyHub.addMember(member);
                }
            } catch (IllegalValueException ive) {
                discard("member", ive.getMessage());
            }
        }
    }

    /**
     * Adds every valid, non-duplicate session in {@code sessions} to {@code hobbyHub}.
     * Must be called after {@link #addMembers(HobbyHub)}, as sessions refer to members by email.
     */
    private void addSessions(HobbyHub hobbyHub) {
        Map<String, Email> memberEmails = new HashMap<>();
        for (Member member : hobbyHub.getMemberList()) {
            memberEmails.putIfAbsent(member.getEmail().value.toLowerCase(), member.getEmail());
        }

        for (JsonAdaptedSession adaptedSession : sessions) {
            try {
                Session session = resolveMembers(adaptedSession.toModelType(), memberEmails);
                if (hobbyHub.hasSession(session)) {
                    discard("session", MESSAGE_DUPLICATE_SESSION);
                } else {
                    hobbyHub.addSession(session);
                }
            } catch (IllegalValueException ive) {
                discard("session", ive.getMessage());
            }
        }
    }

    /**
     * Records and logs that an entry of type {@code entryType} was discarded because of {@code reason}.
     */
    private void discard(String entryType, String reason) {
        discardedCount++;
        logger.warning("Discarding " + entryType + ": " + reason);
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
        return new Session(session.getName(), session.getDate(), session.getLocation(), session.isCompleted(),
                resolved);
    }

}
