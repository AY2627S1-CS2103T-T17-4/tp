package seedu.hobbyhub.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.hobbyhub.logic.parser.OptionalFieldUtil.ThrowingFunction;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;

/**
 * Tests {@code OptionalFieldUtil}.
 */
public class OptionalFieldUtilTest {

    private static final String EMPTY_VALUE = "empty";
    private static final String MESSAGE_REJECTED = "rejected";

    /** Upper-cases values, standing in for a field parser that accepts everything. */
    private static final ThrowingFunction<String, String> UPPER_CASE_PARSER = String::toUpperCase;

    /** Rejects every value, standing in for a field parser that must not be reached. */
    private static final ThrowingFunction<String, String> REJECTING_PARSER = value -> {
        throw new ParseException(MESSAGE_REJECTED);
    };

    /** Copies values into a set, rejecting any blank value like a real field parser would. */
    private static final ThrowingFunction<Collection<String>, Set<String>> SET_PARSER = values -> {
        if (values.stream().anyMatch(String::isBlank)) {
            throw new ParseException(MESSAGE_REJECTED);
        }
        return new HashSet<>(values);
    };

    @Test
    public void constructor_succeeds() {
        OptionalFieldUtil util = new OptionalFieldUtil();
        assertNotNull(util);
    }

    @Test
    public void parseScalar_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, ()
                -> OptionalFieldUtil.parseScalar(null, UPPER_CASE_PARSER, EMPTY_VALUE));
        assertThrows(NullPointerException.class, () -> OptionalFieldUtil.parseScalar("a", null, EMPTY_VALUE));
        assertThrows(NullPointerException.class, ()
                -> OptionalFieldUtil.parseScalar("a", UPPER_CASE_PARSER, null));
    }

    @Test
    public void parseScalar_blankValue_returnsEmptyValueWithoutParsing() throws Exception {
        assertEquals(EMPTY_VALUE, OptionalFieldUtil.parseScalar("", REJECTING_PARSER, EMPTY_VALUE));
        assertEquals(EMPTY_VALUE, OptionalFieldUtil.parseScalar(" ", REJECTING_PARSER, EMPTY_VALUE));
        assertEquals(EMPTY_VALUE, OptionalFieldUtil.parseScalar("\t  ", REJECTING_PARSER, EMPTY_VALUE));
    }

    @Test
    public void parseScalar_nonBlankValue_returnsParsedValue() throws Exception {
        assertEquals("ABC", OptionalFieldUtil.parseScalar("abc", UPPER_CASE_PARSER, EMPTY_VALUE));
    }

    @Test
    public void parseScalar_invalidValue_propagatesParseException() {
        ParseException exception = assertThrows(ParseException.class, ()
                -> OptionalFieldUtil.parseScalar("abc", REJECTING_PARSER, EMPTY_VALUE));

        assertEquals(MESSAGE_REJECTED, exception.getMessage());
    }

    @Test
    public void parseSet_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> OptionalFieldUtil.parseSet(null, SET_PARSER));
        assertThrows(NullPointerException.class, () -> OptionalFieldUtil.parseSet(List.of("a"), null));
    }

    @Test
    public void parseSet_noValues_returnsEmptyOptional() throws Exception {
        assertEquals(Optional.empty(), OptionalFieldUtil.parseSet(List.of(), SET_PARSER));
    }

    @Test
    public void parseSet_singleBlankValue_returnsEmptySet() throws Exception {
        assertEquals(Optional.of(Set.of()), OptionalFieldUtil.parseSet(List.of(""), SET_PARSER));
        assertEquals(Optional.of(Set.of()), OptionalFieldUtil.parseSet(List.of("  "), SET_PARSER));
    }

    @Test
    public void parseSet_singleValue_returnsParsedSet() throws Exception {
        assertEquals(Optional.of(Set.of("a")), OptionalFieldUtil.parseSet(List.of("a"), SET_PARSER));
    }

    @Test
    public void parseSet_multipleValues_returnsWholeParsedSet() throws Exception {
        assertEquals(Optional.of(Set.of("a", "b")), OptionalFieldUtil.parseSet(List.of("a", "b"), SET_PARSER));
    }

    @Test
    public void parseSet_blankValueAmongOthers_propagatesParseException() {
        ParseException exception = assertThrows(ParseException.class, ()
                -> OptionalFieldUtil.parseSet(List.of("", "a"), SET_PARSER));

        assertEquals(MESSAGE_REJECTED, exception.getMessage());
    }

    @Test
    public void parseSet_multipleBlankValues_propagatesParseException() {
        assertThrows(ParseException.class, () -> OptionalFieldUtil.parseSet(List.of("", ""), SET_PARSER));
    }

}
