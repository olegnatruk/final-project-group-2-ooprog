# Ace — agent context

**Member:** Cayabyab, Ace Justine
**Role in the team:** the adoption application workflow — the heart of the system. Users submit and manage
applications; admins review, approve, reject, revoke, and delete them. This is where status rules and the
cross-member side effects live.

Read with: [AGENTS.md](../../../AGENTS.md) → this file → [TASKS.md](TASKS.md) → [NOTES.md](NOTES.md) →
[SESSION_LOG.md](SESSION_LOG.md).

---

## What Ace owns

| File | Contents |
|---|---|
| `application/AdoptionApplication.java` | Model: fields, validating setters, status methods `approve()`, `reject(reason)`, `cancel()`, `revokeApproval(reason)`; `toRecord()`; static `fromRecord()` |
| `application/ApplicationStatus.java`, `LivingSituation.java`, `RejectionReason.java` | Enums with `getLabel()` |
| `application/ApplicationService.java` | All application business rules and queries |
| `application/ApplicationDisplay.java` | Static helpers: application table and details printing |
| `application/ApplicationFormScreen.java` | The application form; `submitFor(String petId)` and `editPending(String applicationId)` |
| `application/UserApplicationMenu.java` | Regular User application menu |
| `application/AdminApplicationMenu.java` | Admin application menu |
| `application/search/*.java` | `ApplicationStatusFilter`, `ApplicantNameSearch`, `ApplicationPetSearch` |

Do **not** edit other packages. To change a pet's status, call Julyan's `PetService` methods — never set it
directly.

## Spec features owned

- User: Submit Adoption Application (1.9), View Adoption Applications (1.10), Update Pending Application
  (1.11), Cancel Adoption Application (1.12)
- Admin: View All (5.1), Review (5.2), Approve (5.3), Reject (5.4), Delete Invalid Application (5.5)
- Admin "Update Application Status" = approve / reject / revoke actions (no free status editing)
- Revoke approval (ADR-015) and admin search/filter for applications (spec §8)

## What Ace must demonstrate for grading

| Requirement | Ace's example |
|---|---|
| Encapsulation | No `setStatus`; status only changes through the four status methods, each guarding its own transition |
| Exception handling | `InvalidStatusTransitionException`, `DuplicateRecordException`, `RecordNotFoundException`, `UnauthorizedAccessException`; menus catch and print |
| Strategy | `application/search/*` implement `SearchStrategy<AdoptionApplication>`; `ApplicationService.search` uses its `RecordFinder` + `setStrategy` (ADR-032) |
| Polymorphism | Menus extend `Menu` and override `handleChoice`; uses `User.canApplyForAdoption()` instead of checking the class |
| Validation | Status, ownership, duplicate, limit (max 3 pending), cross-field (experience details) |

## Key specs to follow

- **File blueprint (your files, fields, signatures):** [BLUEPRINT.md](../../../docs/BLUEPRINT.md#application--ace)
- Application status flow, approval side effects, delete rules: [BUSINESS-RULES.md](../../../docs/BUSINESS-RULES.md#application-status-flow)
- Fields, enums, `applications.txt` layout: [DATA-MODEL.md §AdoptionApplication](../../../docs/DATA-MODEL.md#adoptionapplication--package-application-owner-ace)
- Validation + messages: [VALIDATION.md §Adoption applications](../../../docs/VALIDATION.md#adoption-applications-ace)
- ADR-013 (auto-reject others), ADR-015 (revoke), ADR-016 (no COMPLETED), ADR-017 (duplicates, limit 3),
  ADR-024 (admins don't apply), ADR-026 (form fields), ADR-028 (rejection reason): [DECISIONS.md](../../../docs/DECISIONS.md)
- Methods Ace must provide: [TEAM.md §Ace provides](../../../docs/TEAM.md#ace-provides-applicationservice--form)
- Approve flow walkthrough: [ARCHITECTURE.md §Key flow](../../../docs/ARCHITECTURE.md#key-flow-approving-an-application)

## Acceptance criteria

**User side**
- Submit: only `REGULAR_USER`; pet must exist and be `AVAILABLE`; no duplicate active app for the pet; fewer
  than 3 `PENDING`. Name copied from profile; address and contact pre-filled (Enter keeps). New app is
  `PENDING`, dated today, gets the next `APP####` ID, saved.
- View: only the user's own applications (another user's ID → "not found"). List shows ID, pet name, date,
  status; details show everything, including the decision reason if any.
- Update: only own `PENDING` apps; Enter keeps each value; confirm before saving.
- Cancel: only own `PENDING` apps; confirm; status → `CANCELLED`, decision date set.

**Admin side**
- View all + review details (with applicant and pet info).
- Approve: app `PENDING`, pet `AVAILABLE`; confirm; app → `APPROVED`; pet → `PENDING_ADOPTION` via
  `PetService.markPendingAdoption`; other `PENDING` apps for the pet → `REJECTED` with the standard reason.
  Print how many others were auto-rejected.
- Reject: app `PENDING`; reason from list (Other → typed); confirm.
- Revoke: app `APPROVED` and no adoption record (`RecordService.hasRecordForApplication`); reason; pet →
  `AVAILABLE` via `PetService.markAvailable`.
- Delete invalid: `PENDING`/`REJECTED`/`CANCELLED` only; confirm; never changes pet status.
- Search by applicant name or pet name/ID; filter by status; filters combine.
- If any check fails, nothing is changed or saved.

## Dependencies on others

| Need | From | Method |
|---|---|---|
| Pet info and status changes | Julyan | `getPetById`, `markPendingAdoption`, `markAvailable` |
| Revoke rule; approved-without-record list | Derick | `hasRecordForApplication` |
| Applicant/pet names in admin lists | Derick / Julyan | `UserService.getUserById`, `PetService.getPetById` |
| Everything else | Kurt | `DataStore.applications()`, `SessionManager`, `InputHelper`, `Validator`, `ConsoleUI`, `Menu`, `SearchStrategy`, `AllOfStrategy` |

Others depend on Ace for: `hasAnyApplicationForPet`, `hasApprovedApplicationForPet`, `hasAnyApplicationForUser`,
`getAllApplications`, `getApprovedApplicationsWithoutRecord`, and `ApplicationFormScreen.submitFor`. Ship
these as a skeleton early (Phase 2).
