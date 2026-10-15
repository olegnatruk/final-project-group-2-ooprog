# Ace — tasks

Tick `[x]` when the item is merged into `main` and verified by running the app. Phases follow
[TEAM.md §Build order](../../../docs/TEAM.md#build-order).

## Phase 1 — Model

- [x] Enums: `ApplicationStatus`, `LivingSituation`, `RejectionReason` — each with `getLabel()`
- [x] `AdoptionApplication`: private fields, validating setters for editable fields, **no `setStatus`**
- [x] Status methods `approve()`, `reject(reason)`, `cancel()`, `revokeApproval(reason)` — each checks the current status, throws `InvalidStatusTransitionException`, sets `decisionDate`/`decisionReason`
- [x] Cross-field rule: experience details required only when `hasPreviousPets` is true
- [x] `toRecord()` and `AdoptionApplication.fromRecord(String[] fields)`

## Phase 2 — Skeleton (others depend on these)

- [x] `ApplicationService` with every method in [TEAM.md §Ace provides](../../../docs/TEAM.md#ace-provides-applicationservice--form)
- [x] `ApplicationFormScreen.submitFor(String petId)` signature
- [x] Register `applications()` in `DataStore`

## Phase 3 — Features

- [x] Submit with all checks (role, pet available, duplicate, max 3 pending); pre-filled contact/address
- [x] User: view own list, view details, update pending, cancel pending
- [x] Admin: view all, review details, approve (with side effects), reject (reason list), revoke, delete invalid
- [x] `ApplicationDisplay`: `printApplicationTable`, `printDetails`
- [x] `application/search/`: `ApplicationStatusFilter`, `ApplicantNameSearch`, `ApplicationPetSearch`
- [x] `ApplicationService.search` through `private final RecordFinder<AdoptionApplication> finder` + `setStrategy` (ADR-032)
- [x] `UserApplicationMenu` and `AdminApplicationMenu` with all options from MAP.md
- [x] Wire both menus into `UserMenu` / `AdminMenu` (one line each)

## Verification checklist

- [x] Admin tries to apply → blocked
- [x] Apply to `ADOPTED` / `PENDING_ADOPTION` pet → blocked; apply twice to the same pet → blocked; 4th pending app → blocked
- [x] After a rejection, applying again to the same (available) pet → allowed
- [x] Reason under 20 characters; experience = yes with blank details; occupation `123`
- [x] User B enters user A's application ID → "not found"
- [x] Update / cancel an `APPROVED` app → blocked
- [x] Approve: two users applied to the same pet → one approved, the other auto-rejected; pet shows Pending Adoption
- [x] Approve an already approved app → blocked
- [x] Revoke an approval that already has a record → blocked; without a record → pet back to Available
- [x] Delete an `APPROVED` app → blocked
- [x] Answer `n` at every confirmation → nothing changes
- [x] Restart → statuses, dates, and reasons persisted

## Phase 5 — Polish

- [x] Docs pass: MAP.md rows for `application/` marked ✅, contracts match code
