# Codebase map

**Planned layout — no Java files exist yet.** As files are created, keep this map exact: when a class is
added, renamed, or moved, update this file and [BLUEPRINT.md](BLUEPRINT.md) in the same commit.

Root package: `petadoption` (source root `src/`, so files live under `src/petadoption/`).

---

## Where do I change X?

| I want to… | Edit | Owner |
|---|---|---|
| Change startup / shutdown | `Main.java` | Kurt |
| Change how files are read/written | `core/FileStorage.java`, `core/Repository.java` | Kurt |
| Change a file's field layout | The entity's `toRecord()` + `fromRecord()` + [DATA-MODEL.md](DATA-MODEL.md) | Entity owner |
| Add a validation rule used in many places | `util/Validator.java` + [VALIDATION.md](VALIDATION.md) | Kurt |
| Change how input is read / re-prompted | `util/InputHelper.java` | Kurt |
| Change `[ERROR]`/`[OK]` output, headers, tables | `util/ConsoleUI.java` | Kurt |
| Change login, register, logout, seeded admin | `auth/AuthService.java`, `auth/WelcomeMenu.java` | Kurt |
| Add an option to the Regular User or Admin home menu | `ui/UserMenu.java`, `ui/AdminMenu.java` (see [TEAM.md §Menu wiring](TEAM.md#menu-wiring)) | Kurt |
| Add a new exception type | `exception/` | Kurt |
| Change pet fields / add a pet type | `pet/Pet.java` + subclass + `pet/PetType.java` | Julyan |
| Change pet CRUD or pet status rules | `pet/PetService.java` | Julyan |
| Change browse / Pet Passport display | `pet/PetDisplay.java`, `pet/PetBrowseMenu.java`, `Pet.getTypeSpecificDetails()` | Julyan |
| Add a pet search or filter | new class in `pet/search/` | Julyan |
| Change application rules (submit, approve, reject, …) | `application/ApplicationService.java` ✅, `application/AdoptionApplication.java` ✅ | Ace |
| Change the application form | `application/ApplicationFormScreen.java` ✅ | Ace |
| Change how applications are listed / shown | `application/ApplicationDisplay.java` ✅ | Ace |
| Change rejection reasons | `application/RejectionReason.java` ✅ | Ace |
| Add an application search or filter | new class in `application/search/` ✅ | Ace |
| Change profile screens | `user/ProfileMenu.java`, `user/UserService.java` | Derick |
| Change admin user management | `user/UserManagementMenu.java`, `user/UserService.java` | Derick |
| Add a user search or filter | new class in `user/search/` | Derick |
| Change adoption record rules | `record/RecordService.java` | Derick |
| Change statistics | `record/StatisticsService.java` + strategy classes | Derick |
| Add a record search or filter | new class in `record/search/` | Derick |

---

## Planned files

The full file-by-file blueprint — every package and `.java` file with owner, type, fields, and method
signatures — is in [BLUEPRINT.md](BLUEPRINT.md). This map keeps only the lookup tables below.

---

## Menu tree

The last option of every menu is Back (or Logout/Exit). `→` shows the owning member's class.

```
WelcomeMenu (Kurt)
├── 1 Login  ─────────────▶ UserMenu or AdminMenu by role
├── 2 Register
└── 3 Exit

UserMenu (Kurt) — Regular User home
├── 1 Browse Pets       → PetBrowseMenu (Julyan)
├── 2 My Applications   → UserApplicationMenu (Ace)
├── 3 Adoption History  → UserHistoryMenu (Derick)
├── 4 My Profile        → ProfileMenu (Derick)
└── 5 Logout

AdminMenu (Kurt) — Admin home
├── 1 Manage Pets           → PetAdminMenu (Julyan)
├── 2 Manage Users          → UserManagementMenu (Derick)
├── 3 Adoption Applications → AdminApplicationMenu (Ace)
├── 4 Adoption Records      → AdoptionRecordMenu (Derick)
├── 5 Adoption Statistics   → StatisticsMenu (Derick)
├── 6 My Profile            → ProfileMenu (Derick)
└── 7 Logout
```

| Menu | Options |
|---|---|
| `PetBrowseMenu` | 1 View pets · 2 Search by keyword · 3 Filter pets · 4 View Pet Passport (then "Apply for this pet? y/n" → `ApplicationFormScreen.submitFor`) · 5 Back |
| `UserApplicationMenu` | 1 Apply for a pet · 2 View my applications · 3 View application details · 4 Update pending application · 5 Cancel pending application · 6 Back |
| `UserHistoryMenu` | 1 View my adoption history · 2 View record details · 3 Back |
| `ProfileMenu` | 1 View profile · 2 Update personal information · 3 Change password · 4 Back |
| `PetAdminMenu` | 1 View all pets · 2 View Pet Passport · 3 Add pet · 4 Update pet information · 5 Update pet status · 6 Delete pet · 7 Search & filter pets · 8 Back |
| `UserManagementMenu` | 1 View all users · 2 View user details · 3 Add user · 4 Update user · 5 Enable/disable user · 6 Reset user password · 7 Delete user · 8 Search & filter users · 9 Back |
| `AdminApplicationMenu` | 1 View all applications · 2 Review application · 3 Approve · 4 Reject · 5 Revoke approval · 6 Delete invalid application · 7 Search & filter applications · 8 Back |
| `AdoptionRecordMenu` | 1 View adoption history · 2 View record details · 3 Create record from approved application · 4 Update record · 5 Delete record · 6 Search & filter records · 7 Back |
| `StatisticsMenu` | 1 Monthly statistics · 2 Yearly statistics · 3 Back |

---

## Data files

| File | Entity | Format |
|---|---|---|
| `data/users.txt` | `User` | [DATA-MODEL.md §users](DATA-MODEL.md#datauserstxt--11-fields) |
| `data/pets.txt` | `Pet` | [DATA-MODEL.md §pets](DATA-MODEL.md#datapetstxt--14-fields) |
| `data/applications.txt` | `AdoptionApplication` | [DATA-MODEL.md §applications](DATA-MODEL.md#dataapplicationstxt--15-fields) |
| `data/records.txt` | `AdoptionRecord` | [DATA-MODEL.md §records](DATA-MODEL.md#datarecordstxt--6-fields) |

`data/` is created at runtime and git-ignored (ADR-002).
