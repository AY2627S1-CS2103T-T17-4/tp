package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;

/**
 * Marks a person identified by their name as a favourite.
 */
public class FavCommand extends Command {

    public static final String COMMAND_WORD = "fav";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks the person with the specified name as a favourite. "
            + "Parameters: n/NAME\n"
            + "Example: " + COMMAND_WORD + " n/John Doe";

    public static final String MESSAGE_FAV_PERSON_SUCCESS = "Marked as favourite: %1$s";
    public static final String MESSAGE_PERSON_NOT_FOUND = "No person named '%1$s' found.";

    private final Name targetName;

    public FavCommand(Name targetName) {
        requireNonNull(targetName);
        this.targetName = targetName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        Person personToFav = lastShownList.stream()
                .filter(person -> person.getName().equals(targetName))
                .findFirst()
                .orElseThrow(() -> new CommandException(
                        String.format(MESSAGE_PERSON_NOT_FOUND, targetName)));

        Person favouritedPerson = new Person(
                personToFav.getName(), personToFav.getPhone(), personToFav.getEmail(),
                personToFav.getAddress(), personToFav.getTags(), true);

        model.setPerson(personToFav, favouritedPerson);
        return new CommandResult(String.format(MESSAGE_FAV_PERSON_SUCCESS, Messages.format(favouritedPerson)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof FavCommand)) {
            return false;
        }
        FavCommand otherFavCommand = (FavCommand) other;
        return targetName.equals(otherFavCommand.targetName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetName", targetName)
                .toString();
    }
}
