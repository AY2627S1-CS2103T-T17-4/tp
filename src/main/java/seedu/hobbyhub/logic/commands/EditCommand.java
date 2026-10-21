package seedu.hobbyhub.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_GROUP;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_HOBBY;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.hobbyhub.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.hobbyhub.model.Model.PREDICATE_SHOW_ALL_MEMBERS;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.hobbyhub.commons.core.index.Index;
import seedu.hobbyhub.commons.util.CollectionUtil;
import seedu.hobbyhub.commons.util.ToStringBuilder;
import seedu.hobbyhub.logic.Messages;
import seedu.hobbyhub.logic.commands.exceptions.CommandException;
import seedu.hobbyhub.model.Model;
import seedu.hobbyhub.model.group.Group;
import seedu.hobbyhub.model.member.Address;
import seedu.hobbyhub.model.member.Email;
import seedu.hobbyhub.model.member.Hobby;
import seedu.hobbyhub.model.member.Member;
import seedu.hobbyhub.model.member.Name;
import seedu.hobbyhub.model.member.Phone;

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
            + "[" + PREFIX_GROUP + "GROUP]...\n"
            + "[" + PREFIX_HOBBY + " \"HOBBY:SKILL\"]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_MEMBER_SUCCESS = "Edited member: %1$s";
    public static final String MESSAGE_HOBBIES_REPLACED =
            "Hobby list replaced. Only the supplied hobbies and skill levels were kept.";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_MEMBER = "A member with this email already exists.";

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
        String feedback = String.format(MESSAGE_EDIT_MEMBER_SUCCESS, Messages.format(editedMember));
        if (!memberToEdit.getHobbies().equals(editedMember.getHobbies())) {
            feedback += "\n" + MESSAGE_HOBBIES_REPLACED;
        }
        return new CommandResult(feedback);
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
        private Set<Group> groups;
        private List<Hobby> hobbies;

        public EditMemberDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code groups} is used internally.
         */
        public EditMemberDescriptor(EditMemberDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setGroups(toCopy.groups);
            setHobbies(toCopy.hobbies);
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
            List<Hobby> updatedHobbies = getHobbies().orElse(memberToEdit.getHobbies());
            Set<Group> updatedGroups = getGroups().orElse(memberToEdit.getGroups());

            return new Member(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedHobbies, updatedGroups);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, groups, hobbies);
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
         * Sets {@code groups} to this object's {@code groups}.
         * A defensive copy of {@code groups} is used internally.
         */
        public EditMemberDescriptor setGroups(Set<Group> groups) {
            this.groups = (groups != null) ? new HashSet<>(groups) : null;
            return this;
        }

        /**
         * Returns an unmodifiable group set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code groups} is null.
         */
        public Optional<Set<Group>> getGroups() {
            return (groups != null) ? Optional.of(Collections.unmodifiableSet(groups)) : Optional.empty();
        }

        /** Returns the first supplied hobby for compatibility with single-hobby callers. */
        public Optional<Hobby> getHobby() {
            return getHobbies().map(List::getFirst);
        }

        /** Sets one hobby, replacing all previously supplied hobbies. */
        public EditMemberDescriptor setHobby(Hobby hobby) {
            return setHobbies(hobby == null ? null : List.of(hobby));
        }

        /** Returns all supplied hobbies as an immutable list, or empty if this field is not edited. */
        public Optional<List<Hobby>> getHobbies() {
            return Optional.ofNullable(hobbies);
        }

        /** Copies the hobbies to apply. A null list leaves the field unedited. */
        public EditMemberDescriptor setHobbies(List<Hobby> hobbies) {
            if (hobbies != null) {
                // Reuse the member's list invariants, without requiring a partially constructed member.
                if (hobbies.isEmpty()) {
                    throw new IllegalArgumentException(Member.MESSAGE_HOBBIES_REQUIRED);
                }
                Set<String> names = new HashSet<>();
                for (Hobby hobby : hobbies) {
                    requireNonNull(hobby);
                    if (!names.add(hobby.value.trim().toLowerCase(Locale.ROOT))) {
                        throw new IllegalArgumentException(Member.MESSAGE_DUPLICATE_HOBBY);
                    }
                }
            }
            this.hobbies = hobbies == null ? null : List.copyOf(hobbies);
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
                    && Objects.equals(groups, otherEditMemberDescriptor.groups)
                    && Objects.equals(hobbies, otherEditMemberDescriptor.hobbies);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("groups", groups)
                    .add("hobbies", hobbies)
                    .toString();
        }
    }
}
