| layout | page |
| :---   | :--- |
| title | Developer Guide |
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the originals.}_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams are in `docs/diagrams`. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Logic.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.

</div>

Similarly, how an undo operation goes through the `Model` component is shown below:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Model.png)

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command is correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

A single community hobby-group coordinator who
* manages prospective and existing members of one or more hobby groups
* records member details such as hobbies, skill level, availability, and area
* needs to find suitable members when organising activities
* manually decides which members to match or assign to groups
* prefers fast, keyboard-driven workflows
* is reasonably comfortable using CLI applications
* does not require members to log in or access the application directly

**Value proposition**: Help a hobby-group coordinator manage members and matches quickly from the keyboard by storing member details locally, supporting fast searches and filters, and allowing the coordinator to record matches and group memberships without relying on spreadsheets or remote services.

HobbyHub is a single-user, coordinator-driven tool. It does not provide member accounts or automated black-box matching.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

# HobbyHub — User Stories

# HobbyHub — User Stories

| Priority | ID | As a …​ | I want to …​ | So that I can…​ |
| -------- | -- | ------- | ------------ | ---------------- |
| `* * *` | US01 | coordinator | add a member with a name, email address, and primary hobby | maintain an up-to-date member list |
| `* * *` | US02 | coordinator | list all members | view the members currently under my management |
| `* * *` | US03 | coordinator | remove a member using their unique email address | keep the member list accurate |
| `* * *` | US04 | coordinator | search for members by name using case-insensitive partial matching | retrieve a member quickly |
| `* * *` | US05 | coordinator | search members by hobby | focus on members interested in a particular activity |
| `* * *` | US06 | coordinator | save member data automatically after successful changes and restore it when the application restarts | avoid losing records between sessions |
| `* * *` | US07 | coordinator | exit the application cleanly | close the application without losing saved data |
| `* *` | US08 | coordinator | view a member's complete profile | understand the member's interests and participation needs |
| `* *` | US09 | coordinator | edit a member's information | correct outdated or inaccurate records |
| `* *` | US10 | coordinator | record a member's hobbies, skill level, availability, and area | identify suitable participants for activities |
| `* *` | US11 | coordinator | filter members by availability and skill level | identify members suitable for a planned activity |
| `* *` | US12 | coordinator | combine several search criteria | narrow down a large member list efficiently |
| `* *` | US13 | coordinator | preview the shared attributes of two members | evaluate a potential pairing before recording it |
| `* *` | US14 | coordinator | record a match between two members | keep track of pairings that I have arranged |
| `* *` | US15 | coordinator | view a member's current match | know which pairing may be affected by a change |
| `* *` | US16 | coordinator | remove an existing match | correct an outdated or unsuccessful pairing |
| `* *` | US17 | coordinator | create and assign members to hobby groups | manage group participation |
| `* *` | US18 | coordinator | view a member's group memberships | know which activities the member is involved in |
| `* *` | US19 | coordinator | remove a member from a specific hobby group without deleting the member | update group participation accurately |
| `* *` | US20 | coordinator | archive inactive members and ended matches | retain historical information without cluttering active records |
| `* *` | US21 | coordinator | import member sign-ups from a CSV file | add many local records efficiently |
| `* *` | US22 | coordinator | export member data | create backups or reuse the data elsewhere |
| `* *` | US23 | coordinator | view local statistics such as common hobbies and unmatched members | understand the current state of my programme |
| `*` | US24 | coordinator | use natural-language variants for equivalent commands | remember commands more easily |
| `*` | US25 | coordinator | enter member details one field at a time | avoid remembering every option format |
| `*` | US26 | coordinator | access in-app help and command suggestions | learn how to use the application without external documentation |
| `*` | US27 | coordinator | recall previously entered commands | repeat common actions quickly |
| `*` | US28 | coordinator | undo and redo recent changes | recover from accidental edits or removals |
| `*` | US29 | coordinator | receive confirmation before destructive actions | avoid unintended removals or data clearing |
| `*` | US30 | coordinator | perform fuzzy and typo-tolerant searches | find members without knowing the exact spelling |
| `*` | US31 | coordinator | store multiple hobbies and phone numbers | represent more complete member profiles |
| `*` | US32 | coordinator | validate and standardise addresses | keep stored contact information consistent |
| `*` | US33 | coordinator | use GUI controls for common actions | use the application comfortably without typing every command |

### Use cases

(For all use cases below, the **System** is `HobbyHub` and the **Actor** is the `coordinator`, unless specified otherwise)

**Use case: UC01 - Add a member**

**Preconditions**

* HobbyHub is running.
* The local data store is available.

**MSS**

1. Coordinator initiates the addition of a member.
2. Coordinator supplies the member's name, email address, and primary hobby.
3. HobbyHub validates the supplied details.
4. HobbyHub verifies that the email address is unique.
5. HobbyHub creates the member record.
6. HobbyHub persists the updated member data.
7. HobbyHub records the successful addition.

   Use case ends.

**Guarantees**

* The new member is available in subsequent searches and listings.
* The new member is persisted and can be restored after relaunching HobbyHub.
* Invalid input does not partially modify the member list.

**Extensions**

* 2a. One or more required details are missing.

  * 2a1. HobbyHub reports the missing details.
  * 2a2. HobbyHub does not create or modify the member record.

    Use case ends.

* 3a. One or more supplied details are invalid.

  * 3a1. HobbyHub reports the invalid details and the expected format, where applicable.
  * 3a2. HobbyHub does not create or modify the member record.

    Use case ends.

* 4a. The email address is already in use.

  * 4a1. HobbyHub reports that the email address is already associated with an existing member.
  * 4a2. HobbyHub does not create or modify the member record.

    Use case ends.

* 6a. HobbyHub cannot persist the updated member data.

  * 6a1. HobbyHub reports the persistence failure.
  * 6a2. HobbyHub does not record the addition as successful.
  * 6a3. HobbyHub does not partially modify the stored member data.

    Use case ends.

* *a. Coordinator cancels the operation.

  * *a1. HobbyHub ends the operation without modifying the member data.

    Use case ends.

**Use case: UC02 - List all members**

**Preconditions**

* HobbyHub is running.

**MSS**

1. Coordinator requests to list all members.
2. HobbyHub retrieves the current member records.
3. HobbyHub returns all current member records to the coordinator.

   Use case ends.

**Guarantees**

* All currently stored members are displayed.
* No member data is changed.

**Extensions**

* 1a. The list request contains additional or invalid parameters.

  * 1a1. HobbyHub reports that the request format is invalid.
  * 1a2. HobbyHub does not retrieve or modify any member records.

    Use case ends.

* 2a. There are no members in HobbyHub.

  * 2a1. HobbyHub reports that no members have been added.

    Use case ends.

**Use case: UC03 - Remove a member**

**Preconditions**

* HobbyHub is running.
* The member to be removed exists.

**MSS**

1. Coordinator initiates the removal of a member.
2. Coordinator supplies the member's unique email address.
3. HobbyHub validates the email address.
4. HobbyHub locates the corresponding member record.
5. HobbyHub removes the member record.
6. HobbyHub persists the updated member data.
7. HobbyHub records the successful removal.

   Use case ends.

**Guarantees**

* The selected member is no longer available in the member list.
* The removed member remains absent after HobbyHub is relaunched.
* Other member records remain unchanged.

**Extensions**

* 2a. The coordinator does not provide an email address.

  * 2a1. HobbyHub reports that an email address is required.
  * 2a2. HobbyHub does not remove or modify any member record.

    Use case ends.

* 3a. The supplied email address is invalid.

  * 3a1. HobbyHub reports the invalid email address and the expected format.
  * 3a2. HobbyHub does not remove or modify any member record.

    Use case ends.

* 4a. No member has the supplied email address.

  * 4a1. HobbyHub reports that no matching member was found.
  * 4a2. HobbyHub does not remove or modify any member record.

    Use case ends.

* 6a. HobbyHub cannot persist the updated member data.

  * 6a1. HobbyHub reports the persistence failure.
  * 6a2. HobbyHub does not record the removal as successful.
  * 6a3. HobbyHub preserves the previously persisted member data.

    Use case ends.

* *a. The coordinator cancels the operation before the member record is removed.

  * *a1. HobbyHub ends the operation without modifying the member data.

    Use case ends.

**Use case: UC04 - Search members by name or hobby**

**Preconditions**

* HobbyHub is running.

**MSS**

1. Coordinator initiates a member search.
2. Coordinator supplies either a name fragment or a hobby fragment.
3. HobbyHub validates the search term.
4. HobbyHub identifies members whose name or hobby (whichever was supplied) contains the search term, ignoring letter case.
5. HobbyHub returns the matching member records to the coordinator.

   Use case ends.

**Guarantees**

* HobbyHub displays all members whose names or hobbies match the search criteria.
* No member data is changed.

**Extensions**

* 1a. The search request contains additional or invalid parameters.

  * 1a1. HobbyHub reports that the request format is invalid.
  * 1a2. HobbyHub does not perform a search or modify any member records.

* 1b. The coordinator supplies both a name and a hobby.

  * 1b1. HobbyHub reports that only one search criterion can be given.
  * 1b2. HobbyHub does not perform a search. Use case ends.

    Use case ends.

* 2a. The coordinator does not provide a name or hobby fragment.

  * 2a1. HobbyHub reports that a non-empty search term is required.
  * 2a2. HobbyHub does not perform a search or modify any member records.

    Use case ends.

* 3a. The search term does not follow the expected format.

  * 3a1. HobbyHub reports the invalid search term and the expected format.
  * 3a2. HobbyHub does not perform a search or modify any member records.

    Use case ends.

* 4a. No members match the search term.

  * 4a1. HobbyHub reports that no matching members were found.

    Use case ends.

* *a. The coordinator cancels the search before it is completed.

  * *a1. HobbyHub ends the operation without modifying any member records.

    Use case ends.

**Use case: UC05 - Persist and restore member data**

**Preconditions**

* HobbyHub has been used to add or modify member data.
* The local data store is available.

**MSS**

1. Coordinator completes a successful data-changing operation.
2. HobbyHub persists the updated member data.
3. Coordinator exits HobbyHub.
4. Coordinator relaunches HobbyHub.
5. HobbyHub loads the persisted member data.
6. HobbyHub restores the member data for continued use.

   Use case ends.

**Guarantees**

* Successfully persisted member data is retained between application sessions.
* Members, contact details, and hobbies are restored without unintended changes.
* No separate manual save command is required.

**Extensions**

* 2a. HobbyHub cannot create, access, or write to the local data store.

  * 2a1. HobbyHub reports that the data could not be saved.
  * 2a2. HobbyHub informs the coordinator that the latest changes may not be available after relaunch.

    Use case ends.

* 5a. HobbyHub cannot read the persisted member data.

  * 5a1. HobbyHub reports that the data could not be loaded.
  * 5a2. HobbyHub does not claim that the previous member data was restored.

    Use case ends.

* 5b. The persisted data is invalid.

  * 5b1. HobbyHub reports that the data file is invalid.
  * 5b2. HobbyHub does not silently overwrite the invalid data before informing the coordinator.

    Use case ends.

**Use case: UC06 - Exit the program**

**Preconditions**

* HobbyHub is running.

**MSS**

1. Coordinator initiates an exit request.
2. HobbyHub terminates the application gracefully.

   Use case ends.

**Guarantees**

* HobbyHub closes without crashing.
* All previously persisted member data remains available for the next session.

**Extensions**

* 1a. The exit request contains additional or invalid parameters.

  * 1a1. HobbyHub reports that the exit request format is invalid.
  * 1a2. HobbyHub does not terminate the application.

    Use case ends.

* 2a. HobbyHub encounters an unexpected failure while terminating.

  * 2a1. HobbyHub reports the termination failure.
  * 2a2. HobbyHub does not claim that the application exited successfully.
  * 2a3. Previously persisted member data remains unchanged.

    Use case ends.

### Non-Functional Requirements

| ID | Category | Requirement |
| --- | --- | --- |
| NFR01 | Portability | HobbyHub should run on Windows, macOS, and Linux systems with JDK 25 or later and the required JavaFX runtime. |
| NFR02 | Capacity| HobbyHub should support at least 1000 member records while preserving all MVP functionality. |
| NFR03 | Performance | For a local dataset of up to 1000 members, common operations such as adding a member, searching by name, and applying a basic filter should complete within two seconds under normal operations. |
| NFR04 | Usability | All core workflows should be usable through keyboard-driven commands without requiring mouse interaction or relying solely on colour, animation, or pointer-based interaction. |
| NFR05 | Availability | All MVP functions should work without requiring an Internet connection, remote API, or hosted database. |
| NFR06 | Privacy | Member data should remain on the coordinator's local machine unless the coordinator explicitly exports it. |
| NFR07 | Reliability | Invalid commands should not partially modify stored data. |
| NFR08 | Robustness | Validation and persistence failures should be reported clearly without causing the application to crash. |
| NFR09 | Data portability | Member data should be stored in a local, human-readable format that can be inspected or backed up using standard file-management tools. |
| NFR10 | Error clarity | Every invalid MVP command should produce an error message that identifies the invalid or missing input and, where applicable, states the expected format. |
| NFR11 | UI | The main window should remain usable at a minimum resolution of 1024 × 768 pixels, with no command input, result display, or member information clipped at that resolution. |


### Glossary

* **Mainstream OS**: Windows, Linux, Unix, or macOS
* **Private contact detail**: A contact detail that is not meant to be shared with others

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command, with multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: The first contact is deleted from the list. The status message shows the deleted contact's details.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases …​ }_
