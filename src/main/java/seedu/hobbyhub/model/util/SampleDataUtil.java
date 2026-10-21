package seedu.hobbyhub.model.util;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.hobbyhub.model.HobbyHub;
import seedu.hobbyhub.model.ReadOnlyHobbyHub;
import seedu.hobbyhub.model.group.Group;
import seedu.hobbyhub.model.member.Address;
import seedu.hobbyhub.model.member.Email;
import seedu.hobbyhub.model.member.Hobby;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.member.Name;
import seedu.hobbyhub.model.member.Phone;
import seedu.hobbyhub.model.session.Session;
import seedu.hobbyhub.model.session.SessionName;

/**
 * Contains utility methods for populating {@code HobbyHub} with sample data.
 */
public class SampleDataUtil {
    public static Member[] getSampleMembers() {
        return new Member[] {
            new Member(new Name("Alex Yeoh"), new Phone("87438807"), new Email("alexyeoh@example.com"),
                new Address("Blk 30 Geylang Street 29, #06-40"), new Hobby("Chess"),
                getGroupSet("friends")),
            new Member(new Name("Bernice Yu"), new Phone("99272758"), new Email("berniceyu@example.com"),
                new Address("Blk 30 Lorong 3 Serangoon Gardens, #07-18"), new Hobby("Photography"),
                getGroupSet("colleagues", "friends")),
            new Member(new Name("Charlotte Oliveiro"), new Phone("93210283"), new Email("charlotte@example.com"),
                new Address("Blk 11 Ang Mo Kio Street 74, #11-04"), new Hobby("Cycling"),
                getGroupSet("neighbours")),
            new Member(new Name("David Li"), new Phone("91031282"), new Email("lidavid@example.com"),
                new Address("Blk 436 Serangoon Gardens Street 26, #16-43"), new Hobby("Cooking"),
                getGroupSet("family")),
            new Member(new Name("Irfan Ibrahim"), new Phone("92492021"), new Email("irfan@example.com"),
                new Address("Blk 47 Tampines Street 20, #17-35"), new Hobby("Hiking"),
                getGroupSet("classmates")),
            new Member(new Name("Roy Balakrishnan"), new Phone("92624417"), new Email("royb@example.com"),
                new Address("Blk 45 Aljunied Street 85, #11-31"), new Hobby("Gardening"),
                getGroupSet("colleagues"))
        };
    }

    public static Session[] getSampleSessions() {
        return new Session[] {
            new Session(new SessionName("Weekend Chess Meetup"), LocalDateTime.of(2026, 10, 24, 14, 0),
                "Community Center", false, getEmailSet("alexyeoh@example.com", "berniceyu@example.com")),
            new Session(new SessionName("Sunday Hike"), LocalDateTime.of(2026, 10, 25, 9, 0),
                "MacRitchie Reservoir", false,
                getEmailSet("charlotte@example.com", "irfan@example.com", "royb@example.com"))
        };
    }

    public static ReadOnlyHobbyHub getSampleHobbyHub() {
        HobbyHub sampleAb = new HobbyHub();
        for (Member sampleMember : getSampleMembers()) {
            sampleAb.addMember(sampleMember);
        }
        for (Session sampleSession : getSampleSessions()) {
            sampleAb.addSession(sampleSession);
        }
        return sampleAb;
    }

    /**
     * Returns a group set containing the list of strings given.
     */
    public static Set<Group> getGroupSet(String... strings) {
        return Arrays.stream(strings)
                .map(Group::new)
                .collect(Collectors.toSet());
    }

    /**
     * Returns an email set containing the list of strings given.
     */
    public static Set<Email> getEmailSet(String... strings) {
        return Arrays.stream(strings)
                .map(Email::new)
                .collect(Collectors.toSet());
    }

}
