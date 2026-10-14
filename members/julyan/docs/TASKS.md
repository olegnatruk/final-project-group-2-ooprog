# Julyan — tasks

Tick `[x]` when the item is merged into `main` and verified by running the app. Phases follow
[TEAM.md §Build order](../../../docs/TEAM.md#build-order).

## Phase 1 — Models (can start before Kurt's Phase 1 merges; finish after)

- [ ] Enums: `PetType`, `Gender`, `PetStatus`, `VaccinationStatus`, `DogSize` — each with `getLabel()`
- [ ] `Pet` (abstract): private fields, validating setters (age/weight use `getMaxAgeYears()`/`getMaxWeightKg()`), abstract methods, `toRecord()`
- [ ] `Dog`, `Cat`, `Bird`: extra field, overrides of max age, max weight, `getType()`, `getTypeSpecificDetails()`
- [ ] `Pet.fromRecord(String[] fields)`: reads `type`, builds the right subclass; multi-catch for bad values

## Phase 2 — Skeleton

- [ ] `PetService` with every method in [TEAM.md §Julyan provides](../../../docs/TEAM.md#julyan-provides-petservice) (bodies may throw `UnsupportedOperationException` at first)
- [ ] Ask Kurt to register `pets()` in `DataStore` with `Pet::fromRecord`

## Phase 3 — Features

- [ ] `PetService`: add, update, delete (with ADR-022 check), getAllPets, getVisiblePets, getPetById
- [ ] `PetService.search`: `private final RecordFinder<Pet> finder`; `finder.setStrategy(strategy)` then `finder.find(...)` (ADR-032)
- [ ] `PetService`: workflow transitions `markPendingAdoption`, `markAvailable`, `markAdopted`, `revertToPendingAdoption`
- [ ] `PetService`: manual status update with the BUSINESS-RULES.md table
- [ ] `pet/search/`: `PetKeywordSearch`, `PetTypeFilter`, `PetBreedFilter`, `PetGenderFilter`, `PetAgeRangeFilter`, `PetStatusFilter`, `PetVisibleToUsersFilter`
- [ ] `PetDisplay`: `printPetTable`, `printPassport`
- [ ] `PetAdminMenu`: all 8 options from MAP.md
- [ ] `PetBrowseMenu`: list, keyword search, combined filters, Pet Passport, "Apply for this pet" → `ApplicationFormScreen.submitFor`
- [ ] Wire `PetAdminMenu` into `AdminMenu` and `PetBrowseMenu` into `UserMenu` (one line each)

## Verification checklist

- [ ] Add pet with: blank name, `|` in description, age `abc`, age `-1`, dog age 26, bird age 60 (OK), weight `0`, cat weight 20, weight `4.555`, description under 10 characters
- [ ] Update: press Enter on every field → nothing changes; change one field → only that changes
- [ ] Delete a pet that has a (rejected) application → blocked; delete an unused pet → removed after `y`
- [ ] Manual status: AVAILABLE → ADOPTED with no record → blocked; same status → rejected
- [ ] Regular User cannot see `ADOPTED` pets in browse, search, or passport
- [ ] Combined filter: Dog + Female + Available returns only matches; no matches → info message
- [ ] Restart → pets and subclass fields load correctly

## Phase 5 — Polish

- [ ] A few demo pets of each type for the presentation
- [ ] Docs pass: MAP.md rows for `pet/` marked ✅, contracts match code
