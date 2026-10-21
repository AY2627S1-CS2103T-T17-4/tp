package seedu.hobbyhub.logic.parser;

import static seedu.hobbyhub.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.hobbyhub.logic.parser.exceptions.ParseException;

/**
 * A search option and its non-empty query. Options must be separated from their values by whitespace.
 */
public class SearchArgument {
    private final Prefix prefix;
    private final String query;

    private SearchArgument(Prefix prefix, String query) {
        this.prefix = prefix;
        this.query = query;
    }

    /**
     * Parses one or more distinct supported options, ignoring option-like text inside double quotes.
     * Returns the arguments in prefix order so equivalent searches have a consistent representation.
     */
    public static List<SearchArgument> parse(String args, String usage, Prefix... supported) throws ParseException {
        String input = args.trim();
        if (!input.startsWith("-")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage));
        }
        List<SearchArgument> arguments = new ArrayList<>();
        Set<Prefix> seen = new HashSet<>();
        boolean insideQuotes = false;
        int start = 0;
        for (int i = 0; i < input.length(); i++) {
            char character = input.charAt(i);
            if (character == '"') {
                insideQuotes = !insideQuotes;
            } else if (!insideQuotes && character == '-' && i > 0
                    && Character.isWhitespace(input.charAt(i - 1))) {
                arguments.add(parseArgument(input.substring(start, i), usage, seen, supported));
                start = i;
            }
        }
        arguments.add(parseArgument(input.substring(start), usage, seen, supported));
        return arguments.stream().sorted(Comparator.comparing(argument -> argument.prefix.getPrefix())).toList();
    }

    private static SearchArgument parseArgument(String args, String usage, Set<Prefix> seen, Prefix... supported)
            throws ParseException {
        String[] parts = args.trim().split("\\s+", 2);
        Prefix prefix = new Prefix(parts[0]);
        if (parts.length != 2 || !Arrays.asList(supported).contains(prefix)
                || !seen.add(prefix)) {
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

    public Prefix getPrefix() {
        return prefix;
    }

    public String getQuery() {
        return query;
    }
}
