# Derick — agent context

**Member:** Inandan, Derick
**Role in the team:** people and outcomes — profile management, admin user management, the official adoption
records and history, and adoption statistics (the second Strategy example).

Read with: [AGENTS.md](../../../AGENTS.md) → this file → [TASKS.md](TASKS.md) → [NOTES.md](NOTES.md) →
[SESSION_LOG.md](SESSION_LOG.md).

---

## What Derick owns

| File | Contents |
|---|---|
| `user/UserService.java` | Profile update, password change, admin add/update/enable/disable/reset password/delete, user queries |
| `user/ProfileMenu.java` | View/update own profile, change password (Regular User and Admin) |
| `user/UserManagementMenu.java` | Admin user management |
| `user/search/*.java` | `UserKeywordSearch`, `UserRoleFilter`, `UserStatusFilter` |
| `record/AdoptionRecord.java` | Model; `toRecord()`; static `fromRecord()` |
| `record/RecordService.java` | Record CRUD and history queries |
| `record/AdoptionRecordMenu.java` | Admin: full adoption history + record CRUD + search |
| `record/UserHistoryMenu.java` | Regular User: own adoption history |
| `record/StatisticsStrategy.java`, `MonthlyStatisticsStrategy.java`, `YearlyStatisticsStrategy.java` | Strategy |
| `record/StatisticsReport.java` | Report result |
| `record/StatisticsService.java` | Strategy context |
| `record/StatisticsMenu.java` | Admin statistics screen |
| `record/search/*.java` | `RecordAdopterSearch`, `RecordPetSearch`, `RecordDateRangeFilter`, `RecordPetTypeFilter` |

**Not Derick's:** `User`, `Admin`, `RegularUser`, `Role`, `AccountStatus` (Kurt owns the user models) and
`AuthService` (Kurt). Admin "Add user" must call `AuthService.createAccount(...)` so the same validation is used
as in registration.

## Spec features owned

- Manage Profile (spec 1.4) — for both roles
- View Adoption History, user (1.13) and admin (§7)
- Manage Users: add (4.1), view (4.2), update (4.3), disable (4.4), delete (4.5)
- Manage Adoption Records: create (6.1), view (6.2), update (6.3), delete (6.4)
- View Adoption Statistics (feature table; ADR-025)
- Admin search/filter for users and records (spec §8)

## What Derick must demonstrate for grading

| Requirement | Derick's example |
|---|---|
| Strategy | `StatisticsStrategy` + `Monthly`/`Yearly` implementations + `StatisticsService` context (two constructors + `setStrategy`, chosen at runtime in `StatisticsMenu`) — the closest match to the module's `Sorter` example; `user/search/*` and `record/search/*` through each service's `RecordFinder` (ADR-032) |
| Polymorphism | `StatisticsService` calls `strategy.generate(...)` without knowing which strategy it has |
| Abstraction | `StatisticsStrategy` interface |
| Encapsulation | `AdoptionRecord` private fields; only `adoptionDate`/`notes` have setters |
| Exception handling | Delete/disable safety rules throw clear exceptions; date validation catches `DateTimeParseException` |

## Key specs to follow

- **File blueprint (your files, fields, signatures):** [BLUEPRINT.md §user](../../../docs/BLUEPRINT.md#user--models-by-kurt-servicesmenus-by-derick) and [§record](../../../docs/BLUEPRINT.md#record--derick)
- Account rules (self, last admin, delete vs disable), record rules: [BUSINESS-RULES.md](../../../docs/BUSINESS-RULES.md#account-rules)
- `AdoptionRecord` fields, `records.txt` layout: [DATA-MODEL.md §AdoptionRecord](../../../docs/DATA-MODEL.md#adoptionrecord--package-record-owner-derick)
- Validation: [VALIDATION.md §Accounts](../../../docs/VALIDATION.md#accounts-kurt-writes-the-rules-derick-reuses-them), [§Records and statistics](../../../docs/VALIDATION.md#adoption-records-and-statistics-derick), [§Admin user management](../../../docs/VALIDATION.md#admin-user-management-derick)
- ADR-021 (record fields), ADR-022 (delete rules), ADR-023 (admin safety), ADR-024 (admins have no own
  history), ADR-025 (statistics), ADR-030 (password change): [DECISIONS.md](../../../docs/DECISIONS.md)
- Strategy design: [ARCHITECTURE.md §Design patterns](../../../docs/ARCHITECTURE.md#design-patterns-gof)
- Methods Derick must provide: [TEAM.md §Derick provides](../../../docs/TEAM.md#derick-provides-recordservice-userservice)

## Acceptance criteria

**Profile (both roles)**
- View shows ID, full name, email, contact, address, username, role.
- Update first/last name, email (unique, excluding self), contact, address; Enter keeps; confirm.
- Change password: current password required; new must follow the rules, differ from current, and match
  confirmation. Role and username cannot be changed.

**Admin user management**
- View all (ID, full name, username, email, contact, role, status) and view details.
- Add: via `AuthService.createAccount`, choosing role.
- Update name/email/contact/address; enable/disable; reset password (no old password needed).
- Cannot disable/delete self or the last active admin.
- Delete only users with no applications and no records (otherwise suggest disable); confirm.
- Search by name/username/email; filter by role and status; filters combine.

**Adoption records and history**
- Create from an `APPROVED` application without a record; date defaults to today, not future, not before
  application date; pet → `ADOPTED` via `PetService.markAdopted`; next `REC####` ID; saved.
- Update date/notes only; delete with confirmation → pet back to `PENDING_ADOPTION` via
  `PetService.revertToPendingAdoption`.
- Admin history: record ID, adopter name, pet name, application ID, adoption date. Search by adopter or pet;
  filter by date range and pet type.
- User history: only own records — record ID, pet name, type, breed, adoption date, application ID.

**Statistics**
- Monthly: choose year + month (not in the future). Yearly: choose year; shows per-month rows and a total.
- Counts: applications received, approved, rejected, cancelled, still pending, adoptions completed.
- Empty period → zeros, not an error.

## Dependencies on others

| Need | From | Method |
|---|---|---|
| Account creation + uniqueness | Kurt | `AuthService.createAccount`, `isUsernameTaken`, `isEmailTaken` |
| Delete-user rule | Ace | `hasAnyApplicationForUser` |
| Approved apps to create records from; statistics data | Ace | `getApprovedApplicationsWithoutRecord`, `getAllApplications`, `getApplicationById` |
| Pet status on record create/delete; pet names/types | Julyan | `markAdopted`, `revertToPendingAdoption`, `getPetById` |
| Everything else | Kurt | `DataStore.users()`/`records()`, `SessionManager`, `InputHelper`, `Validator`, `ConsoleUI`, `Menu`, `SearchStrategy` |

Others depend on Derick for: `hasRecordForPet`, `hasRecordForApplication`, `hasRecordForUser`,
`getAllRecords`, `getUserById`. Ship these as a skeleton early (Phase 2).
