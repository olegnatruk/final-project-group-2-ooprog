# Julyan — agent context

**Member:** Lingao, Julianne Therese
**Role in the team:** everything about pets — the data model with the clearest Inheritance and Polymorphism
examples, admin pet management, browsing, the Pet Passport, and pet search/filter (Strategy).

Read with: [AGENTS.md](../../../AGENTS.md) → this file → [TASKS.md](TASKS.md) → [NOTES.md](NOTES.md) →
[SESSION_LOG.md](SESSION_LOG.md).

---

## What Julyan owns

| File | Contents |
|---|---|
| `pet/Pet.java` | Abstract pet: common fields, validating setters, abstract `getType()`, `getMaxAgeYears()`, `getMaxWeightKg()`, `getTypeSpecificDetails()`; `toRecord()`; static `fromRecord()` that builds the right subclass |
| `pet/Dog.java`, `pet/Cat.java`, `pet/Bird.java` | Subclasses: `size` / `indoorOnly` / `canFly`, and the overrides (ADR-018) |
| `pet/PetType.java`, `Gender.java`, `PetStatus.java`, `VaccinationStatus.java`, `DogSize.java` | Enums with `getLabel()` |
| `pet/PetService.java` | CRUD, visibility, manual status update, workflow transitions for Ace/Derick |
| `pet/PetDisplay.java` | Static helpers: pet table and Pet Passport printing, shared by both pet menus |
| `pet/PetAdminMenu.java` | Admin pet management |
| `pet/PetBrowseMenu.java` | Browse, Pet Passport, search/filter for everyone |
| `pet/search/*.java` | `PetKeywordSearch`, `PetTypeFilter`, `PetBreedFilter`, `PetGenderFilter`, `PetAgeRangeFilter`, `PetStatusFilter`, `PetVisibleToUsersFilter` |

Do **not** edit other packages. Use Kurt's `InputHelper`, `Validator`, `ConsoleUI`, `Menu`, `DataStore`,
`SessionManager` as documented in [TEAM.md §Kurt provides](../../../docs/TEAM.md#kurt-provides).

## Spec features owned

- Browse Pets (spec 1.5), View Pet Passport (1.6), Search Pets (1.7), Filter Pets (1.8)
- Add Pet (3.1), View Pets (3.2), Update Pet Information (3.3), Delete Pet (3.4), Update Pet Status (3.5)
- Admin search/filter for pets (spec §8)

## What Julyan must demonstrate for grading

| Requirement | Julyan's example |
|---|---|
| Inheritance | `Pet` → `Dog`, `Cat`, `Bird` |
| Polymorphism | `getMaxAgeYears()` / `getMaxWeightKg()` used by validation; `getTypeSpecificDetails()` used by the Pet Passport; `toRecord()` |
| Abstraction | Abstract `Pet`; menus work with `Pet`, never with the concrete subclass |
| Encapsulation | Private fields; setters validate; no setter for `petId`, `type`, `dateAdded`, or `status` |
| Strategy | `pet/search/*` implement `SearchStrategy<Pet>`; combined with `AllOfStrategy` for multiple filters; `PetService` holds a `RecordFinder<Pet>` and calls `setStrategy` on every search — **the main runtime-swap demo** (keyword search, then type filter) (ADR-032) |
| Exception handling | `ValidationException` from setters; `InvalidStatusTransitionException` in status rules; multi-catch in `fromRecord` |

## Key specs to follow

- **File blueprint (your files, fields, signatures):** [BLUEPRINT.md](../../../docs/BLUEPRINT.md#pet--julyan)
- Pet fields, subclasses, limits, `pets.txt` layout: [DATA-MODEL.md §Pet](../../../docs/DATA-MODEL.md#pet-abstract--package-pet-owner-julyan)
- Pet validation rules + messages: [VALIDATION.md §Pets](../../../docs/VALIDATION.md#pets-julyan)
- Pet status flow, manual status update, delete rules: [BUSINESS-RULES.md](../../../docs/BUSINESS-RULES.md#pet-status-flow)
- What users vs admins see: ADR-020 in [DECISIONS.md](../../../docs/DECISIONS.md)
- Methods Julyan must provide (exact signatures): [TEAM.md §Julyan provides](../../../docs/TEAM.md#julyan-provides-petservice)
- Menu options: [MAP.md §Menu tree](../../../docs/MAP.md#menu-tree)

## Acceptance criteria

**Admin pet management**
- Add: choose type first, then common fields, then the type-specific field. New pet is `AVAILABLE`, gets the
  next `P####` ID, and is saved.
- Age and weight limits change with the type (a 40-year-old dog is rejected; a 40-year-old bird is allowed).
- Update: Enter keeps each current value; every new value is validated; confirm before saving.
- Delete: blocked if the pet has any application or record (ADR-022); confirm before deleting.
- Manual status update follows the table in BUSINESS-RULES.md; same-status is rejected.

**Browse and Pet Passport**
- Regular Users see only `AVAILABLE` and `PENDING_ADOPTION` pets; admins see all.
- Summary list shows ID, name, type, breed, age, gender, status.
- Pet Passport shows every field plus the type-specific details, with readable labels.
- From the passport, a Regular User viewing an `AVAILABLE` pet is offered "Apply for this pet? (y/n)" → calls
  Ace's `ApplicationFormScreen.submitFor(petId)`. Admins and non-available pets get no offer.

**Search and filter**
- Keyword search matches name, breed, or type (case-insensitive, partial).
- Filters: type, breed, gender, age range, status — several can be combined (AND).
- No results → `[INFO] No pets match your search.`

**Workflow methods for Ace/Derick** (`markPendingAdoption`, `markAvailable`, `markAdopted`,
`revertToPendingAdoption`) check the current status, throw `InvalidStatusTransitionException` when wrong, and
save.

## Dependencies on others

| Need | From | Method |
|---|---|---|
| Delete-pet rule, manual status rule | Ace | `hasAnyApplicationForPet`, `hasApprovedApplicationForPet` |
| Delete-pet rule, manual status rule | Derick | `hasRecordForPet` |
| "Apply for this pet" | Ace | `ApplicationFormScreen.submitFor(petId)` |
| Everything else | Kurt | `DataStore.pets()`, `SessionManager`, `InputHelper`, `Validator`, `ConsoleUI`, `Menu`, `SearchStrategy`, `AllOfStrategy` |
