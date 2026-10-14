# Pet Adoption System — agent guide

Java 21 console app for the OOPROG finals (Group 2, C2B). Regular Users browse pets and apply to adopt. Admins
manage pets, users, applications, adoption records, and statistics. File storage (pipe-delimited `.txt`),
plaintext passwords, no external libraries. The professor grades: Login, CRUD, roles, the 4 OOP pillars,
exception handling, Singleton + Strategy, and "all types of validations".

**Current state: docs only, no Java code yet.** Every class in these docs is *planned*. Never claim a class
exists until you have seen the file.

**Read the doc you need — don't scan the repo.**

---

## Step 1 — which member are you working for?

Four students each own a slice of the code. Ask the user which member they are if it is not obvious, then read
that member's context **before** doing anything else:

| Member | Read first |
|---|---|
| Kurt (core, auth, util, ui) | [`members/kurt/docs/CONTEXT.md`](members/kurt/docs/CONTEXT.md) |
| Julyan (pets) | [`members/julyan/docs/CONTEXT.md`](members/julyan/docs/CONTEXT.md) |
| Ace (adoption applications) | [`members/ace/docs/CONTEXT.md`](members/ace/docs/CONTEXT.md) |
| Derick (users, records, statistics) | [`members/derick/docs/CONTEXT.md`](members/derick/docs/CONTEXT.md) |

Each member folder also has `TASKS.md` (checklist), `NOTES.md` (member-specific notes), and `SESSION_LOG.md`.

## Step 2 — where to look

| Working on… | Read |
|---|---|
| "Where does X live?" / which file to edit | [`docs/MAP.md`](docs/MAP.md) |
| Full file blueprint: every planned class, its fields and method signatures | [`docs/BLUEPRINT.md`](docs/BLUEPRINT.md) |
| Layers, class diagram, OOP pillars, Singleton/Strategy, exceptions | [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) |
| Fields, enums, IDs, file formats | [`docs/DATA-MODEL.md`](docs/DATA-MODEL.md) |
| Status changes, approve/reject, delete rules, who can do what | [`docs/BUSINESS-RULES.md`](docs/BUSINESS-RULES.md) |
| Any input validation | [`docs/VALIDATION.md`](docs/VALIDATION.md) |
| Calling another member's code, build order, Git | [`docs/TEAM.md`](docs/TEAM.md) |
| Code style and patterns | [`docs/CONVENTIONS.md`](docs/CONVENTIONS.md) |
| Something looks wrong or you want to change the design | [`docs/DECISIONS.md`](docs/DECISIONS.md) |
| Debugging something that "should work" | [`docs/GOTCHAS.md`](docs/GOTCHAS.md) |
| Original feature spec (verbatim) | [`docs/reference/GROUP2-SPEC.md`](docs/reference/GROUP2-SPEC.md) — `DECISIONS.md` overrides it |
| What happened recently | [`docs/SESSION_LOG.md`](docs/SESSION_LOG.md) + the member's `SESSION_LOG.md` |

---

## Invariants — do not violate these

1. **Stay in your member's packages.** Editing another member's files needs that member's agreement (say so in
   the PR). Exception: the one-line menu wiring described in [`docs/TEAM.md`](docs/TEAM.md#menu-wiring).
2. **All console input goes through `util.InputHelper`.** Never create a second `Scanner(System.in)`.
3. **All persistence goes through `DataStore` repositories.** Call `save()` after every change. Never read or
   write `data/*.txt` directly.
4. **Singletons are only reached by `getInstance()`.** Never `new DataStore()` or `new SessionManager()`.
5. **Status changes only through domain methods** (`approve()`, `reject()`, `cancel()`, `PetService`
   transitions). No public `setStatus(...)`.
6. **Services enforce role and ownership** with `SessionManager.requireAdmin()` / ownership checks, even when
   the menu already hides the option.
7. **Services never print; menus never hold business rules.** Services throw `PetAdoptionException`
   subclasses; menus catch them and print.
8. **No `|` in stored text.** It is the file delimiter. Changing a file format means updating
   `docs/DATA-MODEL.md` and the parser in the same change.
9. **Java 21 standard library only. Console only.** No external libraries, no GUI, no database.
10. **Follow `docs/DECISIONS.md` over the original spec.** If a decision is marked *Proposed — group confirm*,
    follow it, and do not silently change it.

---

## Verifying your work

No test framework exists. Verification is compile + manual run, from the repository root:

```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out petadoption.Main
```

Then drive the feature through the console, including **invalid input** (blank, letters for numbers,
out-of-range, duplicates, wrong status). Each member's `TASKS.md` lists the acceptance checks.

A clean compile is not proof that a feature works. Report what you actually ran, and say plainly when something
is unverified or failing.

---

## Keeping these docs true

**If a doc contradicts the code, the code wins — fix the doc in the same change.**

| You changed… | Update |
|---|---|
| Added/renamed/moved a class, package, or menu option | `docs/BLUEPRINT.md` + `docs/MAP.md` (and the class diagram in `docs/ARCHITECTURE.md` if structural) |
| Changed a field or method signature | `docs/BLUEPRINT.md` (★ methods: also `docs/TEAM.md`) |
| A field, enum value, ID format, or file format | `docs/DATA-MODEL.md` |
| A status rule, side effect, delete rule, or permission | `docs/BUSINESS-RULES.md` |
| A validation rule or limit | `docs/VALIDATION.md` |
| A design choice, or confirmed/rejected a *Proposed* ADR | `docs/DECISIONS.md` — append or update status, never renumber |
| Hit a non-obvious bug | `docs/GOTCHAS.md` (shared) or the member's `NOTES.md` (only affects them) |
| Finished a work session | Member's `SESSION_LOG.md`; shared `docs/SESSION_LOG.md` if it affects everyone |
| Completed a task | Tick it in the member's `TASKS.md` |
