# open-realms: an open-deckbuilding game

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Ne0Strix_open-realms&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Ne0Strix_open-realms) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Ne0Strix_open-realms&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Ne0Strix_open-realms) [![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Ne0Strix_open-realms&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=Ne0Strix_open-realms) [![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Ne0Strix_open-realms&metric=bugs)](https://sonarcloud.io/summary/new_code?id=Ne0Strix_open-realms) [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Ne0Strix_open-realms&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=Ne0Strix_open-realms) [![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=Ne0Strix_open-realms&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=Ne0Strix_open-realms)

## Communication

Communication is handled within the `Communication` class. All classes that want to communicate in any way can use a `Communication` object to do so. Once instantiated, it listens for messages and sends them to a `MessageHandler`. Client and Server each have their own message-handler. The client-messagehandler only forwards the message into the UI-thread as UI updates have to be done there. The server-messagehandler handles the message and sends a response back to the client.

### Message

Messages are identified using the `MessageType` enum, the data inside is stored in key-value pairs. All keys are defined in the `DataKey` enum.

When adding a new Message/Datatype bear the following things in mind:

- **only communicate the bare minimum of information**: e.g. it's sufficient to only communicate the id of a card to be played, as the server knows where the card is and who sent the message anyway. We do this to avoid communicating inconsistent data.
- **reusable `DataKeys`**: make keys reusable and give them a clear purpose. Validate the integrity of the key in the `Message` class.

### Player Stats

This class is used to communicate the current state of a player. It _always_ contains the variables declared in the class. We use the DataKey `TARGET_PLAYER` to specify the player the stats are for.

### DataKeys

Please comment the specific usage of a key in the `DataKey` enum. If you need a new key, please add it to the bottom of the enum and make sure to add a comment. Also verify the integrity of the key in the `Message` class and add corresponding tests.

## Project Setup

To auto-format all java-files please copy the `pre-commit` file to `.git/hooks/` (without any file-endings). This allows spotless to run before each commit and help us keep the same codestyle across all systems. Under Linux execute `cp pre-commit .git/hooks/pre-commit`.

Also, try running `./gradlew spotlessApply` to see whether the plugin works as expected.

A problem that might occur when sharing a git repo across Windows and Linux operating systems is the different newline-characters. If you're on Windows execute `git config --global core.autocrlf true` to auto-convert between the two types of line-return. For more details check out the "Formatting and Whitespace" section in the [Git Configuration Manual](https://git-scm.com/book/en/v2/Customizing-Git-Git-Configuration#Formatting-and-Whitespace).

### Git

**Only branch from develop!**

In order to easily match branches to issues please start the name of your branch with the number of the issue. For example: `git checkout -b 2-ci-integration`. You can change between branches via `git checkout <branchname>`.
Further, please include the issue-number that the commit belongs to at the beginning of the commit-title (not a must) `git commit -m "1 upgrade version`.

GitHub can perform actions automatically on issues by using keywords. For example

- `git commit -m "fix #10"`

Please refer to the [official documentation](https://docs.github.com/en/issues/tracking-your-work-with-issues/linking-a-pull-request-to-an-issue) for more details.

## About Issues, Milestones, Tasks, …

Git(Hub) provides the following concepts. We use them to organise the work like so:

- **Milestones**
  - collect multiple userstories that all relate together
- **Issues**
  - correspond to a single branch
  - can only be completed when all tasks within are checked
- **Feature-Branches**
  - implement a working feature
  - are branched from and merged to `develop`
  - collect multiple commits
- **Commits**
  - contain meaningful units of codechanges
  - Java-files are automagically formatted before commiting
- **Tasks**
  - represent smaller parts of work done in the issue
  - are used to structure the work within an issue
- **Pull Requests**
  - default base branch is `develop`
  - require reviews and succesful CI checks to be merged

## Project Organisation

Under the repos "Projects"-tab you find the linked project where all stories, issues, sprints and tasks are organised. There are four views:

### Backlog

The backlog contains all issues and drafts. When you add an item it will be in the `Draft`-state until it's converted to an issue in this repository. Before converting it to an issue make sure the story is well defined. Newly created issues get the `Newcomer` status and are visible in the Refinement view.

### Refinement

During the refinement we estimate the issues and move them to the `ToDo` state.

### Sprint Planning

In the "Sprint Planning" tab all issues are grouped by Sprint. In the sprintplanning we plan at least those issues that have to be finished in the next sprint. During/after this step the team/assignee defines tasks in the issue. They need to be placed in the top-comment, otherwise the task-progress won't be visible and tracked.

### Sprintboard

The Sprintboard contains all issues that are assigned to the current sprint. It reflects the current status of the work. Issues are moved from one column to the other by the corresponding assignee.

- `ToDo` ⇒ `InProgress`: assignee started working on the issue
- `InProgress` ⇒ `Review`: work is done and PR is opened for review
- `Review` ⇒ `Done`: PR is merged considering given restrictions

Some changes to issues and pull requests are reflected on the board automatically:

- Issue/PR closed/merged: Issue to `Done`
- Codechange requested: Issue to `In Progress`
