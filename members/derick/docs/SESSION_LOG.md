# Derick — session log

Append-only history of **Derick's** work sessions, so an AI agent (or Derick) can pick up where the last session
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

## 2026-10-15 — Models and skeletons (PR 1: derick/record-models)

Implemented Phase 1 models and Phase 2 skeletons:
- `record/AdoptionRecord`: private fields, validating setters, `toRecord()`, static `fromRecord()`.
- `record/StatisticsReport`: record model with immutability and defensive copy.
- Registered `records` repository in `core/DataStore`.
- `record/RecordService` skeleton with `hasRecordForPet`, `hasRecordForApplication`, `hasRecordForUser`, `getAllRecords`.
- `user/UserService` skeleton with `getUserById`.
- Clean compilation on JDK 21 with `-Xlint:all`.
## 2026-10-15 — User and record features (PR 2: derick/user-record-features)

Implemented all Phase 3 user, record, and statistics features:
- `user/UserService`: full implementation for profile update (unique email excluding self), change password (current password check, differing new password), admin user management (addUser via AuthService.createAccount, updateUser, setAccountStatus with self and last-admin rules, resetPassword without old password, deleteUser with self/last-admin and history checks).
- `user/search/`: `UserKeywordSearch`, `UserRoleFilter`, `UserStatusFilter` implementing `SearchStrategy<User>`.
- `user/ProfileMenu`: view own profile, update personal info with optional Enter-to-keep prompts, and change password with confirmation.
- `user/UserManagementMenu`: all 9 options from MAP.md, supporting view all, view details, add, update, enable/disable, reset password, delete, and combined keyword/role/status search & filter.
- `record/RecordService`: full implementation for admin record CRUD (create from APPROVED app, date >= appDate and not in future, mark pet ADOPTED; update date/notes; delete record and revert pet to PENDING_ADOPTION) and Regular User personal history queries (getMyRecords).
- `record/search/`: `RecordAdopterSearch`, `RecordPetSearch`, `RecordDateRangeFilter`, `RecordPetTypeFilter` implementing `SearchStrategy<AdoptionRecord>`.
- `record/AdoptionRecordMenu`: 7 admin options including full history view, details, create record from approved app, update, delete, and search/filter.
- `record/UserHistoryMenu`: Regular User own adoption history and details view.
- `record/StatisticsStrategy`, `MonthlyStatisticsStrategy`, `YearlyStatisticsStrategy`: Strategy pattern implementations for monthly (counts of received, approved, rejected, cancelled, pending, completed) and yearly breakdown.
- `record/StatisticsService`: Strategy context with dual constructors and runtime `setStrategy`.
- `record/StatisticsMenu`: 3 admin options running monthly and yearly reports, validating year (2000 to current) and month (not in future).
- Menu wiring: replaced Derick's `comingSoon(...)` calls in `ui/UserMenu.java` (options 3 and 4) and `ui/AdminMenu.java` (options 2, 4, 5, and 6).
- Verification: clean compile on JDK 21 (`-Xlint:all`), executed and passed every scenario in the TASKS.md checklist including validation and edge cases.

## 2026-10-17 — Git identity fix and completion

- My user/record/statistics features are merged into main and all items in members/derick/docs/TASKS.md are ticked.
- I fixed my git identity: earlier commits (93d4bea, 16f70af) used the hostname email derickinandan@Direkrekreks-MacBook-Air.local, so GitHub couldn't link them to my account. Commits from now on use my GitHub noreply email.
- Next: wait for Kurt's Phase 4 end-to-end run and fix any bugs in user/, record/, or statistics on a derick/fix-<thing> branch with a PR.
