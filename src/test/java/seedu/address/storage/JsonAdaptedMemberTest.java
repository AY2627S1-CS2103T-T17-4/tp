package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedMember.DEFAULT_HOBBY;
import static seedu.address.storage.JsonAdaptedMember.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Hobby;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

public class JsonAdaptedMemberTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_HOBBY = " ";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().toString();
    private static final String VALID_ADDRESS = BENSON.getAddress().toString();
    private static final String VALID_HOBBY = BENSON.getHobby().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedMember person = new JsonAdaptedMember(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedMember person =
                new JsonAdaptedMember(INVALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_HOBBY, VALID_TAGS);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedMember person = new JsonAdaptedMember(null, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_HOBBY,
                VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedMember person =
                new JsonAdaptedMember(VALID_NAME, INVALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_HOBBY, VALID_TAGS);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedMember person = new JsonAdaptedMember(VALID_NAME, null, VALID_EMAIL, VALID_ADDRESS, VALID_HOBBY,
                VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedMember person =
                new JsonAdaptedMember(VALID_NAME, VALID_PHONE, INVALID_EMAIL, VALID_ADDRESS, VALID_HOBBY, VALID_TAGS);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedMember person = new JsonAdaptedMember(VALID_NAME, VALID_PHONE, null, VALID_ADDRESS, VALID_HOBBY,
                VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedMember person =
                new JsonAdaptedMember(VALID_NAME, VALID_PHONE, VALID_EMAIL, INVALID_ADDRESS, VALID_HOBBY, VALID_TAGS);
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullAddress_throwsIllegalValueException() {
        JsonAdaptedMember person = new JsonAdaptedMember(VALID_NAME, VALID_PHONE, VALID_EMAIL, null, VALID_HOBBY,
                VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidHobby_throwsIllegalValueException() {
        JsonAdaptedMember person =
                new JsonAdaptedMember(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, INVALID_HOBBY, VALID_TAGS);
        String expectedMessage = Hobby.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_missingHobby_usesDefaultHobby() throws Exception {
        JsonAdaptedMember person = new JsonAdaptedMember(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null,
                VALID_TAGS);
        assertEquals(DEFAULT_HOBBY, person.toModelType().getHobby().value);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedMember person =
                new JsonAdaptedMember(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_HOBBY, invalidTags);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

}
