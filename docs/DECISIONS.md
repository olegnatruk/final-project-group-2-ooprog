# Decisions

Architecture Decision Records. Numbered, **never renumbered or deleted** — superseded ones are marked
"Superseded by ADR-0XX" and kept.

**Status values**
- **Accepted** — the group decided this.
- **Proposed — group confirm** — a default chosen to fill a gap or contradiction in the original spec
  ([reference/GROUP2-SPEC.md](reference/GROUP2-SPEC.md)). Code follows it until the group confirms or changes
  it. To change one, edit its status to `Accepted` or `Superseded by ADR-0XX` and update the docs it affects.
  Never silently change a Proposed rule in code.

When this file and the original spec disagree, **this file wins**.

---

## Foundation decisions

### ADR-001 — Console user interface
**Status:** Accepted
**Context.** The project may be console or GUI.
**Decision.** Console only. Text menus with numbered options.
**Consequences.** All input goes through `util.InputHelper`. "Summary cards" in the spec are printed as text
tables or blocks. Do not add Swing/JavaFX.

### ADR-002 — File storage: pipe-delimited text files in `data/`
**Status:** Accepted (format details: Proposed — group confirm)
**Context.** Data must survive between runs; no database.
**Decision.** One file per entity in `data/` (`users.txt`, `pets.txt`, `applications.txt`, `records.txt`).
One record per line, fields separated by `|`. All files load at startup; the affected file is rewritten after
every change. `data/` is git-ignored, because runtime data would cause merge conflicts on every commit.
**Consequences.** `|` is forbidden in all text input (see [VALIDATION.md](VALIDATION.md)). Exact layouts are
in [DATA-MODEL.md](DATA-MODEL.md). A missing file is treated as empty and created on first save. If the
group wants demo data in Git, add a `sample-data/` folder later (new ADR).

### ADR-003 — Passwords stored as plaintext
**Status:** Accepted
**Context.** Security is not a grading criterion; hashing adds complexity.
**Decision.** Store passwords as plain text in `users.txt`.
**Consequences.** Never present this as secure. Do not add hashing unless the group decides to (new ADR). Do
not reuse real passwords when testing.

### ADR-004 — One shared `src/` tree; member folders hold docs only
**Status:** Accepted
**Context.** Four members work in parallel. Per-member code folders would break Java packages and compiling.
**Decision.** One source tree, `src/petadoption/`, with one package per domain. Ownership is per package
(and per file where packages are shared) — see [TEAM.md](TEAM.md). `members/<name>/docs/` holds that
member's agent context, tasks, notes, and log.
**Consequences.** Merge conflicts stay inside packages. Shared hot spots (`Main`, `ui/UserMenu`,
`ui/AdminMenu`) are owned by Kurt, with a one-line wiring exception.

### ADR-005 — Java 21, standard library only, plain `javac`
**Status:** Accepted
**Decision.** JDK 21. No Maven/Gradle, no external JARs. Compile with `javac -d out`.
**Consequences.** Java 21 features (records, switch expressions, pattern matching for `instanceof`, text
blocks) are allowed. Keep core model classes as regular classes, not `record`s, because they need private
fields with validating setters (Encapsulation is graded).

### ADR-006 — Single login screen with role routing
**Status:** Proposed — group confirm
**Context.** The spec lists "Login" and "Admin Login" as separate features.
**Decision.** One login screen accepts username or email + password. After login, the user's role opens
either `UserMenu` or `AdminMenu`. Admin-only service methods also check the role.
**Consequences.** Both spec features are covered by one flow. A Regular User can never reach admin functions,
because routing is by stored role and services re-check it.

### ADR-007 — Seeded default admin account
**Status:** Proposed — group confirm
**Context.** Registration creates Regular Users only, so the first admin must come from somewhere.
**Decision.** At startup, if no Admin exists in `users.txt`, create one: username `admin`, password
`admin123`, email `admin@petadoption.com`, name "System Administrator", contact `09000000000`,
address "Pet Adoption Center".
**Consequences.** The README documents these credentials for the demo.

### ADR-008 — Soft delete where history matters
**Status:** Accepted (spec states this)
**Decision.** Users with applications or records are disabled, not deleted. A user's "Cancel application" is a
status change to `CANCELLED`, not a removal. Hard deletes are allowed only where
[BUSINESS-RULES.md §Delete rules](BUSINESS-RULES.md#delete-rules) permits.
**Consequences.** CRUD "Delete" for users/applications is satisfied by the hard delete paths that remain
(admin deleting a user with no history; admin deleting invalid applications).

### ADR-009 — Central `DataStore` Singleton with a generic `Repository<T>`
**Status:** Proposed — group confirm
**Context.** Four members need the same data without each writing file I/O.
**Decision.** `core.DataStore` (Singleton) owns four `Repository<T>` objects: `users()`, `pets()`,
`applications()`, `records()`. Each repository holds an in-memory list, knows its file, and offers `add`,
`remove`, `findById`, `getById`, `findAll`, `nextId()`, and `save()` (`find(SearchStrategy<T>)` was removed by
ADR-032). Each domain
supplies a parser that turns one file line into an object.
**Consequences.** Only Kurt writes file I/O. Members only call repository methods. See
[ARCHITECTURE.md](ARCHITECTURE.md#persistence).

### ADR-010 — Strategy pattern for search/filter (all record types) and statistics
**Status:** Proposed — group confirm
**Decision.** `core.SearchStrategy<T>` is the Strategy interface (`boolean matches(T item)`). Each domain writes
concrete strategies (e.g. `PetTypeFilter`, `UserEmailSearch`). `core.RecordFinder<T>` is the context class.
`core.AllOfStrategy<T>` combines several filters (AND). Separately, `record.StatisticsStrategy` with
`MonthlyStatisticsStrategy` and `YearlyStatisticsStrategy`, used by `StatisticsService`.
**Consequences.** Strategy is visible in three members' code, so any member can explain it at the defense.
Amended by ADR-032 (every search goes through `RecordFinder.setStrategy`).

### ADR-011 — Checked custom exception hierarchy
**Status:** Proposed — group confirm
**Decision.** `PetAdoptionException extends Exception` (checked), with subclasses `ValidationException`,
`AuthenticationException`, `UnauthorizedAccessException`, `RecordNotFoundException`, `DuplicateRecordException`,
`InvalidStatusTransitionException`, `DataFileException`. Services throw; menus catch and print.
**Consequences.** The compiler forces handling, which makes exception handling visible to the grader. The
reasoning for checked vs unchecked is in ADR-033. See
[ARCHITECTURE.md §Exceptions](ARCHITECTURE.md#exception-handling).

### ADR-012 — Abstract `Menu` base class (Template Method)
**Status:** Proposed — group confirm
**Decision.** `ui.Menu` is abstract with a `final run()` loop that prints the title and options, reads a choice,
calls the abstract `handleChoice(int)`, and catches `PetAdoptionException`. Every menu extends it.
**Consequences.** Consistent menus and error handling everywhere. Polymorphism and inheritance appear in every
member's code.

---

## Spec gap decisions (all Proposed — group confirm)

### ADR-013 — Application approval side effects
**Status:** Proposed — group confirm
**Context.** Spec §5.3 says other pending applications "may be reviewed or rejected".
**Decision.** Approving an application sets the pet to `PENDING_ADOPTION` and **automatically rejects** every
other `PENDING` application for the same pet, with reason "Pet already has an approved adopter".
**Consequences.** One approved adopter per pet at a time. See [BUSINESS-RULES.md](BUSINESS-RULES.md).

### ADR-014 — Pet status is driven by the workflow; manual updates are consistency-checked
**Status:** Proposed — group confirm
**Context.** The spec has an admin "Update Pet Status" feature but no trigger for Pending Adoption → Adopted.
**Decision.** Normal transitions happen automatically: approve → `PENDING_ADOPTION`; create adoption record →
`ADOPTED`; revoke approval → `AVAILABLE`; delete adoption record → `PENDING_ADOPTION`. The admin's manual
"Update Pet Status" is allowed only when the new status matches the pet's application/record state (rules in
[BUSINESS-RULES.md](BUSINESS-RULES.md#manual-pet-status-update)). New pets always start `AVAILABLE`.
**Consequences.** Pet status can never contradict the application and record data.

### ADR-015 — Admin can revoke an approval
**Status:** Proposed — group confirm
**Context.** The spec has no path back if an approved adopter withdraws.
**Decision.** Admin action "Revoke approval": `APPROVED` → `REJECTED` with a required reason. Allowed only if no
adoption record exists for that application. The pet returns to `AVAILABLE`.

### ADR-016 — No `COMPLETED` application status
**Status:** Proposed — group confirm
**Decision.** Application statuses are exactly `PENDING`, `APPROVED`, `REJECTED`, `CANCELLED`. An adoption is
"completed" when an `AdoptionRecord` exists for an `APPROVED` application.

### ADR-017 — Duplicate and limit rules for applications
**Status:** Proposed — group confirm
**Context.** The spec says both "no duplicate active applications" and "no duplicate applications".
**Decision.** A user cannot have another `PENDING` or `APPROVED` application for the same pet. Reapplying after
`REJECTED` or `CANCELLED` is allowed if the pet is `AVAILABLE`. A user may have at most **3** `PENDING`
applications at once.

### ADR-018 — Pet subclasses: `Dog`, `Cat`, `Bird`
**Status:** Proposed — group confirm
**Decision.** Abstract `Pet` with three subclasses. Each adds one type-specific field and overrides type-specific
behavior: `Dog` → `size` (`SMALL`/`MEDIUM`/`LARGE`); `Cat` → `indoorOnly` (boolean); `Bird` → `canFly`
(boolean). Each subclass overrides `getMaxAgeYears()`, `getMaxWeightKg()`, and `getTypeSpecificDetails()`.
**Consequences.** Adding a type (e.g. `Rabbit`) means a new subclass, a new `PetType` value, and parser/doc
updates.

### ADR-019 — Pet age in whole years
**Status:** Proposed — group confirm
**Context.** Spec says "positive", which would reject newborn pets.
**Decision.** Age is an integer number of years from 0 to the type's maximum (0 means "under 1 year").

### ADR-020 — What Regular Users see when browsing
**Status:** Proposed — group confirm
**Context.** Spec §1.5 says "available for adoption" but also shows adoption status and lets users filter by it.
**Decision.** Regular Users see pets with status `AVAILABLE` or `PENDING_ADOPTION`. `ADOPTED` pets are hidden
from them. Only `AVAILABLE` pets can receive applications. Admins see all pets.

### ADR-021 — Adoption record fields and editing
**Status:** Proposed — group confirm
**Context.** The spec lists an "Adoption Status" field on records with no defined values.
**Decision.** Records have no status field: `recordId`, `applicationId`, `userId`, `petId`, `adoptionDate`,
`notes`. Only `adoptionDate` and `notes` are editable. Deleting a record (for records created by mistake)
returns the pet to `PENDING_ADOPTION`; the application stays `APPROVED`.

### ADR-022 — Delete restrictions protect references
**Status:** Proposed — group confirm
**Decision.** A pet with **any** application (any status) or record cannot be deleted. A user with any
application or record cannot be deleted (disable instead). `APPROVED` applications cannot be deleted. Details
in [BUSINESS-RULES.md §Delete rules](BUSINESS-RULES.md#delete-rules).
**Consequences.** No file ever references a deleted ID.

### ADR-023 — Admin account safety
**Status:** Proposed — group confirm
**Decision.** Role is chosen when an account is created (admins may create Regular Users or Admins) and can
never be changed afterwards. An admin cannot disable or delete their own account. The last active admin cannot
be disabled or deleted.

### ADR-024 — Admins do not adopt
**Status:** Proposed — group confirm
**Context.** The spec's access table gives admins "View Own Adoption History" but forbids them from applying.
**Decision.** Admins cannot submit applications and have no personal adoption history. They use the full
adoption history view instead.

### ADR-025 — Adoption statistics are in scope
**Status:** Proposed — group confirm
**Context.** "View Adoption Statistics" appears only in the spec's first feature table.
**Decision.** Included, owned by Derick, using the Strategy pattern (ADR-010).

### ADR-026 — Application form fields
**Status:** Proposed — group confirm
**Decision.** "Living situation" is an enum (`HOUSE_WITH_YARD`, `HOUSE_WITHOUT_YARD`, `APARTMENT_OR_CONDO`,
`OTHER`). "Previous pet ownership experience" is a yes/no answer, with details required only when the answer
is yes. Applicant name is copied from the profile at submission and cannot be edited on the application.
Contact number and address are pre-filled from the profile and can be changed.

### ADR-027 — Login attempt limit
**Status:** Proposed — group confirm
**Decision.** After 3 failed login attempts in a row, return to the welcome menu with a message. No permanent
lockout (nothing to persist).

### ADR-028 — Rejection reason is required
**Status:** Proposed — group confirm
**Context.** The spec says the reason is optional.
**Decision.** The admin picks a reason from a list (Incomplete information / Does not meet requirements / Pet
already has an approved adopter / Duplicate application / Other). "Other" requires typed text. The reason is
stored and shown to the applicant.

### ADR-029 — Search and filter live inside each management menu
**Status:** Proposed — group confirm
**Decision.** There is no global "Search Records" menu. Each list screen (pets, users, applications, records)
has its own Search and Filter options, owned by that domain's member.
**Consequences.** Fewer shared files to edit; each member owns their search strategies.

### ADR-030 — Changing a password requires the current password
**Status:** Proposed — group confirm
**Decision.** In "Manage Profile", changing the password asks for the current password first. The new password
must differ from the current one. Admins resetting another user's password do not need the old one.

---

## Course-module alignment (added after checking the Design Patterns and Exception Handling slides)

### ADR-031 — Nested try-catch in `Repository.load()`
**Status:** Proposed — group confirm
**Context.** The Exception Handling module (Topic 5) teaches nested try-catch; no planned file used it.
**Decision.** `Repository.load()` uses two levels. The **outer** `try` covers reading the whole file
(`FileStorage.readLines`). Its `catch (DataFileException e)` adds the file name to the message and re-throws a new
`DataFileException` with `e` as the cause (log/add context, then re-throw). The **inner** `try` sits inside the
loop and covers parsing one line. Its `catch (PetAdoptionException e)` records a warning and continues, so one
bad line does not stop the other lines from loading.
**Consequences.** Nested try-catch, re-throw with context, and exception chaining are all in one short method,
easy to show at the defense. Errors are handled at two levels: file (stop loading) and line (skip and continue).

### ADR-032 — Every search goes through `RecordFinder` and `setStrategy`
**Status:** Proposed — group confirm
**Context.** The Design Patterns module (Topic 4) shows the Strategy **context** class holding a strategy and
swapping it at runtime with `setStrategy`. The plan had a `Repository.find(strategy)` shortcut, so menu and service
code could skip the context class completely.
**Decision.** Remove `Repository.find(...)`. Each service that searches (`PetService`, `UserService`,
`ApplicationService`, `RecordService`) holds a `private final RecordFinder<T> finder` field. Its
`search(SearchStrategy<T> strategy)` method calls `finder.setStrategy(strategy)` and then
`finder.find(...)`. `RecordFinder` gets two constructors, `RecordFinder()` and `RecordFinder(SearchStrategy<T>)`,
matching the module's `Sorter` example. `StatisticsService` keeps `setStrategy` and also gets a constructor that
takes a strategy.
**Consequences.** The context class and the runtime swap appear in four members' code. For the demo: search pets
by keyword, then filter the same list by type — the same `RecordFinder` swaps its strategy at runtime. Supersedes
the `find` method in ADR-009.

### ADR-033 — Custom exceptions are checked; unchecked only for programming errors
**Status:** Proposed — group confirm
**Context.** The module's rule (Topic 6): "Prefer unchecked for programming errors, checked for recoverable
conditions." The professor may ask why the custom exceptions extend `Exception`.
**Decision.** All custom exceptions are checked (`PetAdoptionException extends Exception`), because every one of
them is a **recoverable condition**: bad input, wrong status, missing ID, duplicate, wrong role, unreadable file.
The program reports them and continues, and the compiler forces every caller to handle or declare them.
Unchecked exceptions are only the JDK's own, and each is either caught where it happens or a programming error:

| Unchecked exception | Where | Handling |
|---|---|---|
| `NumberFormatException` | `InputHelper`, parsers | Caught: re-prompt / skip line |
| `DateTimeParseException` | `InputHelper.readDate`, parsers | Caught: re-prompt / skip line |
| `IllegalArgumentException` (`Enum.valueOf`) | Parsers | Caught (multi-catch): skip line |
| `ArrayIndexOutOfBoundsException` | Parsers (too few fields) | Caught (multi-catch): skip line |
| `NoSuchElementException` | `InputHelper` when input ends | Caught: exit cleanly |
| `UnsupportedOperationException` | Skeleton methods (Phase 2) | Programming error — all skeleton code and its catch blocks removed 2026-10-21 |
| `NullPointerException` | Anywhere | Programming error — never caught on purpose; `Main`'s safety net reports it |

**Consequences.** No custom unchecked exceptions. Never catch `Throwable` or `Error`.

### ADR-034 — Thread-safe Singletons with `synchronized getInstance()`
**Status:** Proposed — group confirm
**Context.** The Design Patterns module (Topic 3) says the basic lazy Singleton is not thread-safe and lists
fixes. The professor may ask.
**Decision.** `DataStore.getInstance()` and `SessionManager.getInstance()` are `public static synchronized`. The
module calls this option "simple but slow"; the cost does not matter for a console app.
**Consequences.** Defense answer: "Our app has one thread, so it is already safe, but we made `getInstance()`
`synchronized` so two threads could never create two instances. Double-checked locking with `volatile` or an
enum Singleton are faster options; we chose the simplest."

<!--
Add new ADRs below, numbered sequentially from ADR-035.
Format:

### ADR-0XX — <the decision, not the problem>
**Status:** Accepted | Proposed — group confirm | Superseded by ADR-0YY
**Context.** ...
**Decision.** ...
**Consequences.** ...
-->
