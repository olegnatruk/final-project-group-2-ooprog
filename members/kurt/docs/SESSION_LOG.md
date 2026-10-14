# Kurt — session log

Append-only history of **Kurt's** work sessions, so an AI agent (or Kurt) can pick up where the last session
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

## 2026-10-14 — All Kurt phases implemented (branch `kurt/foundation`)

Implemented every Kurt file from [BLUEPRINT.md](../../../docs/BLUEPRINT.md) in three commits (Phase 1
foundation, Phase 2 persistence/session/users, Phase 3 auth/menus/Main). The app runs: fresh start seeds
`admin`/`admin123` as `U0001`; register, login (username or email, any case), 3-attempt limit, disabled
accounts, role routing, logout. Other members' menu options print `Coming soon - <feature> (<owner>).`

Choices made while coding (all reflected in BLUEPRINT/TEAM):
- `Repository` constructor takes an `entityName` for messages ("User U0007 not found.").
- `User` has no `getPassword()`; use `passwordMatches(...)`. Setters are `final` so the constructor can call
  them safely.
- `SessionManager.login/logout` are package-private; only `AuthService` calls them.
- `AuthService.createAccount(..., Role.ADMIN)` requires a logged-in admin. Saving a new account that fails
  removes it from memory again, then re-throws.
- Enums override `toString()` to return the label (CONVENTIONS.md).
- Build command now uses `--release 21` (this machine has JDK 25).

Verified by scripted runs (piped input from a scratch folder): every item in TASKS.md "Verification
checklist", plus `+639…` contact normalization and input ending mid-prompt (clean exit).

Next: PR review/merge; then Phase 4 — wire each member's menu as it lands and register their repositories in
`DataStore`; Phase 5 demo script.
