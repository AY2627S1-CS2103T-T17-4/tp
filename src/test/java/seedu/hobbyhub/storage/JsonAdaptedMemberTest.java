package seedu.hobbyhub.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.hobbyhub.storage.JsonAdaptedMember.DEFAULT_HOBBY;
import static seedu.hobbyhub.storage.JsonAdaptedMember.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.hobbyhub.testutil.Assert.assertThrows;
import static seedu.hobbyhub.testutil.TypicalMembers.BENSON;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.commons.exceptions.IllegalValueException;
import seedu.hobbyhub.model.member.Address;
import seedu.hobbyhub.model.member.Availability;
import seedu.hobbyhub.model.member.Email;
import seedu.hobbyhub.model.member.Hobby;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.member.Name;
import seedu.hobbyhub.model.member.Phone;
import seedu.hobbyhub.testutil.MemberBuilder;

public class JsonAdaptedMemberTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "++651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_HOBBY = " ";
    private static final String INVALID_GROUP = "#friend";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().toString();
    private static final String VALID_ADDRESS = BENSON.getAddress().toString();
    private static final String VALID_HOBBY = BENSON.getHobby().value;
    private static final List<JsonAdaptedGroup> VALID_GROUPS = BENSON.getGroups().stream()
            .map(JsonAdaptedGroup::new)
            .collect(Collectors.toList());

    @Test
    public void toModelType_validMemberDetails_returnsMember() throws Exception {
        JsonAdaptedMember member = new JsonAdaptedMember(BENSON);
        assertEquals(BENSON, member.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedMember member =
                new JsonAdaptedMember(INVALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_HOBBY, VALID_GROUPS);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, member::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedMember member = new JsonAdaptedMember(null, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_HOBBY,
                VALID_GROUPS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, member::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedMember member =
                new JsonAdaptedMember(VALID_NAME, INVALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_HOBBY, VALID_GROUPS);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, member::toModelType);
    }

    @Test
    public void toModelType_nullPhone_usesAbsentValue() throws Exception {
        JsonAdaptedMember member = new JsonAdaptedMember(VALID_NAME, null, VALID_EMAIL, VALID_ADDRESS, VALID_HOBBY,
                VALID_GROUPS);
        assertTrue(member.toModelType().getPhone().isEmpty());
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedMember member =
                new JsonAdaptedMember(VALID_NAME, VALID_PHONE, INVALID_EMAIL, VALID_ADDRESS, VALID_HOBBY, VALID_GROUPS);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, member::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedMember member = new JsonAdaptedMember(VALID_NAME, VALID_PHONE, null, VALID_ADDRESS, VALID_HOBBY,
                VALID_GROUPS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, member::toModelType);
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedMember member =
                new JsonAdaptedMember(VALID_NAME, VALID_PHONE, VALID_EMAIL, INVALID_ADDRESS, VALID_HOBBY, VALID_GROUPS);
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, member::toModelType);
    }

    @Test
    public void toModelType_nullAddress_usesAbsentValue() throws Exception {
        JsonAdaptedMember member = new JsonAdaptedMember(VALID_NAME, VALID_PHONE, VALID_EMAIL, null, VALID_HOBBY,
                VALID_GROUPS);
        assertTrue(member.toModelType().getAddress().isEmpty());
    }

    @Test
    public void toModelType_invalidHobby_throwsIllegalValueException() {
        JsonAdaptedMember member =
                new JsonAdaptedMember(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, INVALID_HOBBY, VALID_GROUPS);
        String expectedMessage = Hobby.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, member::toModelType);
    }

    @Test
    public void toModelType_hobbiesDifferingOnlyByCaseAndTrailingSpace_throwsIllegalValueException() {
        List<JsonAdaptedHobby> hobbies = List.of(new JsonAdaptedHobby("Chess", "Beginner"),
                new JsonAdaptedHobby("chess ", "Advanced"));
        JsonAdaptedMember member = new JsonAdaptedMember(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                null, hobbies, VALID_GROUPS, null);
        assertThrows(IllegalValueException.class, Member.MESSAGE_DUPLICATE_HOBBY, member::toModelType);
    }

    @Test
    public void toModelType_missingHobby_usesDefaultHobby() throws Exception {
        JsonAdaptedMember member = new JsonAdaptedMember(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null,
                VALID_GROUPS);
        assertEquals(DEFAULT_HOBBY, member.toModelType().getHobby().value);
    }

    @Test
    public void toModelType_availability_returnsMemberWithAvailability() throws Exception {
        JsonAdaptedMember member = new JsonAdaptedMember(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_HOBBY, null, VALID_GROUPS, null, List.of("MONDAY", "saturday"));

        assertEquals(new Availability(List.of(DayOfWeek.MONDAY, DayOfWeek.SATURDAY)),
                member.toModelType().getAvailability());
    }

    @Test
    public void constructor_memberWithAvailability_roundTripsAvailability() throws Exception {
        Member member = new MemberBuilder(BENSON)
                .withAvailability(DayOfWeek.MONDAY, DayOfWeek.SATURDAY).build();

        assertEquals(member, new JsonAdaptedMember(member).toModelType());
    }

    @Test
    public void toModelType_invalidAvailability_throwsIllegalValueException() {
        JsonAdaptedMember member = new JsonAdaptedMember(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_HOBBY, null, VALID_GROUPS, null, List.of("Funday"));
        assertThrows(IllegalValueException.class, Availability.MESSAGE_CONSTRAINTS, member::toModelType);
    }

    @Test
    public void toModelType_invalidGroups_throwsIllegalValueException() {
        List<JsonAdaptedGroup> invalidGroups = new ArrayList<>(VALID_GROUPS);
        invalidGroups.add(new JsonAdaptedGroup(INVALID_GROUP));
        JsonAdaptedMember member =
                new JsonAdaptedMember(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_HOBBY, invalidGroups);
        assertThrows(IllegalValueException.class, member::toModelType);
    }

}
