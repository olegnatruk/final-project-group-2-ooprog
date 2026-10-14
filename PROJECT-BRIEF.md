# Pet Adoption System — project brief

A Java console application that organizes the pet adoption process. People who want to adopt can browse pets,
read each pet's full profile (the **Pet Passport**), apply to adopt, and follow their application. Shelter
admins manage the pets, the user accounts, the applications, and the official records of completed adoptions.

This is the **final project for OOPROG (Object-Oriented Programming)**, Group 2, section C2B.

## Who it's for

- **Regular Users** — prospective adopters. They need to find a suitable pet, learn about it before
  committing, apply, and see where their application stands.
- **Admins** — shelter staff. They need to keep pet information accurate, decide on applications, and keep
  a trustworthy history of completed adoptions.
- **The professor** — the real grader. The system must visibly demonstrate: Login, CRUD, multiple roles, the
  four OOP pillars, exception handling, the Singleton and Strategy design patterns, and thorough input
  validation.

## What problem it solves

Adoption information spread across paper forms and chats is hard to track: which pets are still available,
who applied for which pet, which applications were decided, and who adopted whom. The system keeps all of it in
one place, enforces a consistent adoption workflow (Pending → Approved/Rejected/Cancelled; Available →
Pending Adoption → Adopted), and gives adopters full pet information before they apply.

## Scope

- Two roles: Admin and Regular User.
- Four record types with full CRUD: User accounts, Pets, Adoption Applications, Adoption Records.
- Search and filter for every record type; adoption statistics by month or year.
- Data persists between runs in text files.

## Non-goals

Do not propose these. They are deliberately out of scope (see [docs/DECISIONS.md](docs/DECISIONS.md)):

- No GUI (Swing/JavaFX) — console only.
- No database — text files only.
- No password hashing or encryption — plaintext, by course scope.
- No external libraries or build tools (Maven/Gradle).
- No payments, adoption fees, email/SMS notifications, or image uploads.
- No multi-user concurrency — one person uses the app at a time.
- No networking or web interface.
