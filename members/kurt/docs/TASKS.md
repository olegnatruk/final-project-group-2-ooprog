# Kurt — tasks

Tick `[x]` when the item is merged into `main` and verified by running the app. (Phases 1-3 are done and
verified on branch `kurt/foundation`; they count as merged once that PR is merged.) Phases follow
[TEAM.md §Build order](../../../docs/TEAM.md#build-order).

## Phase 1 — Foundation (merge first; others are waiting)

- [x] `exception/`: `PetAdoptionException` (checked, message + cause constructors) and the 7 subclasses; `ValidationException` has `getFieldName()`
- [x] `util/ConsoleUI`: `printHeader`, `printError`, `printSuccess`, `printInfo`, `printTable`
- [x] `util/TextRule` functional interface
- [x] `util/Validator`: name, email, contact normalization, username, password, address, length, range, no-delimiter
- [x] `util/InputHelper`: single `Scanner`; all methods in TEAM.md contracts, including overloads; handles end of input
- [x] `core/Storable`, `core/RecordParser`, `core/SearchStrategy`, `core/AllOfStrategy`, `core/RecordFinder` (both constructors + `setStrategy`)
- [x] `ui/Menu` abstract base with `final run()`
- [ ] PR merged; tell the group Phase 1 is in `main`

## Phase 2 — Persistence, session, users

- [x] `user/Role`, `user/AccountStatus` enums with `getLabel()`
- [x] `user/User` (abstract), `Admin`, `RegularUser`: validating setters, `getRole()`, `canApplyForAdoption()`, `toRecord()`, `fromRecord()`
- [x] `core/FileStorage`: create `data/`, read/write lines with try-with-resources, wrap `IOException` in `DataFileException`
- [x] `core/Repository<T>`: load with **nested try-catch** (outer per file → re-throw with file name; inner per line → warning, continue — ADR-031), save, add, remove, findById, getById, findAll (unmodifiable), nextId. No `find` (ADR-032)
- [x] `core/DataStore` Singleton (`static synchronized getInstance()`) with `users()`; add `pets()`, `applications()`, `records()` as each model is merged
- [x] `auth/SessionManager` Singleton (`static synchronized getInstance()`) with `requireLoggedIn`, `requireAdmin`, `requireRegularUser`

## Phase 3 — Features

- [x] `auth/AuthService`: `createAccount`, `register`, `login`, `logout`, `isUsernameTaken`, `isEmailTaken`, `ensureDefaultAdmin`
- [x] `auth/WelcomeMenu`: Login (3-attempt limit), Register (with confirm password), Exit
- [x] `ui/UserMenu` and `ui/AdminMenu` with every option from MAP.md and `Coming soon` placeholders
- [x] `Main`: load, seed admin, run, top-level catch, `finally` closes input

## Phase 4 — Integration

- [ ] Every member's menu wired into `UserMenu`/`AdminMenu`
- [ ] Full end-to-end run: register → apply → admin approve → create record → history → statistics
- [ ] Restart test: all data still there

## Verification checklist (run before ticking Phase 3)

- [x] Register with: blank fields, 1-letter name, bad email, taken email (different case), `0917-123-4567` (accepted and normalized), `12345`, 3-character username, taken username, password without digit, mismatched confirmation, text containing `|`
- [x] Login with wrong password ×3 → back to welcome menu; disabled account; email instead of username; uppercase username
- [x] Menu inputs: `abc`, blank, `0`, `99`, `-1`, `2147483648`
- [x] Delete `data/` → app recreates it and seeds `admin`
- [x] Corrupt one line in `users.txt` → warning, others still load (inner catch)
- [x] Make `data/users.txt` unreadable (e.g. replace it with a folder of the same name) → clear error naming the file (outer catch)

## Phase 5 — Polish

- [ ] Demo script (input sequence for the presentation)
- [ ] Docs pass: MAP.md rows marked ✅, contracts match code
