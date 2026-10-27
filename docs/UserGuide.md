---
layout: page
title: User Guide
---

HobbyHub is a **desktop application for managing contacts, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, HobbyHub can help you manage contacts faster than traditional GUI applications.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2627S1-CS2103T-T17-4/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your HobbyHub.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar hobbyhub.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add -n John Doe -p 98765432 -c johnd@example.com -a John street, block 123, #01-01 -h "Chess:Beginner" -h "Table Tennis:Advanced"` : Adds a contact named `John Doe` to the HobbyHub.

   * `search -n John` : Finds members whose names contain `John`.

   * `search -h Chess` : Finds members whose hobbies contain `Chess`.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `view 3` : Shows the phone, address and email of the 3rd contact shown on the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add -n NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `-n NAME [-g GROUP]` can be used as `-n John Doe -g friend` or as `-n John Doe`.

* Items followed by `…`​ can appear zero or more times.<br>
  For example, `[-g GROUP]…​` may be omitted, or written as `-g friend` or `-g friend -g family`.

* Parameters can be in any order.<br>
  For example, if the command specifies `-n NAME -p PHONE_NUMBER`, `-p PHONE_NUMBER -n NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a person: `add`

Adds a person to HobbyHub.

Format: `add -n NAME [-p PHONE_NUMBER] -c EMAIL [-a ADDRESS] -h "HOBBY:SKILL" [-h "HOBBY:SKILL"]… [-g GROUP]…`

* Name, email and at least one hobby are required. Phone number and address are optional.
* Omit `-p` or `-a` when those details are unknown. If supplied during add, each must have a valid, non-empty value.
* Supply name and email once, and phone/address at most once if included. Repeat `-h` for additional hobbies and `-g` for additional groups.
* Names and group names must start with a Unicode letter or digit. They may also contain Unicode letters and digits,
  combining marks, spaces, apostrophes, hyphens, periods, ampersands, and plus signs.
* Phone numbers must contain at least three digits and may have one leading `+`. Do not include spaces or other formatting.
* Use a valid email address, such as `johnd@example.com`. Hobby names must start with a letter or digit and may contain
  letters, digits, spaces, and `&`, `+`, `'`, `/`, or `-`.
* Enclose a hobby and skill pair in double quotes when it contains spaces, for example, `-h "Board Games:Advanced"`.
* Each `-h` option requires a non-empty hobby value. Unknown add options are rejected. Quote hobby text containing an
  option-like token, such as `-h "Chess -x"`, to keep it as part of the hobby value.
* Skill labels are Beginner, Intermediate and Advanced, ignoring capitalization. Hobby names cannot repeat,
  even with different skill levels. Legacy `-h Board Games` input remains valid and defaults to Beginner.
* Email addresses must be unique, ignoring capitalization. Members may share a name.

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A person can have any number of groups, including zero.
</div>

Examples:
* `add -n Alice -c alice@example.com -h "Chess:Beginner"` Adds a member without phone or address.
* `add -n John Doe -p 98765432 -c johnd@example.com -a John street, block 123, #01-01 -h "Chess:Beginner" -h "Table Tennis:Advanced"`
* `add -n Betsy Crowe -g friend -c betsycrowe@example.com -a Newgate Prison -p 1234567 -g criminal -h Chess`

### Listing members or sessions: `list`

Shows all records in the current list, clearing its search filter.

Format: `list [-m | -s]`

* `list -m` switches to members and shows all members.
* `list -s` switches to sessions and shows all sessions.
* `list` keeps the active view and shows all its entries.
* Other flags or additional arguments are rejected.

### Editing a person: `edit`

Edits an existing person in HobbyHub.

Format: `edit INDEX [-n NAME] [-p PHONE_NUMBER] [-c EMAIL] [-a ADDRESS] [-h "HOBBY:SKILL"]… [-g GROUP]…`

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* Omit `-p` or `-a` to preserve the current value. If supplied, the option requires a valid, non-empty value.
* Supplying `-h` replaces the entire hobby list with the supplied hobbies and skill levels.
  Omitting `-h` preserves all hobbies. An empty `-h` is rejected; at least one hobby is required.
* When editing groups, all of the person's existing groups are removed; adding groups is not cumulative.
* To remove all of a person's groups, enter `-g` without a group after it.
* Omitting `-g` preserves the member's current groups.

Examples:
* `edit 1 -h "Chess:Advanced" -h "Cycling:Intermediate"` Replaces the member's hobbies with these two entries.
* `edit 1 -p 91234567 -c johndoe@example.com` Edits the phone number and email address of the 1st person to be `91234567` and `johndoe@example.com` respectively.
* `edit 2 -n Betsy Crower -g` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing groups.

### Searching members and sessions: `search`

Search filters the currently displayed list. Use `list -m` to switch to members or `list -s` to switch to sessions,
then enter a search command. You only need to switch when changing which type of records you want to search.

Use one or more distinct options per search, in any order. Every supplied condition must match (AND).
Single-option searches continue to work. Repeated options, unsupported options, and empty queries are rejected.
Invalid input leaves the previous search results unchanged.
Each search replaces the previous filter and searches all records of the current type.
Plain `list` shows all records in the current list again. Searching does not change stored data.
Results use the normal member or session cards, with a message showing how many records matched.
No matches produce an empty list and a count of zero.

#### Member search options

Format: `search [-n NAME_FRAGMENT] [-h HOBBY_FRAGMENT] [-g GROUP_FRAGMENT] [-c EMAIL] [-sl SKILL_LEVEL]`

At least one option is required.

| Field | Format | Matching rule |
| --- | --- | --- |
| Name | `search -n NAME_FRAGMENT` | Case-insensitive substring |
| Hobby name | `search -h HOBBY_FRAGMENT` | Case-insensitive substring in any hobby name |
| Group | `search -g GROUP_FRAGMENT` | Case-insensitive substring in any group name |
| Email | `search -c EMAIL` | Complete, valid email address, ignoring case |
| Skill level | `search -sl SKILL_LEVEL` | Any hobby at Beginner, Intermediate, or Advanced, ignoring case |

Hobbies and groups are searched independently. Without `-h`, a skill-level search matches any hobby at that level.
When `-h` and `-sl` are combined, the same hobby must match both the name fragment and skill level.
For example, a member with Chess:Beginner and Tennis:Advanced does not match `search -h Chess -sl Advanced`.
Phone, address, and availability are not searchable.

Examples after `list -m`:

* `search -n John` finds members whose names contain `John`, including `Johannes`.
* `search -h chess` finds members with any hobby name containing `chess`.
* `search -g Weekend Club` finds members with a group name containing `Weekend Club`.
* `search -c alice@example.com` finds the member with that email address.
* `search -sl Advanced` finds members with at least one Advanced hobby.
* `search -h Chess -sl Beginner` finds members whose Chess hobby is Beginner.
* `search -n Alice -g Weekend` finds members whose name contains `Alice` and whose group name contains `Weekend`.
* `search -n Alice -h Tennis -g Weekend -c alice@example.com -sl Advanced` requires all five conditions to match.

#### Session search options

Format: `search [-n NAME_FRAGMENT] [-l LOCATION_FRAGMENT] [-t YYYY-MM-DD] [-done true/false] [-c EMAIL]`

At least one option is required. All conditions must match the same session.

| Field | Format | Matching rule |
| --- | --- | --- |
| Name | `search -n NAME_FRAGMENT` | Case-insensitive substring |
| Location | `search -l LOCATION_FRAGMENT` | Case-insensitive substring |
| Date | `search -t YYYY-MM-DD` | Exact calendar date, regardless of time |
| Completion status | `search -done true` or `search -done false` | Completed or incomplete respectively, ignoring case |
| Participant email | `search -c EMAIL` | Contains a participant with that complete email, ignoring case |

Dates must be valid calendar dates with a four-digit year, two-digit month, and two-digit day.
For example, `2026-02-29` is rejected. Date ranges and exact times are not supported by search.
Participant email searches can return several sessions, and sessions without participants do not match them.

Examples after `list -s`:

* `search -n chess` finds sessions whose names contain `chess`.
* `search -l community` finds sessions whose locations contain `community`.
* `search -t 2026-10-24` finds every session on 24 October 2026.
* `search -done false` finds incomplete sessions.
* `search -c alice@example.com` finds every session that Alice participates in.
* `search -l Community -t 2026-10-24` finds sessions at locations containing `Community` on that date.
* `search -c alice@example.com -done false` finds incomplete sessions that Alice participates in.
* `search -n Chess -l Community -t 2026-10-24 -done false -c alice@example.com` requires all five conditions to match.

Text fragments can contain spaces. One surrounding pair of double quotes is optional:
`search -n "Weekend Chess"` and `search -n Weekend Chess` are equivalent.
Quote text containing a word starting with `-` so it is treated as text rather than another option.

### Deleting a session: `delete`

Deletes the session at the specified index from the currently displayed session list.

Format: `delete INDEX`

* This command is available while the session list is shown (`list -s`).
* The index refers to the currently displayed session list and must be a positive integer.
* Deleting a session also removes it from the sessions listed on every member assigned to it.

Example: `list -s` followed by `delete 1` deletes the first displayed session.

### Deleting a person: `delete`

Deletes the specified person from HobbyHub.

Format: `delete INDEX`

* Deletes the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list` followed by `delete 2` deletes the 2nd person in HobbyHub.
* `search -n Betsy` followed by `delete 1` deletes the 1st person in the search results.

### Viewing a session: `view`

Views the session at the specified index from the currently displayed session list.

Format: `view INDEX`

* This command is available while the session list is shown (`list -s`).
* The index refers to the currently displayed session list and must be a positive integer.
* Viewing a session also shows all members assigned to the session.

Example: `list -s` followed by `view 1` deletes the first displayed session.

### Viewing a person: `view`

Viewing the additional details specified person from HobbyHub (Phone, address and email).

Format: `view INDEX`

* Views the details of the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list -m` followed by `view 2` deletes the 2nd person in HobbyHub.


### Assigning a person to a session: `assign`

Assigns the member at the specified index in the currently displayed member list to an existing session.

Format: `assign INDEX SESSION_NAME`

* This command is available while the member list is shown.
* The index refers to the currently displayed member list, including search results.
* Session names may contain spaces and must match an existing session name.

Example: `assign 1 Weekend Chess Meetup` assigns the first displayed member to that session.

### Marking a session as completed: `mark` / `unmark`

Marks the session at the specified index as completed, or as not completed.

Format: `mark INDEX` or `unmark INDEX`

* These commands are only available while the session list is shown. Use `list -s` to switch to it.
* The index refers to the index number shown in the displayed session list. The index **must be a positive integer** 1, 2, 3, …​
* Marking a session that is already completed, or unmarking one that is not, shows an error and changes nothing.
* The session's name, date-time, location and assigned members are kept.

Examples:
* `list -s` followed by `mark 1` marks the 1st session as completed.
* `unmark 1` marks it as not completed again.

### Clearing all entries: `clear`

Clears all entries from HobbyHub.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

HobbyHub automatically saves data after every successful command. You do not need to save manually.

### Editing the data file

HobbyHub data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, HobbyHub starts with an empty member list at the next run. The invalid file remains on disk until you run a successful command (HobbyHub saves after every successful command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause HobbyHub to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous HobbyHub home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add** | `add -n NAME [-p PHONE_NUMBER] -c EMAIL [-a ADDRESS] -h "HOBBY:SKILL" [-h "HOBBY:SKILL"]… [-g GROUP]…​` <br> e.g., `add -n James Ho -p 22224444 -c jamesho@example.com -a 123, Clementi Rd, 1234665 -g friend -g colleague -h Chess`
**Assign** | `assign INDEX SESSION_NAME`<br> e.g., `assign 1 Weekend Chess Meetup`
**Clear** | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3` (member list) or `delete 1` (session list)
**Edit** | `edit INDEX [-n NAME] [-p PHONE_NUMBER] [-c EMAIL] [-a ADDRESS] [-h "HOBBY:SKILL"]… [-g GROUP]…​`<br> e.g., `edit 2 -n James Lee -c jameslee@example.com`
**Search members** | `search [-n NAME] [-h HOBBY] [-g GROUP] [-c EMAIL] [-sl SKILL_LEVEL]` (at least one option; AND matching)
**Search sessions** | `search [-n NAME] [-l LOCATION] [-t YYYY-MM-DD] [-done true/false] [-c EMAIL]` (at least one option; AND matching)
**List** | `list [-m \| -s]` switches lists with a flag, or shows all entries in the current list without one
**View** | `view INDEX`<br> e.g., `view 3`
**Mark / Unmark** (session list) | `mark INDEX` or `unmark INDEX`<br> e.g., `mark 1`
**Help** | `help`
**Exit** | `exit`

### Hobby skill levels

Member profiles can store multiple hobbies, each with a Beginner, Intermediate, or Advanced skill level.
All stored hobbies and their skill levels appear on the member card, and hobby searches match any hobby name.
Existing data with a single hobby loads with Beginner as its skill level.

Use one `-h "HOBBY:SKILL"` option for each hobby, for example:

`-h "Chess:Beginner" -h "Table Tennis:Advanced"`

Each option describes one hobby and its skill level. A missing name, empty or unrecognized skill suffix,
unbalanced quotes, repeated hobby name, or extra colon causes the command to fail without modifying data.
Names without a skill suffix remain supported and default to Beginner. Editing `-h` replaces all hobbies;
editing other fields preserves the current hobbies and skill levels.

### Member groups

Groups are optional labels for organising members, independent of hobbies and skill levels.
Use repeated `-g` options, for example `-g "Weekend Club" -g Volunteers`.
Group names are case-sensitive; repeated identical names are stored once.

Editing with `edit 1 -g "Weekend Club"` replaces all of that member's groups. Use `edit 1 -g`
to clear their groups. These operations preserve their hobbies, skill levels and session assignments.

The former `-t` option is rejected with guidance to use `-g`. Saved JSON files with legacy `tags`
still load; those names become groups. Saving writes `groups` rather than `tags`. If a file contains
both fields, their contents are combined without dropping either set of labels.

### Optional contact details

Phone and address can be omitted when adding a member. Missing values appear as `Not provided`
on the member card and in command feedback. When editing, `-p` and `-a` require valid values;
omitting either option leaves its value unchanged. Empty edit values are rejected.
Name, email and hobbies remain required.

Saved JSON omits absent phone/address fields. Older files with contact details still load, and
missing, null or empty phone/address fields load as absent values. Malformed non-empty values are rejected.
