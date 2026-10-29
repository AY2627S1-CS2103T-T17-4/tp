package seedu.hobbyhub.logic.parser;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.commons.util.StringUtil;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.model.group.Group;
import seedu.hobbyhub.model.member.Address;
import seedu.hobbyhub.model.member.Email;
import seedu.hobbyhub.model.member.Hobby;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.member.Name;
import seedu.hobbyhub.model.member.Phone;
import seedu.hobbyhub.model.member.SkillLevel;
import seedu.hobbyhub.model.session.SessionName;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_HOBBY_FORMAT =
            "Use -h \"HOBBY:SKILL\", with one hobby name and one skill level.";
    public static final String MESSAGE_INVALID_SKILL = "Skill level must be Beginner, Intermediate, or Advanced.";
    public static final String MESSAGE_INVALID_SESSION_LOCATION = "Session location cannot be blank.";
    public static final String MESSAGE_INVALID_SESSION_DATE_TIME =
            "Session date-time must look like 2026-10-24 14:00 or 2026-10-24T14:00 (year-month-day hour:minute).";

    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";

    private static final List<DateTimeFormatter> SESSION_DATE_TIME_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm").withResolverStyle(ResolverStyle.STRICT));

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a session name after removing surrounding whitespace and an optional pair of double quotes.
     *
     * @throws ParseException if the given name is invalid.
     */
    public static SessionName parseSessionName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (trimmedName.length() >= 2 && trimmedName.startsWith("\"") && trimmedName.endsWith("\"")) {
            trimmedName = trimmedName.substring(1, trimmedName.length() - 1).trim();
        }
        if (!SessionName.isValidSessionName(trimmedName)) {
            throw new ParseException(SessionName.MESSAGE_CONSTRAINTS);
        }
        return new SessionName(trimmedName);
    }

    /**
     * Parses a session location and returns it after trimming surrounding whitespace.
     *
     * @throws ParseException if the location is blank.
     */
    public static String parseSessionLocation(String location) throws ParseException {
        requireNonNull(location);
        String trimmedLocation = location.trim();
        if (trimmedLocation.isBlank()) {
            throw new ParseException(MESSAGE_INVALID_SESSION_LOCATION);
        }
        return trimmedLocation;
    }

    /**
     * Parses a date-time in either {@code yyyy-MM-dd HH:mm} or {@code yyyy-MM-ddTHH:mm} format
     * into a {@code LocalDateTime}. Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the date-time is invalid.
     */
    public static LocalDateTime parseSessionDateTime(String dateTime) throws ParseException {
        requireNonNull(dateTime);
        String trimmedDateTime = dateTime.trim();
        for (DateTimeFormatter formatter : SESSION_DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(trimmedDateTime, formatter);
            } catch (DateTimeParseException e) {
                // Try the next accepted format.
            }
        }
        throw new ParseException(MESSAGE_INVALID_SESSION_DATE_TIME);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (trimmedPhone.isEmpty() || !Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (trimmedAddress.isEmpty() || !Address.isValidAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a hobby name with an optional skill suffix, e.g. {@code "Table Tennis:Advanced"}.
     * Balanced outer double quotes and surrounding whitespace are removed.
     * Legacy names without a colon default to Beginner; an explicit suffix must be valid.
     */
    public static Hobby parseHobby(String hobby) throws ParseException {
        requireNonNull(hobby);
        String value = hobby.trim();
        if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
            value = value.substring(1, value.length() - 1).trim();
        }
        if (value.contains("\"")) {
            throw new ParseException(MESSAGE_INVALID_HOBBY_FORMAT);
        }
        String[] parts = value.split(":", -1);
        if (parts.length > 2) {
            throw new ParseException(MESSAGE_INVALID_HOBBY_FORMAT);
        }
        String name = parts[0].trim();
        if (!Hobby.isValidHobby(name)) {
            throw new ParseException(Hobby.MESSAGE_CONSTRAINTS);
        }
        if (parts.length == 1) {
            return new Hobby(name);
        }
        for (SkillLevel level : SkillLevel.values()) {
            if (level.toString().equalsIgnoreCase(parts[1].trim())) {
                return new Hobby(name, level);
            }
        }
        throw new ParseException(MESSAGE_INVALID_SKILL);
    }

    /** Parses all hobby options in input order, rejecting repeated hobby names regardless of skill level. */
    public static List<Hobby> parseHobbies(Collection<String> hobbies) throws ParseException {
        requireNonNull(hobbies);
        if (hobbies.isEmpty()) {
            throw new ParseException(Member.MESSAGE_HOBBIES_REQUIRED);
        }
        List<Hobby> parsed = new ArrayList<>();
        Set<String> names = new HashSet<>();
        for (String value : hobbies) {
            Hobby hobby = parseHobby(value);
            if (!names.add(hobby.value.toLowerCase(Locale.ROOT))) {
                throw new ParseException(Member.MESSAGE_DUPLICATE_HOBBY);
            }
            parsed.add(hobby);
        }
        return List.copyOf(parsed);
    }

    /**
     * Parses a {@code String group} into a {@code Group}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code group} is invalid.
     */
    public static Group parseGroup(String group) throws ParseException {
        requireNonNull(group);
        String trimmedGroup = group.trim();
        if (trimmedGroup.startsWith("\"") && trimmedGroup.endsWith("\"") && trimmedGroup.length() >= 2) {
            trimmedGroup = trimmedGroup.substring(1, trimmedGroup.length() - 1).trim();
        }
        if (!Group.isValidGroupName(trimmedGroup)) {
            throw new ParseException(Group.MESSAGE_CONSTRAINTS);
        }
        return new Group(trimmedGroup);
    }

    /**
     * Parses {@code Collection<String> groups} into a {@code Set<Group>}.
     */
    public static Set<Group> parseGroups(Collection<String> groups) throws ParseException {
        requireNonNull(groups);
        final Set<Group> groupSet = new HashSet<>();
        for (String groupName : groups) {
            groupSet.add(parseGroup(groupName));
        }
        return groupSet;
    }
}
