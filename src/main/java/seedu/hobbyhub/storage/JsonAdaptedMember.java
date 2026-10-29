package seedu.hobbyhub.storage;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import seedu.hobbyhub.commons.exceptions.IllegalValueException;
import seedu.hobbyhub.model.group.Group;
import seedu.hobbyhub.model.member.Address;
import seedu.hobbyhub.model.member.Availability;
import seedu.hobbyhub.model.member.Email;
import seedu.hobbyhub.model.member.Hobby;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.member.Name;
import seedu.hobbyhub.model.member.Phone;

/**
 * Jackson-friendly version of {@link Member}.
 */
class JsonAdaptedMember {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Member's %s field is missing!";
    public static final String DEFAULT_HOBBY = "Unspecified";

    private final String name;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final String phone;
    private final String email;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final String address;
    @JsonProperty(access = Access.WRITE_ONLY)
    private final String hobby;
    private final List<JsonAdaptedHobby> hobbies;
    private final List<JsonAdaptedGroup> groups = new ArrayList<>();
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<String> availability = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedMember} with the given member details.
     */
    public JsonAdaptedMember(String name, String phone, String email, String address,
            String hobby, List<JsonAdaptedGroup> groups) {
        this(name, phone, email, address, hobby, null, groups, null, null);
    }

    /** Constructs a member without an explicit availability list. */
    public JsonAdaptedMember(String name, String phone, String email, String address,
            String hobby, List<JsonAdaptedHobby> hobbies, List<JsonAdaptedGroup> groups,
            List<JsonAdaptedGroup> legacyTags) {
        this(name, phone, email, address, hobby, hobbies, groups, legacyTags, null);
    }

    /** Constructs a member from current and legacy JSON fields. */
    @JsonCreator
    public JsonAdaptedMember(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("address") String address,
            @JsonProperty("hobby") String hobby, @JsonProperty("hobbies") List<JsonAdaptedHobby> hobbies,
            @JsonProperty("groups") List<JsonAdaptedGroup> groups,
            @JsonProperty("tags") List<JsonAdaptedGroup> legacyTags,
            @JsonProperty("availability") List<String> availability) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.hobby = hobby;
        this.hobbies = hobbies;
        if (groups != null) {
            this.groups.addAll(groups);
        }
        if (legacyTags != null) {
            this.groups.addAll(legacyTags);
        }
        if (availability != null) {
            this.availability.addAll(availability);
        }
    }

    /**
     * Converts a given {@code Member} into this class for Jackson use.
     */
    public JsonAdaptedMember(Member source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        address = source.getAddress().value;
        hobby = null;
        hobbies = source.getHobbies().stream().map(JsonAdaptedHobby::new).toList();
        source.getAvailability().getDays().stream()
                .sorted()
                .map(DayOfWeek::name)
                .forEach(availability::add);
        groups.addAll(source.getGroups().stream()
                .map(JsonAdaptedGroup::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted member object into the model's {@code Member} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted member.
     */
    public Member toModelType() throws IllegalValueException {
        final List<Group> memberGroups = new ArrayList<>();
        for (JsonAdaptedGroup group : groups) {
            if (group == null) {
                throw new IllegalValueException(Group.MESSAGE_CONSTRAINTS);
            }
            memberGroups.add(group.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        String phoneValue = phone == null ? "" : phone;
        if (!Phone.isValidPhone(phoneValue)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phoneValue);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        String addressValue = address == null ? "" : address;
        if (!Address.isValidAddress(addressValue)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = new Address(addressValue);

        final List<Hobby> modelHobbies = new ArrayList<>();
        if (hobbies == null) {
            // Legacy files have one hobby string (or none); retain it at Beginner level.
            String hobbyValue = hobby == null ? DEFAULT_HOBBY : hobby;
            if (!Hobby.isValidHobby(hobbyValue)) {
                throw new IllegalValueException(Hobby.MESSAGE_CONSTRAINTS);
            }
            modelHobbies.add(new Hobby(hobbyValue));
        } else {
            if (hobbies.isEmpty()) {
                throw new IllegalValueException(Member.MESSAGE_HOBBIES_REQUIRED);
            }
            for (JsonAdaptedHobby adapted : hobbies) {
                if (adapted == null) {
                    throw new IllegalValueException(Hobby.MESSAGE_CONSTRAINTS);
                }
                Hobby parsed = adapted.toModelType();
                String parsedName = parsed.value.trim();
                if (modelHobbies.stream().anyMatch(existing -> existing.value.trim().equalsIgnoreCase(parsedName))) {
                    throw new IllegalValueException(Member.MESSAGE_DUPLICATE_HOBBY);
                }
                modelHobbies.add(parsed);
            }
        }

        final Set<Group> modelGroups = new HashSet<>(memberGroups);
        final Set<DayOfWeek> modelAvailability = EnumSet.noneOf(DayOfWeek.class);
        for (String day : availability) {
            if (day == null) {
                throw new IllegalValueException(Availability.MESSAGE_CONSTRAINTS);
            }
            try {
                modelAvailability.add(DayOfWeek.valueOf(day.trim().toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException e) {
                throw new IllegalValueException(Availability.MESSAGE_CONSTRAINTS);
            }
        }
        return new Member(modelName, modelPhone, modelEmail, modelAddress, modelHobbies, modelGroups,
                new Availability(modelAvailability));
    }

}
