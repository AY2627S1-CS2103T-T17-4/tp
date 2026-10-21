package seedu.hobbyhub.logic.parser;

import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;

import seedu.hobbyhub.logic.parser.exceptions.ParseException;

/**
 * A single search option and its non-empty query. Options must be separated from their values by whitespace.
 */
public class SearchArgument {
    private final Prefix prefix;
    private final String query;

    private SearchArgument(Prefix prefix, String query) {
        this.prefix = prefix;
        this.query = query;
    }

    /**
     * Parses exactly one supported option, rejecting additional options outside quoted text.
     */
    public static SearchArgument parse(String args, String usage, Prefix... supported) throws ParseException {
        String[] parts = args.trim().split("\\s+", 2);
        Prefix prefix = new Prefix(parts[0]);
        if (parts.length != 2 || !Arrays.asList(supported).contains(prefix)
                || containsOption(parts[1])) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage));
        }
        String query = parts[1].trim();
        if (query.length() >= 2 && query.startsWith("\"") && query.endsWith("\"")) {
            query = query.substring(1, query.length() - 1).trim();
        }
        if (query.isBlank()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage));
        }
        return new SearchArgument(prefix, query);
    }

    private static boolean containsOption(String query) {
        boolean insideQuotes = false;
        for (int i = 0; i < query.length(); i++) {
            char character = query.charAt(i);
            if (character == '"') {
                insideQuotes = !insideQuotes;
            } else if (!insideQuotes && character == '-'
                    && (i == 0 || Character.isWhitespace(query.charAt(i - 1)))) {
                return true;
            }
        }
        return false;
    }

    public Prefix getPrefix() {
        return prefix;
    }

    public String getQuery() {
        return query;
    }
}
