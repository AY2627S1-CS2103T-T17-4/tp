package seedu.hobbyhub.logic.parser;

import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_AVAILABILITY;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_SEARCH_HOBBY;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_SEARCH_NAME;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_SEARCH_SKILL;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import seedu.hobbyhub.logic.commands.SearchCommand;
import seedu.hobbyhub.logic.parser.exceptions.ParseException;
import seedu.hobbyhub.model.AllMatchPredicate;
import seedu.hobbyhub.model.member.HobbyAndSkillPredicate;
import seedu.hobbyhub.model.member.HobbyContainsKeywordsPredicate;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.member.MemberSearchPredicate;
import seedu.hobbyhub.model.member.MemberSearchPredicate.Field;
import seedu.hobbyhub.model.member.NameContainsKeywordsPredicate;
import seedu.hobbyhub.model.member.SkillLevel;

/**
 * Parses member search criteria combined with AND, tying hobby and skill to the same hobby.
 */
public class SearchCommandParser implements Parser<SearchCommand> {
    @Override
    public SearchCommand parse(String args) throws ParseException {
        List<SearchArgument> arguments = SearchArgument.parse(args, SearchCommand.MESSAGE_USAGE,
                PREFIX_SEARCH_NAME, PREFIX_SEARCH_HOBBY, PREFIX_GROUP, PREFIX_EMAIL, PREFIX_SEARCH_SKILL,
                PREFIX_AVAILABILITY);
        Map<Prefix, String> queries = arguments.stream()
                .collect(Collectors.toMap(SearchArgument::getPrefix, SearchArgument::getQuery));
        String hobby = queries.get(PREFIX_SEARCH_HOBBY);
        String skill = queries.get(PREFIX_SEARCH_SKILL);
        boolean pairedHobbyAndSkill = hobby != null && skill != null;
        List<Predicate<Member>> predicates = new ArrayList<>();
        if (pairedHobbyAndSkill) {
            predicates.add(new HobbyAndSkillPredicate(hobby, parseSkill(skill)));
        }
        for (SearchArgument argument : arguments) {
            if (pairedHobbyAndSkill && (argument.getPrefix().equals(PREFIX_SEARCH_HOBBY)
                    || argument.getPrefix().equals(PREFIX_SEARCH_SKILL))) {
                continue;
            }
            predicates.add(parsePredicate(argument));
        }
        return new SearchCommand(predicates.size() == 1 ? predicates.getFirst() : new AllMatchPredicate<>(predicates));
    }

    private Predicate<Member> parsePredicate(SearchArgument argument) throws ParseException {
        Prefix prefix = argument.getPrefix();
        String query = argument.getQuery();
        if (prefix.equals(PREFIX_SEARCH_NAME)) {
            return new NameContainsKeywordsPredicate(List.of(query));
        }
        if (prefix.equals(PREFIX_SEARCH_HOBBY)) {
            return new HobbyContainsKeywordsPredicate(List.of(query));
        }
        if (prefix.equals(PREFIX_GROUP)) {
            return new MemberSearchPredicate(Field.GROUP, query);
        }
        if (prefix.equals(PREFIX_EMAIL)) {
            return new MemberSearchPredicate(Field.EMAIL, ParserUtil.parseEmail(query).value);
        }
        if (prefix.equals(PREFIX_AVAILABILITY)) {
            return new MemberSearchPredicate(Field.AVAILABILITY, ParserUtil.parseAvailabilityDay(query).name());
        }
        return new MemberSearchPredicate(Field.SKILL_LEVEL, parseSkill(query).toString());
    }

    private SkillLevel parseSkill(String query) throws ParseException {
        try {
            return SkillLevel.valueOf(query.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ParseException("Skill level must be Beginner, Intermediate, or Advanced.");
        }
    }
}
