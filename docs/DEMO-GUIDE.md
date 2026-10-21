# Demo Guide: where every requirement lives and how to show it

A cheat sheet for the finals defense. Each requirement has:
- **Say:** one or two sentences to explain it.
- **Open:** the file and line to show. Links are relative to `docs/`; line numbers are from 2026-10-21.
- **Show:** what to type in the running app.

Build and run from the repo root. To start from an empty system, rename `data/` first.

```bash
javac --release 21 -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out petadoption.Main
```

The default admin is `admin` / `admin123`.

---

## 1. Encapsulation

**Say:** All fields are `private`. The only way to change one is a setter that validates first and throws `ValidationException` on bad input. Some fields have no setter at all. For example, an application's status changes only through `approve()` / `reject()`, which check the workflow rules.

**Open:**
- [Pet.java:43](../src/petadoption/pet/Pet.java#L43): `private String name;`
- [Pet.java:159](../src/petadoption/pet/Pet.java#L159): `setName` validates before saving.
- [AdoptionApplication.java:49](../src/petadoption/application/AdoptionApplication.java#L49): private `status`, no `setStatus`. Status changes only through [approve()](../src/petadoption/application/AdoptionApplication.java#L121) and [reject()](../src/petadoption/application/AdoptionApplication.java#L134).
- [Repository.java:115](../src/petadoption/core/Repository.java#L115): `findAll()` returns a read-only copy (`List.copyOf`).

**Show:** Manage Pets → Add Pet → name `B@d!` gives an error and asks again.

## 2. Abstraction

**Say:** Abstract classes and interfaces describe *what* something does and hide *how*. A menu calls `petService.deletePet(id)` and never sees the file code.

**Open:**
- [Pet.java:26](../src/petadoption/pet/Pet.java#L26): `abstract class Pet`, with abstract methods at [line 87](../src/petadoption/pet/Pet.java#L87).
- [User.java:20](../src/petadoption/user/User.java#L20): `abstract class User`, with `getRole()` and `canApplyForAdoption()` abstract.
- [Menu.java:15](../src/petadoption/ui/Menu.java#L15): abstract base for every menu.
- Interfaces: [Storable](../src/petadoption/core/Storable.java), [SearchStrategy](../src/petadoption/core/SearchStrategy.java), [StatisticsStrategy](../src/petadoption/record/StatisticsStrategy.java).

## 3. Inheritance

**Say:** Shared fields and rules sit in a parent class; children add only what is different.

| Parent | Children |
|---|---|
| `Pet` | [Dog](../src/petadoption/pet/Dog.java#L9) (size), [Cat](../src/petadoption/pet/Cat.java) (indoor only), [Bird](../src/petadoption/pet/Bird.java) (can fly) |
| `User` | [Admin](../src/petadoption/user/Admin.java#L8), [RegularUser](../src/petadoption/user/RegularUser.java) |
| `Menu` | [AdminMenu](../src/petadoption/ui/AdminMenu.java#L11), `UserMenu`, `PetAdminMenu`, and all other menus |
| `PetAdoptionException` | 7 exceptions in [exception/](../src/petadoption/exception/) |

## 4. Polymorphism

**Say:**
- **Overriding:** each pet type gives its own age and weight limits. `Pet` calls `getMaxAgeYears()` without knowing which subclass it is. The limits are Dog 25 yrs/100 kg, Cat 30/15 and Bird 80/2.
- **Overloading:** the same method name takes different parameters.

**Open:**
- [Pet.java:169](../src/petadoption/pet/Pet.java#L169): `setAgeYears` calls `getMaxAgeYears()`.
- [Dog.java:43](../src/petadoption/pet/Dog.java#L43) and [Bird.java:40](../src/petadoption/pet/Bird.java#L40): different overrides of `getMaxAgeYears()`.
- [Admin.java:23](../src/petadoption/user/Admin.java#L23) and [RegularUser.java:23](../src/petadoption/user/RegularUser.java#L23): `canApplyForAdoption()` returns false or true. It's used in [ApplicationFormScreen.java:26](../src/petadoption/application/ApplicationFormScreen.java#L26).
- `Menu.handleChoice()`: every menu overrides it.
- Overloading: [InputHelper.readInt(prompt)](../src/petadoption/util/InputHelper.java#L46) and [readInt(prompt, min, max)](../src/petadoption/util/InputHelper.java#L51).

**Show:**
- Manage Pets → Update Pet Information on a **Dog** → age `40` is rejected.
- Do the same on a **Bird** → `40` is accepted.

## 5. Exception handling

**Say:** We made our own exception tree, so each error has a clear meaning. Errors are caught where we can do something useful, and the program never crashes on bad input.

**Open:**
- [PetAdoptionException.java:10](../src/petadoption/exception/PetAdoptionException.java#L10): the base class. Its 7 subclasses are `ValidationException`, `RecordNotFoundException`, `AuthenticationException` and 4 more.
- [Menu.java:28](../src/petadoption/ui/Menu.java#L28): one catch for every menu. A failed action prints `[ERROR] ...` and the menu keeps running.
- [Main.java:31-40](../src/petadoption/Main.java#L31): catches go from most specific to most general (`DataFileException` → `PetAdoptionException` → `Exception`), plus `finally`.
- [Repository.java:53-68](../src/petadoption/core/Repository.java#L53): a **nested** try. One bad line is skipped; an unreadable file stops loading.
- [User.java:179](../src/petadoption/user/User.java#L179): a **multi-catch** (`IllegalArgumentException | DateTimeParseException`).

**Show:**
- Manage Pets → Delete Pet on a pet that has an application → `[ERROR] Pet has adoption history...`, and the menu continues.
- Quit, then add a junk line such as `xyz` to `data/pets.txt`. Restart and the app prints `[INFO] Skipped bad data - ...` and still runs.

## 6. Singleton (GoF)

**Say:** There must be exactly one copy of the data and one record of who is logged in. Otherwise two copies could overwrite each other's saves. A private constructor plus a static `getInstance()` guarantees this.

**Open:**
- [DataStore.java:21](../src/petadoption/core/DataStore.java#L21): `private static DataStore instance`.
- [DataStore.java:28](../src/petadoption/core/DataStore.java#L28): `private DataStore()`.
- [DataStore.java:39](../src/petadoption/core/DataStore.java#L39): `synchronized getInstance()`.
- [SessionManager.java:13-22](../src/petadoption/auth/SessionManager.java#L13): the same three parts.

## 7. Strategy (GoF)

**Say:** Each search or filter rule is its own small class implementing one interface. The service swaps the rule at runtime without changing its own code. Statistics work the same way: monthly and yearly are two strategies.

**Open:**
- [SearchStrategy.java:14](../src/petadoption/core/SearchStrategy.java#L14): `boolean matches(T item)`.
- [RecordFinder.java:27](../src/petadoption/core/RecordFinder.java#L27): `setStrategy(...)` swaps the rule (this class is the context).
- [PetService.java:83](../src/petadoption/pet/PetService.java#L83): `search(strategy)` uses it.
- Concrete strategies live in [pet/search/](../src/petadoption/pet/search/), for example `PetTypeFilter` and `PetGenderFilter`. [AllOfStrategy](../src/petadoption/core/AllOfStrategy.java#L11) combines several.
- [StatisticsService.java:29](../src/petadoption/record/StatisticsService.java#L29): `setStrategy`. [StatisticsMenu.java:57](../src/petadoption/record/StatisticsMenu.java#L57) and [line 71](../src/petadoption/record/StatisticsMenu.java#L71) choose Monthly or Yearly.

**Show:**
- Browse Pets → Filter Pets → by type, then add gender.
- Admin → Adoption Statistics → Monthly, then Yearly.

## 8. Login and roles

**Say:**
- Login accepts a username or an email.
- After 3 wrong tries, the app goes back to the welcome menu.
- The role decides which menu you get. Every service also checks the role again, so a hidden option can't be reached another way.

**Open:**
- [AuthService.java:35](../src/petadoption/auth/AuthService.java#L35): `login`.
- [WelcomeMenu.java:49](../src/petadoption/auth/WelcomeMenu.java#L49): the 3-attempt loop.
- [WelcomeMenu.java:70](../src/petadoption/auth/WelcomeMenu.java#L70): `ADMIN ? new AdminMenu() : new UserMenu()`.
- [SessionManager.java:53](../src/petadoption/auth/SessionManager.java#L53): `requireAdmin()`, called at the start of each admin action (for example [PetService.java:98](../src/petadoption/pet/PetService.java#L98)).

**Show:**
- Log in as `admin` and you see 7 options. A registered user sees 5.
- Enter a wrong password 3 times → `Too many failed attempts`.

## 9. CRUD

| Entity | Create | Read | Update | Delete | Menu |
|---|---|---|---|---|---|
| Pet | Add Pet | View All / Passport / Search | Update Info / Status | Delete Pet | Admin → Manage Pets |
| Application | Apply (user) | View / Review | Update pending (user), Approve / Reject (admin) | Cancel (user), Delete invalid (admin) | My Applications / Adoption Applications |
| Adoption record | Create from approved app | View history / details | Update record | Delete record | Admin → Adoption Records |
| User | Register / Add user | View users / My Profile | Update user / profile | Delete user | Manage Users / My Profile |

Each entity uses the same layering: **Menu** (asks and prints) → **Service** (rules and role checks) → **Repository** (memory and file). Examples are [PetAdminMenu.java:49-55](../src/petadoption/pet/PetAdminMenu.java#L49) → [PetService.addPet / updatePet / deletePet](../src/petadoption/pet/PetService.java#L94) → `Repository`.

---

## 10. Demo script (about 5 minutes, start with no `data/` folder)

1. **Start the app.** You see `Default admin account created`. *(Main, Singleton DataStore)*
2. **Register** a user and enter a bad email first, which re-prompts. *(Encapsulation, validation)*
3. **Login** with a wrong password 3 times. *(Login, exception)* Then log in as the new user and you see the **5-option User Menu**. *(Roles)* Browse Pets shows `No pets listed yet.` Log out.
4. Log in as **`admin` / `admin123`**. You see the **7-option Admin Menu**. *(Roles)*
5. Go to Manage Pets:
   - **Add** a Dog and a Bird. *(Create, inheritance)*
   - **View All**. *(Read)*
   - **Update** the Dog's age to 40, which is rejected. Do the same for the Bird, which is accepted. *(Update, polymorphism)*
   - **Search & Filter** by type. *(Strategy)*
6. Log out. As the user: Browse Pets → View Pet Passport → apply for the Dog. *(Create application)*
7. As the admin:
   - Adoption Applications → **Approve**. Adoption Records → **Create record**.
   - Adoption Statistics → Monthly, then Yearly. *(Strategy)*
8. Manage Pets:
   - **Delete** the Dog, which is blocked because it has history. *(Exception, the menu keeps running)*
   - **Delete** the Bird, which works. *(Delete)*
9. Exit, add a junk line to `data/pets.txt`, and restart. The app shows a skip warning. *(Nested try-catch)*

## Who presents what

| Member | Part | Requirements to explain |
|---|---|---|
| Kurt | foundation, login | Singleton, exception hierarchy + `Main`/`Menu` catches, login and roles, `Menu` (abstraction/inheritance) |
| Julyan | pets | Inheritance and polymorphism (`Pet` → `Dog`/`Cat`/`Bird`), pet CRUD, `SearchStrategy` pet filters |
| Ace | applications | Encapsulation (`approve()`/`reject()` instead of `setStatus`), application CRUD and workflow |
| Derick | users, records, statistics | `StatisticsStrategy` (monthly/yearly), user and record CRUD, `canApplyForAdoption()` |
