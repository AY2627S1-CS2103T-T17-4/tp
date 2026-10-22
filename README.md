[![CI Status](https://github.com/AY2627S1-CS2103T-T17-4/tp/workflows/Java%20CI/badge.svg)](https://github.com/AY2627S1-CS2103T-T17-4/tp/actions) [![codecov](https://codecov.io/gh/AY2627S1-CS2103T-T17-4/tp/graph/badge.svg?token=k5fKg7pGJ0)](https://codecov.io/gh/AY2627S1-CS2103T-T17-4/tp)

![Ui](docs/images/Ui.png)

# HobbyHub

## Value proposition

HobbyHub helps a community hobby-group coordinator manage members and matches quickly from the keyboard. It keeps hobby, skill level, availability, group memberships, and match relationships in one local application, allowing coordinators to search and update their records without relying on spreadsheets or a remote service.

HobbyHub is a single-user, CLI-first productivity tool. It does not connect members to one another directly or use automated, black-box matching. Instead, it gives the coordinator fast, structured tools to make and record their own decisions.

## Project status

**In development — MVP specification complete.**

The product direction, user stories, and external MVP behaviour have been specified. The current repository is an early JavaFX/CLI prototype derived from the AddressBook Level 3 codebase; HobbyHub-specific member, hobby, matching, and group-management features are being implemented incrementally.

## MVP features

| Feature | Description                                                                                                          | Status |
| --- |----------------------------------------------------------------------------------------------------------------------| --- |
| Add member | Add a new member with a name, phone number, email, address, hobby, and optional tags.                                              | MVP |
| List all members | Display all members currently being managed.                                                                         | MVP |
| Delete member | Delete a member by the index number shown in the current list.                                                       | MVP |
| Search members by name or by hobby | Search members by name or hobby using case-insensitive partial matching.                                                     | MVP |
| Automatic data saving | Save member data automatically after every successful data-changing command and reload it when the application starts. | MVP |
| Exit program | Exit HobbyHub cleanly without losing successfully saved data.                                                        | MVP |

## MVP scope

The MVP is intentionally small and local. It focuses on the minimum workflow needed for a coordinator to maintain a usable member list:

- Add a member with a name, phone number, email, address, hobby, and optional tags.
- List all members.
- Delete a member by their index in the displayed list.
- Search members by name or hobby using case-insensitive partial matching.
- Save data automatically after each successful data-changing command.
- Exit the application cleanly.

The MVP uses exact, lowercase command words and Bash-style short options. Fuzzy search, multiple hobbies, backup commands, confirmations, undo/redo, and command aliases are outside the MVP and planned for later iterations.

## Documentation

### Prerequisites

- Java Development Kit (JDK) 25 or later
- A supported desktop environment for JavaFX

### Commands

| Command | Format | Example |
| --- | --- | --- |
| Add member | `add -n NAME -p PHONE -c EMAIL -a ADDRESS -h HOBBY [-t TAG]...` | `add -n John Doe -p 98765432 -c john@example.com -a 311 Clementi Ave 2 -h Chess` |
| List all members | `list` | `list` |
| Search members by name | `search -n NAME` | `search -n John` |
| Search members by hobby | `search -h HOBBY` | `search -h Chess` |
| Delete member or session | `delete INDEX` | Member list: `delete 2`; session list: `list -s`, then `delete 1` |
| Unassign member from a session | `unassign INDEX SESSION_NAME` (member list only) | `unassign 2 Weekend Chess Meetup` |
| Exit program | `exit` | `exit` |

Notes:
- Command words and option flags (`-n`, `-c`, `-h`, `-p`, `-a`, `-t`) are lowercase and case-sensitive.
- Data is saved automatically after every successful data-changing command, and reloaded on startup.
- Search command takes exactly one of -n or -h.
- INDEX refers to the number shown in the current list; after search, it refers to the filtered list. In the session list, `delete` removes the session and clears it from assigned members' session records.

## Authors

- Dylan Tay
- Ng Jing Jie, Asher
- Chew Hong Zhi
- Darryl Zhang Junzhuo
- Ni Jian

## Acknowledgements

This project is based on the [AddressBook-Level3 project](https://github.com/se-edu/addressbook-level3) created by the [SE-EDU initiative](https://se-education.org).

HobbyHub also uses [JavaFX](https://openjfx.io/), [Jackson](https://github.com/FasterXML/jackson), [JUnit 5](https://junit.org/junit5/), Gradle, and the [JaCoCo](https://www.jacoco.org/jacoco/) code-coverage tool.


