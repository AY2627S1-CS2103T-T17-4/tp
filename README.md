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

| Feature | Description                                                                                                            | Status |
| --- |------------------------------------------------------------------------------------------------------------------------| --- |
| Add member | Add a new member with a name, unique email address, and primary hobby.                                                 | MVP |
| List all members | Display all members currently being managed.                                                                           | MVP |
| Remove member | Remove an existing member using their unique email address.                                                            | MVP |
| Search members by name or by hobby | Search members by name or hobby using case-insensitive partial matching.                                                       | MVP |
| Automatic data saving | Save member data automatically after every successful data-changing command and reload it when the application starts. | MVP |
| Exit program | Exit HobbyHub cleanly without losing successfully saved data.                                                          | MVP |

## MVP scope

The MVP is intentionally small and local. It focuses on the minimum workflow needed for a coordinator to maintain a usable member list:

- Add a member with a name, unique email address, and primary hobby.
- List all members.
- Remove a member by email address.
- Search members by name using case-insensitive partial matching.
- Save data automatically after each successful data-changing command.
- Exit the application cleanly.

The MVP uses exact, lowercase command words and Bash-style short options. Fuzzy search, multiple hobbies, phone-number support, backup commands, confirmations, undo/redo, and command aliases are outside the MVP and planned for later iterations.

## Documentation

### Prerequisites

- Java Development Kit (JDK) 25 or later
- A supported desktop environment for JavaFX

### Commands

| Command | Format | Example |
| --- | --- | --- |
| Add member | `add -n "NAME" -c EMAIL -h HOBBY` | `add -n "John Doe" -c john@example.com -h Chess` |
| List all members | `list` | `list` |
| Search members by name | `search -n NAME` | `search -n John` |
| Search members by hobby | `search -h HOBBY` | `search -h Chess` |
| Remove member | `remove -c EMAIL` | `remove -c john@example.com` |
| Exit program | `exit` | `exit` |

Notes:
- Values containing spaces (e.g. names) must be enclosed in double quotation marks.
- Command words and option flags (`-n`, `-c`, `-h`) are lowercase and case-sensitive.
- Data is saved automatically after every successful data-changing command, and reloaded on startup.
- Search command takes exactly one of -n or -h.

## Authors

- Dylan Tay
- Ng Jing Jie, Asher
- Chew Hong Zhi
- Darryl Zhang Junzhuo
- Ni Jian

## Acknowledgements

This project is based on the [AddressBook-Level3 project](https://github.com/se-edu/addressbook-level3) created by the [SE-EDU initiative](https://se-education.org).

HobbyHub also uses [JavaFX](https://openjfx.io/), [Jackson](https://github.com/FasterXML/jackson), [JUnit 5](https://junit.org/junit5/), Gradle, and the [JaCoCo](https://www.jacoco.org/jacoco/) code-coverage tool.


