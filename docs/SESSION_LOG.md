# Session log (shared)

A running, append-only record of what shipped in each work session that affects **the whole team** —
architecture, shared contracts, decisions. Member-only progress goes in `members/<name>/docs/SESSION_LOG.md`.

**Why this exists:** AI agents start every session with no memory. Automated memory tools can break and do
not travel with `git clone`. This file is the durable fallback: plain text, in Git, readable by anyone.

**Convention:** one dated entry per session (newest at the bottom). Say what changed and why. Do not repeat
`git log`; link to commits, PRs, ADRs, or GOTCHAS entries instead. Record declined or deferred ideas briefly
so they are not proposed again from scratch.

---

## 2026-10-14 — Documentation bootstrap (no code yet)

Set up the shared documentation and per-member agent context before any coding started. Inputs: the
professor's requirements (Login, CRUD, roles, 4 OOP pillars, exception handling, Singleton + Strategy, all
types of validations) and the group's feature spec, copied verbatim to
[reference/GROUP2-SPEC.md](reference/GROUP2-SPEC.md).

Group decisions: console UI, file storage, plaintext passwords, Java 21 with plain `javac`, one shared `src/`
tree with package-per-domain ownership, split by domain (Kurt core/auth, Julyan pets, Ace applications,
Derick users/records/statistics). See ADR-001 to ADR-005 in [DECISIONS.md](DECISIONS.md).

The spec had gaps and contradictions (status flow after approval, what happens on revoke, duplicate rule,
admin adoption history, record status field, exact validation limits, delete rules). Each got a concrete
default as a **Proposed — group confirm** ADR (ADR-006 to ADR-030) so coding can start from one source. The
group should review those ADRs together and mark each Accepted or change it.

Created: `README.md`, `AGENTS.md`, `CLAUDE.md`, `PROJECT-BRIEF.md`, `.gitignore`, `docs/*`, and
`members/{kurt,julyan,ace,derick}/docs/*`.

Deferred: sample/demo data in Git (ADR-002 mentions a possible `sample-data/` folder); a scripted test
input file for demo runs.

## 2026-10-14 — App file blueprint

Added [BLUEPRINT.md](BLUEPRINT.md): the full planned source tree (84 `.java` files across `core`, `auth`, `ui`,
`util`, `exception`, `user`, `pet`, `application`, `record`) with owner, kind, extends/implements, fields, and
method signatures for every file, a package dependency diagram, and a build checklist. Cross-member contract
methods are marked ★ and mirror [TEAM.md §Contracts](TEAM.md#contracts).

New planned files introduced while detailing the blueprint: `pet/PetDisplay` and
`application/ApplicationDisplay` (shared printing so admin and user screens format the same way) and the
`application/RejectionReason` enum (ADR-028's reason list). `Pet.setStatus` is planned as package-private so
only `PetService` can change pet status. MAP.md's file list now points to BLUEPRINT.md instead of duplicating it.

## 2026-10-14 — Aligned the design with the course modules

Checked the plan against the course's Design Patterns and Exception Handling slides. Four gaps were closed with
new Proposed ADRs in [DECISIONS.md](DECISIONS.md):

- **ADR-031:** nested try-catch (outer per file, inner per line) with re-throw in `Repository.load()` — the
  module teaches nested try-catch and nothing used it.
- **ADR-032:** `Repository.find` removed; every service search goes through its own `RecordFinder` and
  `setStrategy`, so the Strategy context and runtime swap are visible. `RecordFinder` and `StatisticsService`
  got constructors that take a strategy, matching the module's `Sorter` example.
- **ADR-033:** written reason why all custom exceptions are checked (recoverable conditions) and where the
  JDK's unchecked exceptions are caught.
- **ADR-034:** `getInstance()` of both Singletons is `synchronized`, with a prepared answer for the
  thread-safety question.

Updated to match: ARCHITECTURE, BLUEPRINT, TEAM (contracts), CONVENTIONS, and every member's CONTEXT/TASKS.
