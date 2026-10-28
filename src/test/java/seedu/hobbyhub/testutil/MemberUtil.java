package seedu.hobbyhub.testutil;

import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_AVAILABILITY;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_HOBBY;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.Set;

import seedu.hobbyhub.logic.commands.AddCommand;
import seedu.hobbyhub.logic.commands.EditCommand.EditMemberDescriptor;
import seedu.hobbyhub.model.group.Group;
import seedu.hobbyhub.model.member.Member;

/**
 * A utility class for Member.
 */
public class MemberUtil {

    /**
     * Returns an add command string for adding the {@code member}.
     */
    public static String getAddCommand(Member member) {
        return AddCommand.COMMAND_WORD + " " + getMemberDetails(member);
    }

    /**
     * Returns the part of command string for the given {@code member}'s details.
     */
    public static String getMemberDetails(Member member) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME + member.getName().fullName + " ");
        if (!member.getPhone().isEmpty()) {
            sb.append(PREFIX_PHONE + member.getPhone().value + " ");
        }
        sb.append(PREFIX_EMAIL + member.getEmail().value + " ");
        if (!member.getAddress().isEmpty()) {
            sb.append(PREFIX_ADDRESS + member.getAddress().value + " ");
        }
        member.getHobbies().forEach(hobby -> sb.append(PREFIX_HOBBY).append(" \"")
                .append(hobby.value).append(":").append(hobby.getSkillLevel()).append("\" "));
        member.getGroups().stream().forEach(
            s -> sb.append(PREFIX_GROUP + s.groupName + " ")
        );
        member.getAvailability().getDays().forEach(
            day -> sb.append(PREFIX_AVAILABILITY).append(day.name()).append(" ")
        );
        return sb.toString();
    }

    /**
     * Returns the part of command string for the given {@code EditMemberDescriptor}'s details.
     */
    public static String getEditMemberDescriptorDetails(EditMemberDescriptor descriptor) {
        StringBuilder sb = new StringBuilder();
        descriptor.getName().ifPresent(name -> sb.append(PREFIX_NAME).append(name.fullName).append(" "));
        descriptor.getPhone().ifPresent(phone -> sb.append(PREFIX_PHONE).append(phone.value).append(" "));
        descriptor.getEmail().ifPresent(email -> sb.append(PREFIX_EMAIL).append(email.value).append(" "));
        descriptor.getAddress().ifPresent(address -> sb.append(PREFIX_ADDRESS).append(address.value).append(" "));
        descriptor.getHobbies().ifPresent(hobbies -> hobbies.forEach(hobby -> sb.append(PREFIX_HOBBY)
                .append(" \"").append(hobby.value).append(":").append(hobby.getSkillLevel()).append("\" ")));
        if (descriptor.getGroups().isPresent()) {
            Set<Group> groups = descriptor.getGroups().get();
            if (groups.isEmpty()) {
                sb.append(PREFIX_GROUP).append(" ");
            } else {
                groups.forEach(s -> sb.append(PREFIX_GROUP).append(s.groupName).append(" "));
            }
        }
        descriptor.getAvailability().ifPresent(availability -> {
            if (availability.isEmpty()) {
                sb.append(PREFIX_AVAILABILITY).append(" ");
            } else {
                availability.getDays().forEach(day -> sb.append(PREFIX_AVAILABILITY)
                        .append(day.name()).append(" "));
            }
        });
        return sb.toString();
    }
}
