# Team, contracts, and workflow

Who owns what, which methods each member provides to the others, the build order, and the Git workflow.

---

## Ownership

| Member | Folder | Packages / files owned |
|---|---|---|
| **Kurt** — Barbin, Kurt Angelo | `members/kurt/` | `Main`, `core/*`, `auth/*`, `ui/*`, `util/*`, `exception/*`, `user/User`, `user/Admin`, `user/RegularUser`, `user/Role`, `user/AccountStatus` |
| **Julyan** — Lingao, Julianne Therese | `members/julyan/` | `pet/*` (incl. `pet/search/*`) |
| **Ace** — Cayabyab, Ace Justine | `members/ace/` | `application/*` (incl. `application/search/*`) |
| **Derick** — Inandan, Derick | `members/derick/` | `user/UserService`, `user/ProfileMenu`, `user/UserManagementMenu`, `user/search/*`, `record/*` (incl. `record/search/*`) |

Shared docs in `docs/` belong to everyone. Whoever changes behavior updates the matching doc.

**Spec feature → owner**

| Spec feature | Owner |
|---|---|
| Register, Login/Logout, Admin Login/Logout | Kurt |
| Manage Profile | Derick |
| Browse Pets, View Pet Passport, Search/Filter Pets | Julyan |
| Submit / View / Update / Cancel Adoption Application (user) | Ace |
| View Adoption History (user) | Derick |
| Manage Pets, Update Pet Status | Julyan |
| Manage Users (create, view, update, disable, delete) | Derick |
| View / Review / Approve / Reject Applications, Delete Invalid Applications (admin) | Ace |
| Manage Adoption Records, View Adoption History (admin) | Derick |
| Search and Filter Records (admin) | Each domain owner (ADR-029) |
| View Adoption Statistics | Derick |
| Validation framework, exception hierarchy, file storage, Singletons | Kurt |

---

## Contracts

These are the public methods members call across package lines (marked ★ in [BLUEPRINT.md](BLUEPRINT.md),
which has every other signature too). **Keep these signatures stable.** If you
must change one, update this section in the same PR and tell the members who call it.

All methods that can fail declare `throws` with a `PetAdoptionException` subclass (shown as `throws ...`).

### Kurt provides

```java
// core
DataStore.getInstance()                       // Singleton (synchronized, ADR-034)
dataStore.users() / pets() / applications() / records()   // Repository<T>

Repository<T>:
  void add(T item) throws DuplicateRecordException
  void remove(String id) throws RecordNotFoundException
  Optional<T> findById(String id)
  T getById(String id) throws RecordNotFoundException
  List<T> findAll()                            // unmodifiable
  String nextId()
  void save() throws DataFileException

interface SearchStrategy<T> { boolean matches(T item); }
class AllOfStrategy<T> implements SearchStrategy<T>   // new AllOfStrategy<>(List.of(f1, f2))
class RecordFinder<T> { RecordFinder(); RecordFinder(SearchStrategy<T> s);
                        void setStrategy(SearchStrategy<T> s); List<T> find(List<T> items); }
// Every service search(...) goes through its own RecordFinder (ADR-032) — there is no Repository.find

// auth
SessionManager.getInstance()                  // Singleton (synchronized, ADR-034)
  User getCurrentUser()
  boolean isLoggedIn()
  User requireLoggedIn() throws UnauthorizedAccessException
  User requireAdmin() throws UnauthorizedAccessException
  User requireRegularUser() throws UnauthorizedAccessException

AuthService:
  User createAccount(String firstName, String lastName, String email, String contactNumber,
                     String address, String username, String password, Role role) throws ...
  boolean isUsernameTaken(String username)
  boolean isEmailTaken(String email, String excludeUserId)   // excludeUserId = null when creating
  // createAccount with Role.ADMIN requires a logged-in admin

// user (models)
User.passwordMatches(String candidate)       // there is no getPassword()

// ui
abstract class Menu {
  public final void run()
  protected abstract String getTitle()
  protected abstract String[] getOptions()          // last option = Back / Logout / Exit
  protected abstract boolean handleChoice(int choice) throws PetAdoptionException  // false = leave menu
}

// util — every read re-prompts until valid
InputHelper.readMenuChoice(int optionCount)        // 1..optionCount
InputHelper.readInt(String prompt)  /  readInt(String prompt, int min, int max)
InputHelper.readDouble(String prompt, double min, double max)
InputHelper.readText(String prompt)  /  readText(String prompt, int minLen, int maxLen)
InputHelper.readValidated(String prompt, TextRule rule)        // rule = a Validator method reference
InputHelper.readOptional(String prompt, String current, TextRule rule)  // Enter keeps current
InputHelper.readYesNo(String prompt)
InputHelper.readEnum(String prompt, E[] values)    // numbered list of getLabel()
InputHelper.readDate(String prompt, LocalDate defaultValue)    // Enter = default
InputHelper.readId(String prompt, String prefix)   // "P", "U", "APP", "REC"
InputHelper.confirm(String action)                 // "Are you sure you want to <action>? (y/n)"

Validator.validateName / validateEmail / normalizeContactNumber / validateUsername /
          validatePassword(password, username) / validateAddress / requireLength /
          requireNoDelimiter / requireRange                   // throw ValidationException

ConsoleUI.printHeader / printError / printSuccess / printInfo / printTable(String[] headers, List<String[]> rows)
```

### Julyan provides (`PetService`)

```java
Pet getPetById(String petId) throws RecordNotFoundException
List<Pet> getAllPets()                 // admin view
List<Pet> getVisiblePets()             // AVAILABLE + PENDING_ADOPTION (ADR-020)
List<Pet> search(SearchStrategy<Pet> strategy)

// Workflow transitions — called by Ace and Derick. Each checks the current status,
// throws InvalidStatusTransitionException if wrong, and saves pets.
void markPendingAdoption(String petId) throws ...   // AVAILABLE → PENDING_ADOPTION (Ace: approve)
void markAvailable(String petId) throws ...         // PENDING_ADOPTION → AVAILABLE (Ace: revoke)
void markAdopted(String petId) throws ...           // PENDING_ADOPTION → ADOPTED (Derick: create record)
void revertToPendingAdoption(String petId) throws ... // ADOPTED → PENDING_ADOPTION (Derick: delete record)
```

### Ace provides (`ApplicationService` + form)

```java
AdoptionApplication getApplicationById(String applicationId) throws RecordNotFoundException
boolean hasAnyApplicationForPet(String petId)          // Julyan: delete-pet rule
boolean hasApprovedApplicationForPet(String petId)     // Julyan: manual status rule
boolean hasAnyApplicationForUser(String userId)        // Derick: delete-user rule
List<AdoptionApplication> getAllApplications()         // Derick: statistics
List<AdoptionApplication> getApprovedApplicationsWithoutRecord()  // Derick: create record

ApplicationFormScreen.submitFor(String petId)          // Julyan: "Apply for this pet" in Pet Passport
```

### Derick provides (`RecordService`, `UserService`)

```java
boolean hasRecordForPet(String petId)                  // Julyan: delete-pet + manual status rules
boolean hasRecordForApplication(String applicationId)  // Ace: revoke-approval rule
boolean hasRecordForUser(String userId)                // own delete-user rule
List<AdoptionRecord> getAllRecords()

User getUserById(String userId) throws RecordNotFoundException   // anyone showing names
```

---

## Build order

Work in parallel, but merge in this order so `main` always compiles.

| Phase | Kurt | Julyan | Ace | Derick |
|---|---|---|---|---|
| **1. Foundation** | `exception/*`, `util/*`, `core` interfaces (`Storable`, `RecordParser`, `SearchStrategy`, `AllOfStrategy`, `RecordFinder`), `ui/Menu`. **Merge first.** | Enums + `Pet`, `Dog`, `Cat`, `Bird` (fields, validating setters, overrides, `toRecord`/`fromRecord`) | Enums + `AdoptionApplication` (fields, setters, status methods) | `AdoptionRecord`, `StatisticsReport` |
| **2. Skeletons** | `User` models, `FileStorage`, `Repository`, `DataStore`, `SessionManager` | `PetService` skeleton with every contract method | `ApplicationService` + `ApplicationFormScreen` skeletons | `UserService`, `RecordService` skeletons |
| **3. Features** | `AuthService`, `WelcomeMenu`, `UserMenu`/`AdminMenu` shells, `Main`, seeded admin | Pet CRUD, browse, passport, search strategies, menus | Submit/update/cancel, approve/reject/revoke/delete, menus | Profile, user management, records, history, statistics |
| **4. Integration** | Full end-to-end run | Cross-checks with apps/records | Cross-checks with pets/records | Cross-checks with apps/pets |
| **5. Polish** | Demo script, docs pass | Docs pass | Docs pass | Docs pass |

**Skeleton rule:** a skeleton is the real class with the contract signatures and bodies that
`throw new UnsupportedOperationException("Not implemented yet")`. This lets others compile against your class
before you finish it. **Never create your own copy of another member's class** in your branch — it causes
conflicts and duplicate classes.

## Menu wiring

`ui/UserMenu.java` and `ui/AdminMenu.java` belong to Kurt. Kurt creates them early with every option already
listed; unfinished options call `comingSoon(...)`, which prints `[INFO] Coming soon - <feature> (<owner>).`. When your menu is ready, your PR may change **only
your own placeholder line**, for example:

```java
case 1 -> new PetAdminMenu().run();   // was: comingSoon("Manage Pets", "Julyan");
```

Any other change to those files goes through Kurt.

---

## Git workflow

Repository: `https://github.com/olegnatruk/ooprog-group2-finals` (default branch `main`).

**One-time setup**
1. The repo owner adds the other three members as collaborators (GitHub → Settings → Collaborators).
2. Recommended: protect `main` (Settings → Branches → require a pull request with 1 approval).
3. Each member clones: `git clone https://github.com/olegnatruk/ooprog-group2-finals.git`.

**Every task**
1. `git checkout main` and `git pull origin main`.
2. `git checkout -b <name>/<feature>` — e.g. `kurt/foundation`, `julyan/pet-crud`, `ace/approve-flow`,
   `derick/statistics`.
3. Work only in your own packages. Compile and run before every commit.
4. Commit with a clear message (format below).
5. Before opening the PR, update your branch: `git pull origin main` (fix conflicts, re-compile, re-run).
6. `git push -u origin <name>/<feature>` and open a Pull Request into `main`.
7. Another member reviews and merges. Delete the branch after merging.

**Commit message format:** `<type>(<area>): <what changed>`
- Types: `feat`, `fix`, `refactor`, `docs`, `chore`.
- Examples: `feat(pet): add Dog subclass with size`, `fix(application): block cancel on approved apps`,
  `docs(team): update PetService contract`.

**PR checklist**
- [ ] Compiles from a clean `out/` with the README command.
- [ ] Ran the feature, including invalid input from [VALIDATION.md](VALIDATION.md).
- [ ] Only touched my own packages (plus my one wiring line, if any).
- [ ] Updated the docs the change affects (see the table at the end of [AGENTS.md](../AGENTS.md)).
- [ ] Ticked finished items in my `members/<name>/docs/TASKS.md` and added a `SESSION_LOG.md` entry.
- [ ] No `out/`, `*.class`, `data/`, or IDE files committed.

**Merge-conflict hot spots:** `ui/UserMenu.java`, `ui/AdminMenu.java`, `core/DataStore.java`, and the docs in
`docs/`. Pull often, keep PRs small, and merge Phase 1 quickly.
