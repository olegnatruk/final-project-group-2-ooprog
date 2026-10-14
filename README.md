# Pet Adoption System — Group 2 (OOPROG Finals)

A Java console application where Regular Users browse pets, view each pet's **Pet Passport**, and apply to
adopt. Admins manage pets, user accounts, adoption applications, completed adoption records, and adoption
statistics.

> **Status:** Documentation and architecture only. No Java source code exists yet. Every class named in these
> docs is *planned*. When code lands, the code is the source of truth and the docs must be updated to match.

---

## Team

| Folder | Member | Owns (see [docs/TEAM.md](docs/TEAM.md)) |
|---|---|---|
| `members/kurt/` | Barbin, Kurt Angelo | Core: `Main`, `core`, `auth`, `util`, `exception`, `ui`, user models |
| `members/julyan/` | Lingao, Julianne Therese | Pets: `pet` package (pet CRUD, browse, Pet Passport, search/filter) |
| `members/ace/` | Cayabyab, Ace Justine | Adoption applications: `application` package |
| `members/derick/` | Inandan, Derick | User management, profile, adoption records, history, statistics: `user` services + `record` package |

Section: **C2B**

---

## Course requirements checklist

| Requirement | Where it is satisfied |
|---|---|
| Login | `auth` package — single login screen with role routing ([ADR-006](docs/DECISIONS.md)) |
| CRUD | Users, Pets, Adoption Applications, Adoption Records — see [CRUD summary](#crud-summary) |
| Different roles | `Admin` and `RegularUser`, both extending abstract `User` |
| Encapsulation | Private fields + validating setters in every model ([ARCHITECTURE.md §OOP](docs/ARCHITECTURE.md#four-pillars-of-oop)) |
| Abstraction | Abstract `User`, `Pet`, `Menu`; interfaces `Storable`, `SearchStrategy<T>`, `StatisticsStrategy` |
| Inheritance | `User` → `Admin`/`RegularUser`; `Pet` → `Dog`/`Cat`/`Bird`; `Menu` → all menus; exception hierarchy |
| Polymorphism | Pet type-specific overrides, menu `handleChoice`, strategy implementations, `InputHelper` overloads |
| Exception handling | Custom checked hierarchy under `PetAdoptionException` ([ARCHITECTURE.md §Exceptions](docs/ARCHITECTURE.md#exception-handling)) |
| Singleton (GoF) | `DataStore`, `SessionManager` |
| Strategy (GoF) | `SearchStrategy<T>` (search/filter for every record type) and `StatisticsStrategy` (monthly/yearly) |
| All types of validation | [docs/VALIDATION.md](docs/VALIDATION.md): required, type, range, format, length, uniqueness, status, ownership, role, confirmation, cross-field, referential |

---

## Tech stack

- **Java 21** (standard library only — no external libraries, no build tool)
- **Console UI** (no GUI)
- **File storage**: pipe-delimited text files in `data/` ([docs/DATA-MODEL.md](docs/DATA-MODEL.md))
- **Passwords stored as plaintext** — deliberate course-scope decision ([ADR-003](docs/DECISIONS.md))

---

## Build and run

Run all commands from the **repository root**. The app reads and writes `data/` relative to the current
working directory.

**macOS / Linux**

```bash
javac --release 21 -encoding UTF-8 -d out $(find src -name "*.java")
```

```bash
java -cp out petadoption.Main
```

**Windows (Command Prompt)**

```bat
dir /s /b src\*.java > sources.txt
javac --release 21 -encoding UTF-8 -d out @sources.txt
java -cp out petadoption.Main
```

`--release 21` makes any JDK 21 or newer compile exactly to Java 21, so code that needs a newer Java fails on
every machine, not just some.

**IDE (IntelliJ / VS Code / NetBeans):** mark `src/` as the source root, set the main class to
`petadoption.Main`, and set the working directory to the repository root.

On first run, the app creates `data/` and seeds one admin account (see [ADR-007](docs/DECISIONS.md)):
username `admin`, password `admin123`.

---

## Folder layout

```
ooprog-group2-finals/
├── README.md              ← you are here (humans + agents)
├── AGENTS.md              ← agent entry point (router)
├── CLAUDE.md              ← loads AGENTS.md for Claude Code
├── PROJECT-BRIEF.md       ← why the project exists, scope, non-goals
├── docs/                  ← shared single source of truth
│   ├── BLUEPRINT.md       ← every planned package + .java file: owner, fields, method signatures
│   ├── ARCHITECTURE.md    ← layers, class diagram, OOP + patterns, exceptions
│   ├── MAP.md             ← package/class map, "where do I change X?", menu tree
│   ├── DATA-MODEL.md      ← entities, enums, IDs, file formats
│   ├── BUSINESS-RULES.md  ← status flows, side effects, delete rules, role access
│   ├── VALIDATION.md      ← every validation rule with exact limits + messages
│   ├── TEAM.md            ← ownership, cross-member contracts, build order, Git workflow
│   ├── CONVENTIONS.md     ← coding rules
│   ├── DECISIONS.md       ← ADRs (incl. proposed answers to spec gaps)
│   ├── GOTCHAS.md         ← known pitfalls
│   ├── SESSION_LOG.md     ← shared dated history
│   └── reference/GROUP2-SPEC.md  ← original group spec (verbatim)
├── members/
│   ├── kurt/docs/         ← CONTEXT, TASKS, NOTES, SESSION_LOG (per member)
│   ├── julyan/docs/
│   ├── ace/docs/
│   └── derick/docs/
├── src/petadoption/       ← (planned) Java source, one package per domain — see docs/BLUEPRINT.md
└── data/                  ← (created at runtime, git-ignored) users/pets/applications/records .txt
```

---

## Architecture summary

Three layers. Full detail: [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md). File-by-file blueprint (84 planned
`.java` files with owners and method signatures): [docs/BLUEPRINT.md](docs/BLUEPRINT.md).

```
Menus (ui + each domain's *Menu classes)   ← console I/O only, via util.InputHelper
        │ calls
Services (AuthService, PetService, ApplicationService, UserService, RecordService, StatisticsService)
        │ business rules, validation, role checks; throw PetAdoptionException subclasses
        ▼
DataStore (Singleton) → Repository<T> per entity → FileStorage → data/*.txt
```

`SessionManager` (Singleton) holds the logged-in user. Menus catch exceptions and print readable errors;
services never print.

---

## Features

The full feature list is in [docs/reference/GROUP2-SPEC.md](docs/reference/GROUP2-SPEC.md). Where the spec was
vague or contradictory, [docs/DECISIONS.md](docs/DECISIONS.md) wins.

**Regular User:** register, login/logout, manage profile, browse pets, view Pet Passport, search and filter
pets, submit adoption application, view own applications, update pending application, cancel pending
application, view own adoption history.

**Admin:** login/logout, manage pets (CRUD), update pet status, manage users (CRUD + disable), view/review all
applications, approve/reject applications, delete invalid applications, manage adoption records (CRUD), view
adoption history, search and filter all record types, view adoption statistics (monthly/yearly).

### CRUD summary

| Record | Create | Read | Update | Delete |
|---|---|---|---|---|
| User account | User (register) / Admin | User (own) / Admin | User (own profile) / Admin | Admin (or disable) |
| Pet | Admin | User / Admin | Admin | Admin |
| Adoption application | User | User (own) / Admin | User (own, Pending) / Admin (approve/reject) | User (cancel) / Admin (delete invalid) |
| Adoption record | Admin | User (own) / Admin | Admin | Admin |

---

## Git workflow (short version)

Full version: [docs/TEAM.md §Git workflow](docs/TEAM.md#git-workflow).

1. `git pull origin main` before starting work.
2. Create a branch: `<name>/<feature>`, for example `julyan/pet-crud`.
3. Commit small, focused changes. Commit message format: `feat(pet): add Dog subclass`.
4. Push the branch and open a Pull Request into `main`. At least one other member reviews.
5. Never push directly to `main`. Never commit `out/`, `*.class`, or `data/`.

---

## For AI agents

Start at [AGENTS.md](AGENTS.md). Then read `members/<your-member>/docs/CONTEXT.md` for the member you are
working for.

## Defense reminder

Each member must be able to explain every line of the code they own. The professor may ask any member about
any pillar or pattern. Use AI to help, but understand the code before you commit it.
