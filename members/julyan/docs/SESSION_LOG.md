# Julyan — session log

Append-only history of **Julyan's** work sessions, so an AI agent (or Julyan) can pick up where the last session
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
 
## 2026-10-15 — Models and skeletons (PR 1: julyan/pet-models)
 
Implemented Phase 1 models and Phase 2 skeletons:
- Enums: `PetType`, `Gender`, `PetStatus`, `VaccinationStatus`, `DogSize` with `getLabel()` and `toString()`.
- Abstract `Pet`: private fields, validating setters, polymorphic `getMaxAgeYears()` and `getMaxWeightKg()`, `getTypeSpecificDetails()`, `toRecord()`, `create()`, and static `fromRecord()`.
- Concrete subclasses: `Dog` (with `DogSize size`), `Cat` (with `boolean indoorOnly`), `Bird` (with `boolean canFly`).
- Registered `pets` repository in `core/DataStore`.
- `PetService` skeleton with all ★ contract methods (`getPetById`, `getAllPets`, `getVisiblePets`, `search`, `markPendingAdoption`, `markAvailable`, `markAdopted`, `revertToPendingAdoption`).
- Verified models via unit scratch harness (48 test cases passing) and clean compilation with `javac -Xlint:all`.
- Next step: stack branch `julyan/pet-features` for full service, menus, and search strategies.

## 2026-10-15 — Full features and menu wiring (PR 2: julyan/pet-features)

Implemented Phase 3 features, UI, and search strategies:
- `PetService`: complete CRUD, visibility scoping, manual status consistency check (ADR-014), workflow transitions (`markPendingAdoption`, `markAvailable`, `markAdopted`, `revertToPendingAdoption`), and search via `RecordFinder` strategy context (ADR-032).
- `PetDisplay`: shared `printPetTable` and `printPassport` formatting.
- `pet/search/*`: `PetKeywordSearch`, `PetTypeFilter`, `PetBreedFilter`, `PetGenderFilter`, `PetAgeRangeFilter`, `PetStatusFilter`, `PetVisibleToUsersFilter`.
- `PetAdminMenu`: full 8 options with update flow, status flow, add pet by type, delete confirmation, and search/filter.
- `PetBrowseMenu`: view available pets, keyword search, combined filter with `AllOfStrategy`, Pet Passport, and application handoff.
- Replaced placeholders in `UserMenu` and `AdminMenu` (one line each).
- Verified all acceptance criteria including invalid input matrix, visibility, and persistence (42 automated checks passed + manual console walkthrough).
