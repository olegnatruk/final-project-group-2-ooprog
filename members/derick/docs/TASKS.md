# Derick — tasks

Tick `[x]` when the item is merged into `main` and verified by running the app. Phases follow
[TEAM.md §Build order](../../../docs/TEAM.md#build-order).

## Phase 1 — Models

- [ ] `AdoptionRecord`: private fields; setters only for `adoptionDate` and `notes` (validated); `toRecord()`; `AdoptionRecord.fromRecord(String[] fields)`
- [ ] `StatisticsReport` (counts per period; may be a Java `record`)
- [ ] `StatisticsStrategy` interface

## Phase 2 — Skeletons (others depend on these)

- [ ] `RecordService` with `hasRecordForPet`, `hasRecordForApplication`, `hasRecordForUser`, `getAllRecords`
- [ ] `UserService` with `getUserById`
- [ ] Ask Kurt to register `records()` in `DataStore`

## Phase 3 — Features

**Profile**
- [ ] `UserService`: update own profile (email unique excluding self), change password (ADR-030)
- [ ] `ProfileMenu`: view, update personal info, change password

**User management (admin)**
- [ ] `UserService`: add (via `AuthService.createAccount`), update, enable/disable, reset password, delete — with self / last-admin / history rules
- [ ] `user/search/`: `UserKeywordSearch`, `UserRoleFilter`, `UserStatusFilter`; `UserService.search` via `RecordFinder` + `setStrategy` (ADR-032)
- [ ] `UserManagementMenu`: all 9 options from MAP.md

**Adoption records and history**
- [ ] `RecordService`: create (from approved app, date rules, pet → Adopted), update date/notes, delete (pet → Pending Adoption), history queries
- [ ] `record/search/`: `RecordAdopterSearch`, `RecordPetSearch`, `RecordDateRangeFilter`, `RecordPetTypeFilter`; `RecordService.search` via `RecordFinder` + `setStrategy` (ADR-032)
- [ ] `AdoptionRecordMenu` (admin) and `UserHistoryMenu` (user)

**Statistics**
- [ ] `MonthlyStatisticsStrategy`, `YearlyStatisticsStrategy`, `StatisticsService` (context: both constructors + `setStrategy`), `StatisticsMenu` (picks the strategy at runtime)

- [ ] Wire `ProfileMenu` (both home menus), `UserManagementMenu`, `AdoptionRecordMenu`, `StatisticsMenu`, `UserHistoryMenu` (one line each)

## Verification checklist

- [ ] Profile: change email to another user's email (different case) → blocked; own email unchanged → OK
- [ ] Password change: wrong current password; new = current; new without digit; mismatch
- [ ] Admin disables self / deletes self / disables the only admin → blocked
- [ ] Delete a user who has an application → blocked with "disable instead"; disabled user cannot log in; re-enable → can log in
- [ ] Create record from a `PENDING` app → blocked; second record for the same app → blocked
- [ ] Adoption date tomorrow; before the application date; `2026-02-30`; Enter → today
- [ ] Delete record → pet back to Pending Adoption; recreate → Adopted again
- [ ] User history shows only own adoptions; admin has no "own history" option
- [ ] Statistics: future month → blocked; month `13`; year `1999`; empty period → zeros; numbers match the data
- [ ] Restart → users, records persisted

## Phase 5 — Polish

- [ ] Docs pass: MAP.md rows for `user/` services and `record/` marked ✅, contracts match code
