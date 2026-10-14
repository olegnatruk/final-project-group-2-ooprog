# Validation

The project brief says **"INCLUDE ALL TYPES OF VALIDATIONS!"** This file is the single list of every rule with
exact limits. Limits marked here are Proposed defaults (see [DECISIONS.md](DECISIONS.md)); change them here
first, then in code.

---

## Where validation happens (three layers)

| Layer | What it checks | On failure |
|---|---|---|
| **Input** — `util.InputHelper` (Kurt) | Data type, blank input, menu range, format, length, `\|` character, yes/no, enum choice, date format | Prints `[ERROR] ...` and **asks again** (loops until valid) |
| **Model** — setters in `User`, `Pet`, `AdoptionApplication`, `AdoptionRecord` | Same field rules, using `util.Validator` | Throws `ValidationException` (protects the object even if input was skipped, e.g. file loading) |
| **Service** — `*Service` classes | Uniqueness, existence, status, ownership, role, cross-record rules, business limits | Throws a `PetAdoptionException` subclass; the menu prints it and **returns to the menu** |

`util.Validator` holds the shared static checks so the rules are written once. `InputHelper` calls `Validator`,
catches `ValidationException`, prints it, and asks again.

## Validation types covered

| Type | Examples in this system |
|---|---|
| Required / not blank | Every required field; whitespace-only counts as blank |
| Data type | Age must be a whole number; weight must be a number; menu choice must be a number |
| Range | Age 0–max for type; weight > 0 and ≤ max for type; menu choice 1–N; month 1–12 |
| Length | Names 2–50, username 4–20, password 8–30, reason 20–500, ... |
| Format (regex) | Email, PH contact number, username characters, name characters, ID format, date `yyyy-MM-dd` |
| Uniqueness | Username, email, one active application per user+pet, one record per application |
| Existence (referential) | Pet/user/application/record ID must exist before view/update/delete |
| Status | Only `AVAILABLE` pets accept applications; only `PENDING` apps are editable, etc. |
| Ownership | Users only see/change their own profile, applications, history |
| Role / authorization | Admin-only service methods call `SessionManager.requireAdmin()` |
| Cross-field | Password = confirm password; experience details required only if "has previous pets" = yes; adoption date ≥ application date |
| Business limit | Max 3 `PENDING` applications per user; 3 failed logins in a row |
| Confirmation | y/n before every delete, cancel, approve, reject, revoke, disable |
| Sanitization | Trim input; reject `\|`; normalize email to lower case, IDs to upper case, contact number to `09XXXXXXXXX` |
| Change detection | New password must differ from the current one; manual pet status must differ from current |

---

## Field rules

Text is **trimmed** before checking. "Letters" includes `Ñ`/`ñ`. Every text field also rejects `|` (file
delimiter, ADR-002).

### Accounts (Kurt writes the rules; Derick reuses them)

| Field | Rule | Regex / limit | Error message |
|---|---|---|---|
| First name, Last name | Required, 2–50, letters, spaces, `.`, `'`, `-`; must start with a letter | `^[A-Za-zÑñ][A-Za-zÑñ .'-]{1,49}$` | "First name must be 2–50 letters (spaces, . ' - allowed)." |
| Email | Required, max 100, valid format, unique (case-insensitive). Stored lower case | `^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$` | "Email must look like name@example.com." / "Email is already registered." |
| Contact number | Required. Remove spaces and `-`; convert `+639…` to `09…`; then must be 11 digits starting `09` | `^09\d{9}$` | "Contact number must be 11 digits starting with 09 (e.g. 09171234567)." |
| Address | Required, 5–150 characters | length | "Address must be 5–150 characters." |
| Username | Required, 4–20, letters/digits/underscore, unique (case-insensitive), cannot be changed | `^[A-Za-z0-9_]{4,20}$` | "Username must be 4–20 letters, digits, or _." / "Username is already taken." |
| Password | Required, 8–30, at least 1 letter and 1 digit, no spaces, must not equal the username | `^(?=.*[A-Za-z])(?=.*\d)\S{8,30}$` | "Password must be 8–30 characters with at least one letter and one number, no spaces." |
| Confirm password | Must equal password | equals | "Passwords do not match." |
| Current password (profile change) | Must match stored password (ADR-030) | equals | "Current password is incorrect." |
| New password (profile change) | Password rules + must differ from current | — | "New password must be different from the current password." |

### Login

| Check | Message |
|---|---|
| Username/email blank | "Username or email is required." |
| Password blank | "Password is required." |
| No match / wrong password | "Invalid username/email or password." (same message for both) |
| Account `DISABLED` | "This account is disabled. Please contact an administrator." |
| 3rd failure in a row | "Too many failed attempts. Returning to the main menu." |

### Pets (Julyan)

| Field | Rule | Regex / limit |
|---|---|---|
| Type | Choose from numbered list (`DOG`/`CAT`/`BIRD`) | enum |
| Name | Required, 1–30, letters/digits/spaces/`.'-`, starts with letter or digit | `^[A-Za-z0-9Ññ][A-Za-z0-9Ññ .'-]{0,29}$` |
| Breed | Required, 2–40, letters/spaces/`.'()-` | `^[A-Za-zÑñ][A-Za-zÑñ .'()-]{1,39}$` |
| Age (years) | Whole number, 0 to type max (Dog 25, Cat 30, Bird 80) | `getMaxAgeYears()` override |
| Gender | Choose from list | enum |
| Color | Required, 2–30, letters/spaces/`,/-` | `^[A-Za-z][A-Za-z ,/-]{1,29}$` |
| Weight (kg) | Number, > 0, ≤ type max (Dog 100, Cat 15, Bird 2), max 2 decimal places | `getMaxWeightKg()` override |
| Description | Required, 10–300 characters | length |
| Health info | Required, 3–200 characters | length |
| Vaccination status | Choose from list | enum |
| Dog size / Cat indoor-only / Bird can-fly | Choose from list / y-n / y-n | enum / boolean |
| Manual status | Must differ from current, and must match application/record state | [BUSINESS-RULES.md](BUSINESS-RULES.md#manual-pet-status-update) |

The age and weight maximums come from the **subclass** (`Dog`, `Cat`, `Bird` override `getMaxAgeYears()` and
`getMaxWeightKg()`). This is a deliberate Polymorphism example.

### Adoption applications (Ace)

| Field / check | Rule |
|---|---|
| User | Must be logged in as `REGULAR_USER` (admins cannot apply, ADR-024) |
| Pet ID | Format `P` + 4 digits; pet must exist; status must be `AVAILABLE` |
| Duplicate | No `PENDING`/`APPROVED` application by this user for this pet (ADR-017) |
| Limit | User has fewer than 3 `PENDING` applications |
| Address, contact number | Same rules as accounts; pre-filled from profile, Enter keeps |
| Occupation | Required, 2–50, letters/spaces/`.,'&/-` — regex `^[A-Za-zÑñ][A-Za-zÑñ .,'&/-]{1,49}$` |
| Living situation | Choose from list (enum) |
| Has previous pets | y/n |
| Experience details | Required **only if** has previous pets = yes; 10–300 characters |
| Reason for adoption | Required, 20–500 characters |
| Update / cancel | Application must belong to the user and be `PENDING` |
| Reject / revoke reason | Choose from list; "Other" requires 5–200 characters (ADR-028) |

### Adoption records and statistics (Derick)

| Field / check | Rule |
|---|---|
| Application ID | Format `APP` + 4 digits; must exist, be `APPROVED`, and have no record yet |
| Adoption date | `yyyy-MM-dd`, real calendar date, not in the future, not before the application date. Enter = today |
| Notes | Optional, max 200 characters |
| Statistics year | Whole number, 2000 to current year |
| Statistics month | Whole number, 1–12; year + month cannot be in the future |

### Admin user management (Derick)

| Check | Rule |
|---|---|
| Add user | All account rules above + choose role |
| Disable / delete self | Not allowed |
| Disable / delete last active admin | Not allowed |
| Delete user with applications or records | Not allowed — suggest disable |
| Enable an already active user / disable an already disabled user | `ValidationException` ("Account is already Active.") |

---

## Generic input rules (`InputHelper`)

| Input kind | Rule | Message |
|---|---|---|
| Menu choice | Whole number from the listed range (options are numbered 1–N; the last option is always Back / Logout / Exit) | "Please enter a number from 1 to 5." |
| Whole number | Parsed with `Integer.parseInt`; `NumberFormatException` caught | "Please enter a whole number." |
| Decimal number | Parsed with `Double.parseDouble`; reject `NaN`/`Infinity` | "Please enter a valid number." |
| Yes/No | `y`, `yes`, `n`, `no` (any case) | "Please enter y or n." |
| Enum choice | Numbered list of labels; pick by number | "Please enter a number from 1 to 3." |
| Date | `yyyy-MM-dd`, parsed with `LocalDate.parse` (rejects `2026-02-30`) | "Please enter a valid date as yyyy-MM-dd." |
| ID | Trim, upper-case, must match the entity's ID pattern | "Pet ID must look like P0001." |
| Search keyword | 1–50 characters | "Search keyword must be 1–50 characters." |
| Optional field on update forms | Blank (just Enter) keeps the current value; any other input is fully validated | — |
| Any text | Reject `\|` | "The \| character is not allowed." |

## Confirmations

Ask `Are you sure you want to <action>? (y/n)` before: delete pet, delete user, disable user, cancel
application, delete application, approve application, reject application, revoke approval, create adoption
record, delete adoption record, and saving any update form. Logout is not confirmed. Answering `n` prints
"Action cancelled." and changes nothing.

## Message style

- Errors: `[ERROR] <message>` — one line, says what is wrong **and** what is expected.
- Success: `[OK] <message>` — e.g. `[OK] Application APP0003 submitted. Status: Pending.`
- Info: `[INFO] <message>` — e.g. `[INFO] No pets match your filters.`
- Never print a stack trace to the user. Unexpected exceptions are caught in `Main` and shown as
  `[ERROR] Something went wrong: <message>`.
