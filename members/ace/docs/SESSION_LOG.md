# Ace — session log

Append-only history of **Ace's** work sessions, so an AI agent (or Ace) can pick up where the last session
stopped. Newest entry at the bottom. Never rewrite old entries.

**At the end of every session, add an entry with:**
- What was done (link the branch / PR / commit instead of pasting code).
- What is half-finished and the exact next step.
- Anything blocked on another member.

Changes that affect the whole team also get a short entry in
[docs/SESSION_LOG.md](../../../docs/SESSION_LOG.md).

---

## 2026-10-14 — Docs set up

Project documentation and this folder were created. No code yet. Next step: start Phase 1 in
[TASKS.md](TASKS.md).

## 2026-10-15 — Application models and skeletons (branch ace/application-models)

Implemented Phase 1 models and Phase 2 skeletons:
- Created enums `ApplicationStatus`, `LivingSituation`, `RejectionReason` with `getLabel()` and `toString()`.
- Created `AdoptionApplication` with private fields, validating setters, no `setStatus`, guarded status methods (`approve()`, `reject()`, `cancel()`, `revokeApproval()`), cross-field validation for pet experience, and `toRecord()` / `fromRecord()`.
- Created `ApplicationService` skeleton with cross-member contract methods (`getAllApplications`, `getApplicationById`, `hasAnyApplicationForPet`, `hasApprovedApplicationForPet`, `hasAnyApplicationForUser`, `getApprovedApplicationsWithoutRecord`).
- Created `ApplicationFormScreen` skeleton with `submitFor(String petId)` and `editPending(String applicationId)`.
- Registered `applications` repository in `core/DataStore.java`.
- Verified clean compilation with `javac --release 21 -Xlint:all`.
- PR 1 opened and merged into `main`.

## 2026-10-15 — Application features (branch ace/application-features)

Implemented all Phase 3 application features:
- `ApplicationService`: full implementation with submitApplication (role check, duplicate active application check, limit of 3 pending applications, pet availability), getMyApplications, getMyApplicationById (throws RecordNotFoundException for other users' IDs), updateMyApplication, cancelMyApplication, getAllApplications, getApplicationById, approveApplication (transitions pet to PENDING_ADOPTION via PetService, auto-rejects other pending applications for the same pet with reason 'Pet already has an approved adopter', returns count), rejectApplication with reason, revokeApproval (requires APPROVED, checks no adoption record exists, returns pet to AVAILABLE), deleteApplication (PENDING/REJECTED/CANCELLED only), and search via RecordFinder.
- `ApplicationDisplay`: static helpers `printApplicationTable` and `printDetails` with pet name lookups.
- `ApplicationFormScreen`: interactive form with profile pre-fill (address and contact number), field validation, Enter-to-keep on edit, and confirmation dialogs.
- `application/search/`: `ApplicationStatusFilter`, `ApplicantNameSearch`, `ApplicationPetSearch` implementing `SearchStrategy<AdoptionApplication>`.
- `UserApplicationMenu`: all 6 options from MAP.md wired.
- `AdminApplicationMenu`: all 8 options from MAP.md wired (including combined search & filter via `AllOfStrategy`).
- Menu wiring: replaced `comingSoon(...)` lines in `ui/UserMenu.java` (option 2) and `ui/AdminMenu.java` (option 3).
- Verification: clean compilation with `javac --release 21 -encoding UTF-8 -Xlint:all`. Executed comprehensive test suite and interactive menu run, passing all 12 acceptance criteria from TASKS.md including invalid inputs, role restrictions, and wrong-status transition cases.
- Next step: open PR 2 and merge into main.


