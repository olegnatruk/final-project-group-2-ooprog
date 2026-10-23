# Session log (shared)

A running, append-only record of what shipped in each work session that affects **the whole team** —
architecture, shared contracts, decisions. Member-only progress goes in `members/<name>/docs/SESSION_LOG.md`.

**Why this exists:** AI agents start every session with no memory. Automated memory tools can break and do
not travel with `git clone`. This file is the durable fallback: plain text, in Git, readable by anyone.

**Convention:** one dated entry per session (newest at the bottom). Say what changed and why. Do not repeat
`git log`; link to commits, PRs, ADRs, or GOTCHAS entries instead. Record declined or deferred ideas briefly
so they are not proposed again from scratch.

---

## 2026-10-14 — Documentation bootstrap (no code yet)

Set up the shared documentation and per-member agent context before any coding started. Inputs: the
professor's requirements (Login, CRUD, roles, 4 OOP pillars, exception handling, Singleton + Strategy, all
types of validations) and the group's feature spec, copied verbatim to
[reference/GROUP2-SPEC.md](reference/GROUP2-SPEC.md).

Group decisions: console UI, file storage, plaintext passwords, Java 21 with plain `javac`, one shared `src/`
tree with package-per-domain ownership, split by domain (Kurt core/auth, Julyan pets, Ace applications,
Derick users/records/statistics). See ADR-001 to ADR-005 in [DECISIONS.md](DECISIONS.md).

The spec had gaps and contradictions (status flow after approval, what happens on revoke, duplicate rule,
admin adoption history, record status field, exact validation limits, delete rules). Each got a concrete
default as a **Proposed — group confirm** ADR (ADR-006 to ADR-030) so coding can start from one source. The
group should review those ADRs together and mark each Accepted or change it.

Created: `README.md`, `AGENTS.md`, `CLAUDE.md`, `PROJECT-BRIEF.md`, `.gitignore`, `docs/*`, and
`members/{kurt,julyan,ace,derick}/docs/*`.

Deferred: sample/demo data in Git (ADR-002 mentions a possible `sample-data/` folder); a scripted test
input file for demo runs.

## 2026-10-14 — App file blueprint

Added [BLUEPRINT.md](BLUEPRINT.md): the full planned source tree (84 `.java` files across `core`, `auth`, `ui`,
`util`, `exception`, `user`, `pet`, `application`, `record`) with owner, kind, extends/implements, fields, and
method signatures for every file, a package dependency diagram, and a build checklist. Cross-member contract
methods are marked ★ and mirror [TEAM.md §Contracts](TEAM.md#contracts).

New planned files introduced while detailing the blueprint: `pet/PetDisplay` and
`application/ApplicationDisplay` (shared printing so admin and user screens format the same way) and the
`application/RejectionReason` enum (ADR-028's reason list). `Pet.setStatus` is planned as package-private so
only `PetService` can change pet status. MAP.md's file list now points to BLUEPRINT.md instead of duplicating it.

## 2026-10-14 — Aligned the design with the course modules

Checked the plan against the course's Design Patterns and Exception Handling slides. Four gaps were closed with
new Proposed ADRs in [DECISIONS.md](DECISIONS.md):

- **ADR-031:** nested try-catch (outer per file, inner per line) with re-throw in `Repository.load()` — the
  module teaches nested try-catch and nothing used it.
- **ADR-032:** `Repository.find` removed; every service search goes through its own `RecordFinder` and
  `setStrategy`, so the Strategy context and runtime swap are visible. `RecordFinder` and `StatisticsService`
  got constructors that take a strategy, matching the module's `Sorter` example.
- **ADR-033:** written reason why all custom exceptions are checked (recoverable conditions) and where the
  JDK's unchecked exceptions are caught.
- **ADR-034:** `getInstance()` of both Singletons is `synchronized`, with a prepared answer for the
  thread-safety question.

Updated to match: ARCHITECTURE, BLUEPRINT, TEAM (contracts), CONVENTIONS, and every member's CONTEXT/TASKS.

## 2026-10-14 — Kurt's foundation implemented (PR from `kurt/foundation`)

All of Kurt's 32 files exist and compile with `javac --release 21 -Xlint:all` with no warnings. The app runs
end to end for login/register/logout with role routing. Other members can now build on it — see
[BLUEPRINT.md](BLUEPRINT.md) for the exact signatures as built.

**For the other members:**
- Pull `main` after the PR is merged, then branch from it.
- To register your repository, edit `core/DataStore.java`: add the field, create it in the constructor
  (`new Repository<>("pets.txt", "P", "Pet", Pet::fromRecord)`), add a `warnings.addAll(pets.load());` line in
  `load()`, and add the accessor `pets()`. The commented lines in that file show the exact values.
- To wire your menu, replace only your `comingSoon(...)` line in `ui/UserMenu.java` / `ui/AdminMenu.java`
  (the comment on that line shows the replacement).
- Enums: override `toString()` to return the label (CONVENTIONS.md).
- New gotcha: bad data lines are dropped on the next save ([GOTCHAS.md](GOTCHAS.md)).

## 2026-10-15 — Adoption application workflow implemented (Ace)

Implemented Phase 1 models, Phase 2 skeletons, and Phase 3 features for the application package:
- Models & Enums: `AdoptionApplication` (private fields, validating setters, guarded status methods `approve`, `reject`, `cancel`, `revokeApproval` without `setStatus`, and pipe-delimited persistence), `ApplicationStatus`, `LivingSituation`, `RejectionReason`.
- Service & Queries: `ApplicationService` with full implementation of `submitApplication` (active duplicate check, limit 3 pending), `getMyApplications`, `getMyApplicationById`, `updateMyApplication`, `cancelMyApplication`, `getAllApplications`, `getApplicationById`, `approveApplication` (transitions pet to PENDING_ADOPTION, auto-rejects other pending apps for the same pet), `rejectApplication`, `revokeApproval` (checks no adoption record exists, returns pet to AVAILABLE), `deleteApplication`, and `search` via `RecordFinder`.
- Cross-member contract queries: `hasAnyApplicationForPet`, `hasApprovedApplicationForPet`, `hasAnyApplicationForUser`, `getApprovedApplicationsWithoutRecord`.
- UI: `ApplicationDisplay`, `ApplicationFormScreen`, `UserApplicationMenu`, and `AdminApplicationMenu`.
- Search: concrete strategies in `petadoption.application.search.*`.
- Menu wiring: connected `ui/UserMenu` option 2 and `ui/AdminMenu` option 3.
- DataStore: registered `applications()` repository in `core/DataStore.java`.

## 2026-10-18 — End-to-end test pass and fixes (Kurt, branch `kurt/fix-empty-pet-lists`)

Ran the full app from fresh data through scripted input: register/login guards, add/update/delete pets,
browse/search/filter/passport, apply → approve → adoption record, statistics, user management, profile,
corrupted data lines, and an unreadable data file. Fixes (touches every member's package — reviewed by Kurt):
- New rule: every pet-reading option prints "No pets listed yet." when there are no pets
  ([BUSINESS-RULES.md](BUSINESS-RULES.md#menu-behaviour-on-empty-or-blocked-actions)).
- Same for applications: "No applications applied yet." (user's My Applications options 2–5) and
  "No applications submitted yet." (admin application options 1–7).
- Blocked actions now fail before the form/confirmation (`checkCanApply`, pet/user `checkCanDelete`,
  `checkStatusChange`, `checkAccountStatusChange`). Create Record stops early when nothing is approved.
- Admin Add/Update User and Profile now reject a taken email/username at the prompt
  (`AuthService.checkNewEmail/checkNewUsername`, shared with Register).
- Dates re-prompt when out of range (`InputHelper.readDate(prompt, default, min, max)`); the monthly
  statistics month is limited to past months.
- Bug: `RecordService.updateRecord` swallowed its own "cannot be before the application date" error, so the
  rule never applied. Fixed.
- Password mismatch in Reset Password / Change Password re-asks instead of failing after confirmation.
- Cosmetic: doubled `(y/n)`, doubled `[default]` in the application form, doubled "Action cancelled.",
  empty `[]` in Update Pet enum prompts.

## 2026-10-21 — Remove skeleton leftovers, add demo guide (Kurt, branch `simplier-code`)

Goal: make the code easier to explain at the defense without changing behaviour.
- Removed Java reflection (`Class.forName` / `invoke`) in `PetService` and `PetBrowseMenu`. They now call
  `ApplicationService` and `ApplicationFormScreen` directly. `PetService` creates `ApplicationService` inside the
  method, not as a field, because `ApplicationService` already holds a `PetService`.
- Removed every `catch (UnsupportedOperationException ...)`. Nothing throws it since the skeletons were replaced
  ([DECISIONS.md](DECISIONS.md) exception table). `catch (Exception e)` around pet lookups narrowed to
  `RecordNotFoundException`. About 100 lines gone across pet, application, record and user packages.
- New [DEMO-GUIDE.md](DEMO-GUIDE.md): each requirement → file/line → what to type, a 5-minute demo script, and
  who presents what. Every "Show" step was run against the app.
- Verified: clean compile with `-Xlint:all`; scripted runs of apply-from-passport, approve, create/delete
  record, statistics, delete-pet / status-change / delete-user blocks, and the demo script.

## 2026-10-23 — "Already that value" on updates (Kurt, branch `kurt/same-value-update`)

Rule: updating a field to the value it already has is refused with `<Field> is already <value>.` and the
field is asked again ([BUSINESS-RULES.md](BUSINESS-RULES.md#menu-behaviour-on-empty-or-blocked-actions)).
- One change in `InputHelper.readOptional` covers My Profile, Update User, Update Pet, Update Application and
  Update Record notes. New `readWithDefault` keeps the old behaviour for the new-application form, and
  `readDateForUpdate` does the same check for the record's adoption date.
- Pet update: age and weight now return the parsed number, so `03` or `12.50` match `3` or `12.5`; gender,
  vaccination, dog size and the cat/bird yes/no questions refuse the current choice.
- Application update: same living situation, same experience answer (No to No) and same experience details.
  Admin Reset Password refuses the user's current password.
- Verified with scripted runs for each form (repeat every field, then a real change still saves).
