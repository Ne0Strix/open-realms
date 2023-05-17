# open-realms: an open-deckbuilding game

## Communication

Communication is handled

## Message Types and Data Keys

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
