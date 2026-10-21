package seedu.hobbyhub.logic.parser;

import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_SEARCH_HOBBY;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_SEARCH_NAME;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_SEARCH_SKILL;

import java.util.List;
import java.util.Locale;

import seedu.hobbyhub.logic.commands.SearchCommand;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.model.member.HobbyContainsKeywordsPredicate;
import seedu.hobbyhub.model.member.MemberSearchPredicate;
import seedu.hobbyhub.model.member.MemberSearchPredicate.Field;
import seedu.hobbyhub.model.member.NameContainsKeywordsPredicate;
import seedu.hobbyhub.model.member.SkillLevel;

/**
 * Parses a single member search criterion.
 */
public class SearchCommandParser implements Parser<SearchCommand> {
    @Override
    public SearchCommand parse(String args) throws ParseException {
        SearchArgument argument = SearchArgument.parse(args, SearchCommand.MESSAGE_USAGE,
                PREFIX_SEARCH_NAME, PREFIX_SEARCH_HOBBY, PREFIX_GROUP, PREFIX_EMAIL, PREFIX_SEARCH_SKILL);
        Prefix prefix = argument.getPrefix();
        String query = argument.getQuery();
        if (prefix.equals(PREFIX_SEARCH_NAME)) {
            return new SearchCommand(new NameContainsKeywordsPredicate(List.of(query)));
        }
        if (prefix.equals(PREFIX_SEARCH_HOBBY)) {
            return new SearchCommand(new HobbyContainsKeywordsPredicate(List.of(query)));
        }
        if (prefix.equals(PREFIX_GROUP)) {
            return new SearchCommand(new MemberSearchPredicate(Field.GROUP, query));
        }
        if (prefix.equals(PREFIX_EMAIL)) {
            return new SearchCommand(new MemberSearchPredicate(Field.EMAIL, ParserUtil.parseEmail(query).value));
        }
        try {
            SkillLevel skill = SkillLevel.valueOf(query.toUpperCase(Locale.ROOT));
            return new SearchCommand(new MemberSearchPredicate(Field.SKILL_LEVEL, skill.toString()));
        } catch (IllegalArgumentException e) {
            throw new ParseException("Skill level must be Beginner, Intermediate, or Advanced.");
        }
    }
}
