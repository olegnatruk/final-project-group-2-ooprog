# Conventions

No code exists yet, so these are **agreed rules**, not observed patterns. Once code exists, if the team
consistently does something different, update this file to match reality.

## Naming

- Packages: lower case — `petadoption.pet`, `petadoption.pet.search`.
- Classes: `PascalCase` nouns — `PetService`, `AdoptionApplication`. Menus end in `Menu`; form screens end in
  `Screen`; services end in `Service`; search strategies end in `Search` (keyword) or `Filter` (category).
- Methods and variables: `camelCase`. Booleans read as questions: `isAvailable()`, `hasRecordForPet()`.
- Constants: `UPPER_SNAKE_CASE` — `MAX_PENDING_APPLICATIONS = 3`.
- Enums: `UPPER_SNAKE_CASE` values, plus a `getLabel()` for display.

## Classes and encapsulation

- Every field is `private`. Use `final` for values that never change (IDs, `dateCreated`, `applicationDate`).
- Getters for what others need to read. Setters only for editable fields (see [DATA-MODEL.md](DATA-MODEL.md)).
- Setters validate through `Validator` and throw `ValidationException`. An object must never hold an invalid
  value.
- No setter for IDs, `username`, `role`, `type`, or status fields. Status changes go through named methods
  (`approve()`, `markAdopted(...)`).
- Return unmodifiable lists (`List.copyOf(...)` or `Collections.unmodifiableList(...)`) from getters that
  expose collections.
- Prefer `protected` constructors on abstract classes. Subclasses call `super(...)`.
- Add `@Override` on every overriding method.

## Layers

- **Menus:** read input with `InputHelper`, call one service method per action, catch exceptions, print
  results with `ConsoleUI`. No file access, no business rules.
- **Services:** check the session (role, ownership) first, then validate, then change data, then `save()`.
  Never print. Never read input.
- **Models:** data and their own rules (validating setters, status methods). No input/output, no services.

## Exceptions

- Throw the most specific `PetAdoptionException` subclass, with a message a user can understand and act on
  (see [VALIDATION.md §Message style](VALIDATION.md#message-style)).
- Do not catch an exception just to ignore it. If you catch, either recover (re-prompt) or report.
- Catch `PetAdoptionException` in menus, not in services (services let it propagate).
- Wrap low-level exceptions with a cause: `throw new DataFileException("...", e);`.
- Custom exceptions are always checked (extend `PetAdoptionException`). Do not create unchecked custom
  exceptions (ADR-033). Never catch `Throwable` or `Error`.
- In a `catch` chain, put specific types first and `Exception` last.
- Do not use exceptions for normal control flow (e.g. use `findById(...)` returning `Optional` to check
  existence, not `try { getById } catch`).

## Console input and output

- Only `InputHelper` reads from `System.in`. Only `ConsoleUI` (and menus via `ConsoleUI`) print.
- Always read whole lines (`nextLine()`), then parse. Never `nextInt()` (see [GOTCHAS.md](GOTCHAS.md)).
- Lists print as aligned tables; the Pet Passport and application details print as labeled blocks.
- Show enum labels ("Pending Adoption"), never raw names (`PENDING_ADOPTION`).
- Dates display as `yyyy-MM-dd`.

## Comments and documentation

- A short Javadoc comment on every public class saying what it is for, and on each contract method in
  [TEAM.md](TEAM.md#contracts).
- Mark design-pattern roles in the class comment so they are easy to find at the defense:
  `/** Singleton: ... */`, `/** Strategy (concrete): ... */`, `/** Strategy context: ... */`.
- Comment *why*, not *what*. Do not leave commented-out code in commits.

## Formatting

- 4 spaces, no tabs. Opening brace on the same line. One class per file.
- Keep methods short; if a menu case grows past ~15 lines, move it to a private method.

## Dependencies

- Java 21 standard library only (ADR-005). Do not add JARs, Maven, or Gradle.

## Verification loop (before every commit)

1. Delete `out/` and compile with the README command — fix every error and warning you introduced.
2. Run the app from the repo root.
3. Exercise the feature: the happy path, then the invalid inputs listed for it in
   [VALIDATION.md](VALIDATION.md), then the status/permission cases in [BUSINESS-RULES.md](BUSINESS-RULES.md).
4. Restart the app and confirm the change persisted in `data/`.
