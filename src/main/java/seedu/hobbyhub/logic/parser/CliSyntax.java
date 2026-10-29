package seedu.hobbyhub.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_NAME = new Prefix("-n");
    public static final Prefix PREFIX_LOCATION = new Prefix("-l");
    public static final Prefix PREFIX_DATETIME = new Prefix("-t");
    public static final Prefix PREFIX_PHONE = new Prefix("-p");
    public static final Prefix PREFIX_EMAIL = new Prefix("-c");
    public static final Prefix PREFIX_ADDRESS = new Prefix("-a");
    public static final Prefix PREFIX_HOBBY = new Prefix("-h");
    public static final Prefix PREFIX_GROUP = new Prefix("-g");
    public static final Prefix PREFIX_AVAILABILITY = new Prefix("-d");

    /** Recognized only to reject the replaced tag option with a helpful error. */
    public static final Prefix PREFIX_LEGACY_TAG = new Prefix("-t");

    /* Search-specific prefix definitions */
    public static final Prefix PREFIX_SEARCH_NAME = new Prefix("-n");
    public static final Prefix PREFIX_SEARCH_HOBBY = new Prefix("-h");
    public static final Prefix PREFIX_SEARCH_SKILL = new Prefix("-sl");
    public static final Prefix PREFIX_SEARCH_COMPLETED = new Prefix("-done");

}
