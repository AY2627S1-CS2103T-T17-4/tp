package seedu.hobbyhub.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_HOBBY;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.hobbyhub.model.Model.PREDICATE_SHOW_ALL_MEMBERS;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.commons.util.CollectionUtil;
import seedu.hobbyhub.commons.util.ToStringBuilder;
import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.logic.commands.exceptions.CommandException;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.member.Address;
import seedu.hobbyhub.model.member.Email;
import seedu.hobbyhub.model.member.Hobby;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.member.Name;
import seedu.hobbyhub.model.member.Phone;
import seedu.hobbyhub.model.tag.Tag;

/**
 * Edits the details of an existing member in the HobbyHub.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the member identified "
            + "by the index number used in the displayed member list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "[" + PREFIX_HOBBY + "HOBBY]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_MEMBER_SUCCESS = "Edited member: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_MEMBER = "This member already exists in the HobbyHub.";

    private final Index index;
    private final EditMemberDescriptor editMemberDescriptor;

    /**
     * @param index of the member in the filtered member list to edit
     * @param editMemberDescriptor details to edit the member with
     */
    public EditCommand(Index index, EditMemberDescriptor editMemberDescriptor) {
        requireNonNull(index);
        requireNonNull(editMemberDescriptor);

        this.index = index;
        this.editMemberDescriptor = new EditMemberDescriptor(editMemberDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Member> lastShownList = model.getFilteredMemberList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_MEMBER_DISPLAYED_INDEX);
        }

        Member memberToEdit = lastShownList.get(index.getZeroBased());
        Member editedMember = editMemberDescriptor.createEditedMember(memberToEdit);

        if (!memberToEdit.isSameMember(editedMember) && model.hasMember(editedMember)) {
            throw new CommandException(MESSAGE_DUPLICATE_MEMBER);
        }

        model.setMember(memberToEdit, editedMember);
        model.updateFilteredMemberList(PREDICATE_SHOW_ALL_MEMBERS);
        return new CommandResult(String.format(MESSAGE_EDIT_MEMBER_SUCCESS, Messages.format(editedMember)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand otherEditCommand)) {
            return false;
        }

        return index.equals(otherEditCommand.index)
                && editMemberDescriptor.equals(otherEditCommand.editMemberDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editMemberDescriptor", editMemberDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the member with. Each non-empty field value will replace the
     * corresponding field value of the member.
     */
    public static class EditMemberDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Set<Tag> tags;
        private Hobby hobby;

        public EditMemberDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditMemberDescriptor(EditMemberDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setTags(toCopy.tags);
            setHobby(toCopy.hobby);
        }

        /**
         * Creates and returns a {@code Member} with the details of {@code memberToEdit}
         * edited with this descriptor's fields.
         */
        public Member createEditedMember(Member memberToEdit) {
            requireNonNull(memberToEdit);

            Name updatedName = getName().orElse(memberToEdit.getName());
            Phone updatedPhone = getPhone().orElse(memberToEdit.getPhone());
            Email updatedEmail = getEmail().orElse(memberToEdit.getEmail());
            Address updatedAddress = getAddress().orElse(memberToEdit.getAddress());
            Hobby updatedHobby = getHobby().orElse(memberToEdit.getHobby());
            Set<Tag> updatedTags = getTags().orElse(memberToEdit.getTags());

            return new Member(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedHobby, updatedTags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, tags, hobby);
        }

        public EditMemberDescriptor setName(Name name) {
            this.name = name;
            return this;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public EditMemberDescriptor setPhone(Phone phone) {
            this.phone = phone;
            return this;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public EditMemberDescriptor setEmail(Email email) {
            this.email = email;
            return this;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public EditMemberDescriptor setAddress(Address address) {
            this.address = address;
            return this;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditMemberDescriptor setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
            return this;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        public Optional<Hobby> getHobby() {
            return Optional.ofNullable(hobby);
        }

        public EditMemberDescriptor setHobby(Hobby hobby) {
            this.hobby = hobby;
            return this;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditMemberDescriptor otherEditMemberDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditMemberDescriptor.name)
                    && Objects.equals(phone, otherEditMemberDescriptor.phone)
                    && Objects.equals(email, otherEditMemberDescriptor.email)
                    && Objects.equals(address, otherEditMemberDescriptor.address)
                    && Objects.equals(tags, otherEditMemberDescriptor.tags)
                    && Objects.equals(hobby, otherEditMemberDescriptor.hobby);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("tags", tags)
                    .add("hobby", hobby)
                    .toString();
        }
    }
}
