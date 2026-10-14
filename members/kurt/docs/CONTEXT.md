# Kurt — agent context

**Member:** Barbin, Kurt Angelo
**Role in the team:** foundation and integration. Everyone else builds on Kurt's code, so Kurt's Phase 1 must
merge first ([TEAM.md §Build order](../../../docs/TEAM.md#build-order)).

Read with: [AGENTS.md](../../../AGENTS.md) → this file → [TASKS.md](TASKS.md) → [NOTES.md](NOTES.md) →
[SESSION_LOG.md](SESSION_LOG.md).

---

## What Kurt owns

| Package / file | Contents |
|---|---|
| `Main.java` | Startup, top-level catch, `finally` |
| `core/*` | `Storable`, `RecordParser`, `FileStorage`, `Repository<T>`, `DataStore` (Singleton), `SearchStrategy<T>`, `AllOfStrategy<T>`, `RecordFinder<T>` |
| `auth/*` | `SessionManager` (Singleton), `AuthService`, `WelcomeMenu` |
| `ui/*` | `Menu` (abstract), `UserMenu`, `AdminMenu` |
| `util/*` | `InputHelper`, `Validator`, `TextRule`, `ConsoleUI` |
| `exception/*` | `PetAdoptionException` + 7 subclasses |
| `user/` models | `User` (abstract), `Admin`, `RegularUser`, `Role`, `AccountStatus` |

Do **not** edit: `pet/*` (Julyan), `application/*` (Ace), `record/*` and `user/UserService`,
`user/ProfileMenu`, `user/UserManagementMenu`, `user/search/*` (Derick).

## Spec features owned

- Register Account (spec 1.1) — always creates a `RegularUser`.
- Login / Logout (spec 1.2, 1.3) and Admin Login / Logout (spec 2.1, 2.2) — one login screen (ADR-006).
- Seeded default admin (ADR-007).
- Role-based routing to `UserMenu` / `AdminMenu`.
- The validation framework, exception hierarchy, file storage, and menu framework used by all features.

## What Kurt must demonstrate for grading

| Requirement | Kurt's example |
|---|---|
| Singleton | `DataStore`, `SessionManager` — `synchronized getInstance()`; be ready to explain thread safety (ADR-034) |
| Strategy | `SearchStrategy<T>` interface, `AllOfStrategy<T>`, `RecordFinder<T>` context with two constructors + `setStrategy` (ADR-032) |
| Abstraction | Abstract `User`, abstract `Menu`, interfaces `Storable`, `RecordParser`, `SearchStrategy`, `TextRule` |
| Inheritance | `User` → `Admin`/`RegularUser`; exception hierarchy; `Menu` → `WelcomeMenu`/`UserMenu`/`AdminMenu` |
| Polymorphism | `User.getRole()`, `User.canApplyForAdoption()` overrides; `InputHelper` overloads; `Menu.handleChoice` |
| Encapsulation | `User` private fields + validating setters; no setter for `userId`/`username`/`role` |
| Exception handling | Custom checked hierarchy (why checked: ADR-033); try-with-resources in `FileStorage`; **nested try-catch + re-throw with context in `Repository.load()`** (ADR-031); multi-catch in `User.fromRecord`; chaining in `DataFileException`; multiple catch blocks + `finally` in `Main` |

## Key specs to follow

- **File blueprint (your files, fields, signatures):** [BLUEPRINT.md](../../../docs/BLUEPRINT.md#petadoption--kurt)
- Contracts Kurt must provide (exact signatures): [TEAM.md §Kurt provides](../../../docs/TEAM.md#kurt-provides)
- Layers, Singleton, Strategy, exceptions, persistence: [ARCHITECTURE.md](../../../docs/ARCHITECTURE.md)
- File formats, `users.txt` layout, ID format: [DATA-MODEL.md](../../../docs/DATA-MODEL.md)
- Account + login validation, `InputHelper` rules, message style: [VALIDATION.md](../../../docs/VALIDATION.md)
- Login rules, account rules: [BUSINESS-RULES.md](../../../docs/BUSINESS-RULES.md#login-rules)
- Menu tree for `WelcomeMenu`, `UserMenu`, `AdminMenu`: [MAP.md §Menu tree](../../../docs/MAP.md#menu-tree)

## Acceptance criteria

**Register**
- All account rules in VALIDATION.md enforced, with re-prompting on bad input.
- Duplicate username or email (case-insensitive) is rejected.
- New account is `REGULAR_USER`, `ACTIVE`, gets the next `U####` ID, and is saved to `data/users.txt`.

**Login / logout**
- Accepts username or email (any case) + exact password.
- Wrong credentials → one generic message; 3 failures in a row → back to welcome menu.
- `DISABLED` account cannot log in.
- Admin lands in `AdminMenu`; Regular User lands in `UserMenu`.
- Logout clears `SessionManager`; protected menus are unreachable until the next login.

**Foundation**
- Fresh clone, empty `data/`: app starts, creates `data/`, seeds `admin`/`admin123` as `U0001`.
- A corrupted line in any data file is skipped with a warning; the rest loads.
- `InputHelper` never crashes on letters, blanks, huge numbers, or end of input.
- `UserMenu`/`AdminMenu` list every option from MAP.md, with `Coming soon` placeholders until members wire
  their menus.

## Dependencies on others

Kurt's code depends on others only in `DataStore`, which registers each repository with its parser
(`Pet::fromRecord`, `AdoptionApplication::fromRecord`, `AdoptionRecord::fromRecord`). Wire each repository when
that member's model is merged. Until then, leave the repository out (do not write a stand-in model class).
