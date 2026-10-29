package seedu.hobbyhub.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import seedu.hobbyhub.logic.parser.exceptions.ParseException;

/**
 * Contains utility methods for parsing optional member fields in the edit command, so that every optional field
 * is replaced and cleared by the same rules.
 * <ul>
 *     <li>A value replaces the field, after being validated by the field's own parser.</li>
 *     <li>A prefix given without a value clears the field, resetting it to its empty value.</li>
 * </ul>
 */
public class OptionalFieldUtil {

    /**
     * A parsing function that may throw a {@code ParseException}.
     *
     * @param <I> the type of the input to parse.
     * @param <O> the type of the parsed result.
     */
    @FunctionalInterface
    public interface ThrowingFunction<I, O> {
        /**
         * Parses {@code input} into the result.
         *
         * @throws ParseException if {@code input} is invalid.
         */
        O apply(I input) throws ParseException;
    }

    /**
     * Parses the value of a single-valued optional field.
     * A blank {@code value} clears the field and is parsed into {@code emptyValue}, whether or not it has trailing
     * whitespace. Any other value is parsed with {@code parser}.
     *
     * @param value the value that followed the field's prefix.
     * @param parser the parser of valid values of the field.
     * @param emptyValue the value of the field when it is cleared.
     * @throws ParseException if {@code value} is not blank and is rejected by {@code parser}.
     */
    public static <T> T parseScalar(String value, ThrowingFunction<String, T> parser, T emptyValue)
            throws ParseException {
        requireNonNull(value);
        requireNonNull(parser);
        requireNonNull(emptyValue);

        return value.isBlank() ? emptyValue : parser.apply(value);
    }

    /**
     * Parses the values of a multi-valued optional field, which are replaced as a whole.
     * Returns {@code Optional.empty()} if {@code values} is empty, which leaves the field unedited.
     * If {@code values} is a single blank value, the field is cleared and parsed as an empty collection.
     * Otherwise all values are parsed together with {@code parser}, so a blank value among other values is
     * rejected by the parser like any other invalid value.
     *
     * @param values the values that followed the field's prefix, one per occurrence of the prefix.
     * @param parser the parser of valid collections of values of the field.
     * @throws ParseException if {@code parser} rejects the values.
     */
    public static <T> Optional<Set<T>> parseSet(Collection<String> values,
            ThrowingFunction<Collection<String>, Set<T>> parser) throws ParseException {
        requireNonNull(values);
        requireNonNull(parser);

        if (values.isEmpty()) {
            return Optional.empty();
        }

        boolean isSingleBlankValue = values.size() == 1 && values.iterator().next().isBlank();
        Collection<String> valuesToParse = isSingleBlankValue ? Collections.emptySet() : values;
        return Optional.of(parser.apply(valuesToParse));
    }

}
