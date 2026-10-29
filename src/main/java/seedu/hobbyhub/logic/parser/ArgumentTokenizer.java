package seedu.hobbyhub.logic.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import seedu.hobbyhub.logic.parser.exceptions.ParseException;

/**
 * Tokenizes arguments string of the form: {@code preamble <prefix>value <prefix>value ...}<br>
 *     e.g. {@code some preamble text t/ 11.00 t/12.00 k/ m/ July}  where prefixes are {@code t/ k/ m/}.<br>
 * 1. An argument's value can be an empty string e.g. the value of {@code k/} in the above example.<br>
 * 2. Leading and trailing whitespaces of an argument value will be discarded.<br>
 * 3. An argument may be repeated and all its values will be accumulated e.g. the value of {@code t/}
 *    in the above example.<br>
 * 4. Prefixes inside a quoted value, such as {@code -h "Chess -x"}, are not recognized. A quoted value opens with
 *    {@code "} at the start of a word or immediately after a prefix, and closes with the next {@code "} at the end
 *    of a word. A {@code "} without such a partner is kept as an ordinary character.<br>
 */
public class ArgumentTokenizer {

    /**
     * Tokenizes an arguments string and returns an {@code ArgumentMultimap} object that maps prefixes to their
     * respective argument values. Only the given prefixes will be recognized in the arguments string.
     *
     * @param argsString Arguments string of the form: {@code preamble <prefix>value <prefix>value ...}
     * @param prefixes   Prefixes to tokenize the arguments string with
     * @return           ArgumentMultimap object that maps prefixes to their arguments
     */
    public static ArgumentMultimap tokenize(String argsString, Prefix... prefixes) {
        List<PrefixPosition> positions = findAllPrefixPositions(argsString, false, prefixes);
        return extractArguments(argsString, positions);
    }

    /**
     * Tokenizes arguments, recognizing a prefix only when followed by whitespace or the end of the input.
     * For example, {@code -night} remains part of a value rather than being read as {@code -n}.
     */
    public static ArgumentMultimap tokenizeWithExactPrefixes(String argsString, Prefix... prefixes) {
        List<PrefixPosition> positions = findAllPrefixPositions(argsString, true, prefixes);
        return extractArguments(argsString, positions);
    }

    /**
     * Finds all zero-based prefix positions in the given arguments string.
     *
     * @param argsString Arguments string of the form: {@code preamble <prefix>value <prefix>value ...}
     * @param requireExactPrefixes whether each prefix must be followed by whitespace or the end of the input
     * @param prefixes   Prefixes to find in the arguments string
     * @return           List of zero-based prefix positions in the given arguments string
     */
private static List<PrefixPosition> findAllPrefixPositions(String argsString,
        boolean requireExactPrefixes, Prefix... prefixes) {
    boolean[] quoted = findQuotedCharacters(argsString, prefixes);
    return Arrays.stream(prefixes)
            .flatMap(prefix -> findPrefixPositions(
                    argsString, prefix, requireExactPrefixes, quoted).stream())
            .collect(Collectors.toList());
}

    /**
     * @see #findAllPrefixPositions(String, boolean, Prefix...)
     */
    private static List<PrefixPosition> findPrefixPositions(String argsString, Prefix prefix,
            boolean requireExactPrefixes, boolean[] quoted) {
        List<PrefixPosition> positions = new ArrayList<>();

        int prefixPosition = findPrefixPosition(argsString, prefix.getPrefix(), 0);
        while (prefixPosition != -1) {
            PrefixPosition extendedPrefix = new PrefixPosition(prefix, prefixPosition);
            int prefixEnd = prefixPosition + prefix.getPrefix().length();
            boolean isExactPrefix = prefixEnd == argsString.length()
                    || Character.isWhitespace(argsString.charAt(prefixEnd));
            if ((!requireExactPrefixes || isExactPrefix) && !quoted[prefixPosition]) {
                positions.add(extendedPrefix);
            }
            prefixPosition = findPrefixPosition(argsString, prefix.getPrefix(), prefixPosition);
        }

        return positions;
    }

    /**
     * Returns the index of the first occurrence of {@code prefix} in
     * {@code argsString} starting from index {@code fromIndex}. An occurrence
     * is valid if there is a whitespace before {@code prefix}. Returns -1 if no
     * such occurrence can be found.
     *
     * E.g if {@code argsString} = "e/hip/900", {@code prefix} = "p/" and
     * {@code fromIndex} = 0, this method returns -1 as there are no valid
     * occurrences of "p/" with whitespace before it. However, if
     * {@code argsString} = "e/hi p/900", {@code prefix} = "p/" and
     * {@code fromIndex} = 0, this method returns 5.
     */
    private static int findPrefixPosition(String argsString, String prefix, int fromIndex) {
        for (int i = fromIndex + 1; i < argsString.length(); i++) {
            if (Character.isWhitespace(argsString.charAt(i - 1)) && argsString.startsWith(prefix, i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Throws a {@code ParseException} with {@code message} if {@code argsString} contains an option that does not
     * start with one of {@code prefixes}, or that repeats its option letter, such as {@code -hh}.
     * An option is a {@code -} followed by a letter at the start of a word, outside a quoted value. Other uses of
     * {@code -}, such as {@code East - West} or a negative index, are ordinary text. Compact options such as
     * {@code -p91234567} are allowed.
     */
    public static void verifyNoUnknownOptions(String argsString, String message, Prefix... prefixes)
            throws ParseException {
        boolean[] quoted = findQuotedCharacters(argsString, prefixes);
        for (int i = 0; i < argsString.length(); i++) {
            if (quoted[i] || !isOptionStart(argsString, i)) {
                continue;
            }

            int optionEnd = i + 1;
            while (optionEnd < argsString.length() && Character.isLetter(argsString.charAt(optionEnd))) {
                optionEnd++;
            }
            String option = argsString.substring(i, optionEnd);
            boolean isSupported = Arrays.stream(prefixes).anyMatch(prefix -> option.startsWith(prefix.getPrefix()));
            if (!isSupported || isRepeatedOptionLetter(option)) {
                throw new ParseException(message);
            }
            i = optionEnd - 1;
        }
    }

    /** Returns whether the {@code -} at {@code index} starts a word and is followed by a letter. */
    private static boolean isOptionStart(String input, int index) {
        return input.charAt(index) == '-' && isAtWordStart(input, index)
                && index + 1 < input.length() && Character.isLetter(input.charAt(index + 1));
    }

    /** Returns whether a compact option repeats its option letter, such as {@code -hh}. */
    private static boolean isRepeatedOptionLetter(String option) {
        return option.length() == 3 && option.charAt(1) == option.charAt(2);
    }

    /**
     * Returns which characters of {@code input} belong to a quoted value, including the quotes themselves.
     * A quoted value opens with {@code "} at the start of a word or immediately after a recognized prefix and closes
     * with the next {@code "} at the end of a word. A {@code "} without such a partner, such as in
     * {@code Block 5 "Sunrise}, is an ordinary character.
     */
    private static boolean[] findQuotedCharacters(String input, Prefix... prefixes) {
        boolean[] quoted = new boolean[input.length() + 1];
        int i = 0;
        while (i < input.length()) {
            boolean isOpeningQuote = input.charAt(i) == '"'
                    && (isAtWordStart(input, i) || isImmediatelyAfterPrefix(input, i, prefixes));
            int closingQuote = isOpeningQuote
                    ? findClosingQuote(input, i + 1) : -1;
            if (closingQuote == -1) {
                i++;
                continue;
            }
            Arrays.fill(quoted, i, closingQuote + 1, true);
            i = closingQuote + 1;
        }
        return quoted;
    }

    /** Returns whether the character at {@code index} immediately follows a recognized prefix. */
    private static boolean isImmediatelyAfterPrefix(String input, int index, Prefix... prefixes) {
        return Arrays.stream(prefixes).anyMatch(prefix -> {
            int prefixStart = index - prefix.getPrefix().length();
            return prefixStart >= 0
                    && input.startsWith(prefix.getPrefix(), prefixStart)
                    && isAtWordStart(input, prefixStart);
        });
    }

    /** Returns the index of the first {@code "} from {@code fromIndex} that ends a word, or -1 if there is none. */
    private static int findClosingQuote(String input, int fromIndex) {
        for (int i = fromIndex; i < input.length(); i++) {
            if (input.charAt(i) == '"' && (i + 1 == input.length() || Character.isWhitespace(input.charAt(i + 1)))) {
                return i;
            }
        }
        return -1;
    }

    /** Returns whether the character at {@code index} begins a whitespace-delimited word. */
    private static boolean isAtWordStart(String input, int index) {
        return index == 0 || Character.isWhitespace(input.charAt(index - 1));
    }

    /**
     * Extracts prefixes and their argument values, and returns an {@code ArgumentMultimap} object that maps the
     * extracted prefixes to their respective arguments. Prefixes are extracted based on their zero-based positions in
     * {@code argsString}.
     *
     * @param argsString      Arguments string of the form: {@code preamble <prefix>value <prefix>value ...}
     * @param prefixPositions Zero-based positions of all prefixes in {@code argsString}
     * @return                ArgumentMultimap object that maps prefixes to their arguments
     */
    private static ArgumentMultimap extractArguments(String argsString, List<PrefixPosition> prefixPositions) {

        // Sort by start position
        prefixPositions.sort((prefix1, prefix2) -> prefix1.getStartPosition() - prefix2.getStartPosition());

        // Insert a PrefixPosition to represent the preamble
        PrefixPosition preambleMarker = new PrefixPosition(new Prefix(""), 0);
        prefixPositions.addFirst(preambleMarker);

        // Add a dummy PrefixPosition to represent the end of the string
        PrefixPosition endPositionMarker = new PrefixPosition(new Prefix(""), argsString.length());
        prefixPositions.add(endPositionMarker);

        // Map prefixes to their argument values (if any)
        ArgumentMultimap argMultimap = new ArgumentMultimap();
        for (int i = 0; i < prefixPositions.size() - 1; i++) {
            // Extract and store prefixes and their arguments
            Prefix argPrefix = prefixPositions.get(i).getPrefix();
            String argValue = extractArgumentValue(argsString, prefixPositions.get(i), prefixPositions.get(i + 1));
            argMultimap.put(argPrefix, argValue);
        }

        return argMultimap;
    }

    /**
     * Returns the trimmed value of the argument in the arguments string specified by {@code currentPrefixPosition}.
     * The end position of the value is determined by {@code nextPrefixPosition}.
     */
    private static String extractArgumentValue(String argsString,
                                        PrefixPosition currentPrefixPosition,
                                        PrefixPosition nextPrefixPosition) {
        Prefix prefix = currentPrefixPosition.getPrefix();

        int valueStartPos = currentPrefixPosition.getStartPosition() + prefix.getPrefix().length();
        String value = argsString.substring(valueStartPos, nextPrefixPosition.getStartPosition());

        return value.strip();
    }

    /**
     * Represents a prefix's position in an arguments string.
     */
    private static class PrefixPosition {
        private int startPosition;
        private final Prefix prefix;

        PrefixPosition(Prefix prefix, int startPosition) {
            this.prefix = prefix;
            this.startPosition = startPosition;
        }

        int getStartPosition() {
            return startPosition;
        }

        Prefix getPrefix() {
            return prefix;
        }
    }

}
