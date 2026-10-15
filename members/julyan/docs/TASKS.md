# Julyan — tasks

Tick `[x]` when the item is merged into `main` and verified by running the app. Phases follow
[TEAM.md §Build order](../../../docs/TEAM.md#build-order).

## Phase 1 — Models (can start before Kurt's Phase 1 merges; finish after)

- [x] Enums: `PetType`, `Gender`, `PetStatus`, `VaccinationStatus`, `DogSize` — each with `getLabel()`
- [x] `Pet` (abstract): private fields, validating setters (age/weight use `getMaxAgeYears()`/`getMaxWeightKg()`), abstract methods, `toRecord()`
- [x] `Dog`, `Cat`, `Bird`: extra field, overrides of max age, max weight, `getType()`, `getTypeSpecificDetails()`
- [x] `Pet.fromRecord(String[] fields)`: reads `type`, builds the right subclass; multi-catch for bad values

## Phase 2 — Skeleton

- [x] `PetService` with every method in [TEAM.md §Julyan provides](../../../docs/TEAM.md#julyan-provides-petservice) (bodies may throw `UnsupportedOperationException` at first)
- [x] Ask Kurt to register `pets()` in `DataStore` with `Pet::fromRecord`

## Phase 3 — Features

- [x] `PetService`: add, update, delete (with ADR-022 check), getAllPets, getVisiblePets, getPetById
- [x] `PetService.search`: `private final RecordFinder<Pet> finder`; `finder.setStrategy(strategy)` then `finder.find(...)` (ADR-032)
- [x] `PetService`: workflow transitions `markPendingAdoption`, `markAvailable`, `markAdopted`, `revertToPendingAdoption`
- [x] `PetService`: manual status update with the BUSINESS-RULES.md table
- [x] `pet/search/`: `PetKeywordSearch`, `PetTypeFilter`, `PetBreedFilter`, `PetGenderFilter`, `PetAgeRangeFilter`, `PetStatusFilter`, `PetVisibleToUsersFilter`
- [x] `PetDisplay`: `printPetTable`, `printPassport`
- [x] `PetAdminMenu`: all 8 options from MAP.md
- [x] `PetBrowseMenu`: list, keyword search, combined filters, Pet Passport, "Apply for this pet" → `ApplicationFormScreen.submitFor`
- [x] Wire `PetAdminMenu` into `AdminMenu` and `PetBrowseMenu` into `UserMenu` (one line each)

## Verification checklist

- [x] Add pet with: blank name, `|` in description, age `abc`, age `-1`, dog age 26, bird age 60 (OK), weight `0`, cat weight 20, weight `4.555`, description under 10 characters
- [x] Update: press Enter on every field → nothing changes; change one field → only that changes
- [x] Delete a pet that has a (rejected) application → blocked; delete an unused pet → removed after `y`
- [x] Manual status: AVAILABLE → ADOPTED with no record → blocked; same status → rejected
- [x] Regular User cannot see `ADOPTED` pets in browse, search, or passport
- [x] Combined filter: Dog + Female + Available returns only matches; no matches → info message
- [x] Restart → pets and subclass fields load correctly

## Phase 5 — Polish

- [ ] A few demo pets of each type for the presentation
- [ ] Docs pass: MAP.md rows for `pet/` marked ✅, contracts match code
