package seedu.hobbyhub.testutil;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.hobbyhub.logic.commands.EditCommand.EditMemberDescriptor;
import seedu.hobbyhub.model.group.Group;
import seedu.hobbyhub.model.member.Address;
import seedu.hobbyhub.model.member.Availability;
import seedu.hobbyhub.model.member.Email;
import seedu.hobbyhub.model.member.Hobby;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.member.Name;
import seedu.hobbyhub.model.member.Phone;

/**
 * A utility class to help with building EditMemberDescriptor objects.
 */
public class EditMemberDescriptorBuilder {

    private EditMemberDescriptor descriptor;

    public EditMemberDescriptorBuilder() {
        descriptor = new EditMemberDescriptor();
    }

    public EditMemberDescriptorBuilder(EditMemberDescriptor descriptor) {
        this.descriptor = new EditMemberDescriptor(descriptor);
    }

    /**
     * Returns an {@code EditMemberDescriptor} with fields containing {@code member}'s details
     */
    public EditMemberDescriptorBuilder(Member member) {
        descriptor = new EditMemberDescriptor();
        descriptor.setName(member.getName());
        descriptor.setPhone(member.getPhone());
        descriptor.setEmail(member.getEmail());
        descriptor.setAddress(member.getAddress());
        descriptor.setGroups(member.getGroups());
        descriptor.setHobbies(member.getHobbies());
        descriptor.setAvailability(member.getAvailability());
    }

    /**
     * Sets the {@code Name} of the {@code EditMemberDescriptor} that we are building.
     */
    public EditMemberDescriptorBuilder withName(String name) {
        descriptor.setName(new Name(name));
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code EditMemberDescriptor} that we are building.
     */
    public EditMemberDescriptorBuilder withPhone(String phone) {
        descriptor.setPhone(new Phone(phone));
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code EditMemberDescriptor} that we are building.
     */
    public EditMemberDescriptorBuilder withEmail(String email) {
        descriptor.setEmail(new Email(email));
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code EditMemberDescriptor} that we are building.
     */
    public EditMemberDescriptorBuilder withAddress(String address) {
        descriptor.setAddress(new Address(address));
        return this;
    }

    /**
     * Sets the {@code Hobby} of the {@code EditMemberDescriptor} that we are building.
     */
    public EditMemberDescriptorBuilder withHobby(String hobby) {
        descriptor.setHobby(new Hobby(hobby));
        return this;
    }

    /**
     * Parses the {@code groups} into a {@code Set<Group>} and sets it to the {@code EditMemberDescriptor}
     * that we are building.
     */
    public EditMemberDescriptorBuilder withGroups(String... groups) {
        Set<Group> groupSet = Stream.of(groups).map(Group::new).collect(Collectors.toSet());
        descriptor.setGroups(groupSet);
        return this;
    }

    /** Sets the complete hobby list to replace the member's hobbies. */
    public EditMemberDescriptorBuilder withHobbies(Hobby... hobbies) {
        descriptor.setHobbies(List.of(hobbies));
        return this;
    }

    /** Sets the availability to the supplied weekdays. */
    public EditMemberDescriptorBuilder withAvailability(DayOfWeek... days) {
        descriptor.setAvailability(new Availability(Arrays.asList(days)));
        return this;
    }

    public EditMemberDescriptor build() {
        return descriptor;
    }
}
