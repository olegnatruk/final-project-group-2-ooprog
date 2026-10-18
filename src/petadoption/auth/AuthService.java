package petadoption.auth;

import java.time.LocalDate;

import petadoption.core.DataStore;
import petadoption.core.Repository;
import petadoption.exception.AuthenticationException;
import petadoption.exception.DataFileException;
import petadoption.exception.DuplicateRecordException;
import petadoption.exception.PetAdoptionException;
import petadoption.exception.RecordNotFoundException;
import petadoption.exception.ValidationException;
import petadoption.user.AccountStatus;
import petadoption.user.Admin;
import petadoption.user.RegularUser;
import petadoption.user.Role;
import petadoption.user.User;
import petadoption.util.Validator;

/**
 * Login, logout, registration, and account creation. Services never print; problems are thrown as
 * {@link PetAdoptionException} subclasses for the menu to show.
 */
public class AuthService {

    private final Repository<User> users = DataStore.getInstance().users();
    private final SessionManager session = SessionManager.getInstance();

    // ------------------------------------------------------------------ login / logout

    /**
     * Logs in with a username or email (any letter case) and the exact password. Wrong username and wrong
     * password give the same message, so the screen never reveals which one was wrong.
     */
    public User login(String usernameOrEmail, String password)
            throws ValidationException, AuthenticationException {
        String identifier = Validator.requireNotBlank("Username or email", usernameOrEmail);
        if (password == null || password.isEmpty()) {
            throw new ValidationException("Password", "Password is required.");
        }

        User match = null;
        for (User user : users.findAll()) {
            if (user.getUsername().equalsIgnoreCase(identifier) || user.getEmail().equalsIgnoreCase(identifier)) {
                match = user;
                break;
            }
        }
        if (match == null || !match.passwordMatches(password)) {
            throw new AuthenticationException("Invalid username/email or password.");
        }
        if (!match.isActive()) {
            throw new AuthenticationException("This account is disabled. Please contact an administrator.");
        }
        session.login(match);
        return match;
    }

    public void logout() {
        session.logout();
    }

    // ------------------------------------------------------------------ accounts

    /** Self-registration. Always creates an active Regular User. */
    public RegularUser register(String firstName, String lastName, String email, String contactNumber,
                                String address, String username, String password, String confirmPassword)
            throws ValidationException, DuplicateRecordException, DataFileException {
        if (password == null || !password.equals(confirmPassword)) {
            throw new ValidationException("Confirm password", "Passwords do not match.");
        }
        RegularUser user = new RegularUser(users.nextId(), firstName, lastName, email, contactNumber, address,
                username, password, AccountStatus.ACTIVE, LocalDate.now());
        saveNewAccount(user);
        return user;
    }

    /**
     * Creates an account with the given role. Used by admin "Add user" (Derick's UserService). Creating an
     * Admin requires an admin to be logged in.
     */
    public User createAccount(String firstName, String lastName, String email, String contactNumber,
                              String address, String username, String password, Role role)
            throws PetAdoptionException {
        if (role == null) {
            throw new ValidationException("Role", "Role is required.");
        }
        if (role == Role.ADMIN) {
            session.requireAdmin();
        }
        String id = users.nextId();
        User user = (role == Role.ADMIN)
                ? new Admin(id, firstName, lastName, email, contactNumber, address, username, password,
                        AccountStatus.ACTIVE, LocalDate.now())
                : new RegularUser(id, firstName, lastName, email, contactNumber, address, username, password,
                        AccountStatus.ACTIVE, LocalDate.now());
        saveNewAccount(user);
        return user;
    }

    /** True if any account already uses this username (ignoring upper/lower case). */
    public boolean isUsernameTaken(String username) {
        if (username == null) {
            return false;
        }
        for (User user : users.findAll()) {
            if (user.getUsername().equalsIgnoreCase(username.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * True if another account already uses this email (ignoring upper/lower case).
     *
     * @param excludeUserId the account being edited, which may keep its own email; {@code null} when creating
     */
    public boolean isEmailTaken(String email, String excludeUserId) {
        if (email == null) {
            return false;
        }
        for (User user : users.findAll()) {
            boolean sameEmail = user.getEmail().equalsIgnoreCase(email.trim());
            boolean isExcluded = excludeUserId != null && user.getUserId().equalsIgnoreCase(excludeUserId);
            if (sameEmail && !isExcluded) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates a username and checks no account uses it yet. Used as a {@code TextRule} so every form
     * rejects a taken username at the prompt instead of after the whole form.
     */
    public String checkNewUsername(String input) throws ValidationException {
        String cleaned = Validator.validateUsername(input);
        if (isUsernameTaken(cleaned)) {
            throw new ValidationException("Username", "Username is already taken.");
        }
        return cleaned;
    }

    /**
     * Validates an email and checks no other account uses it. Used as a {@code TextRule}.
     *
     * @param excludeUserId the account being edited, which may keep its own email; {@code null} when creating
     */
    public String checkNewEmail(String input, String excludeUserId) throws ValidationException {
        String cleaned = Validator.validateEmail(input);
        if (isEmailTaken(cleaned, excludeUserId)) {
            throw new ValidationException("Email", "Email is already registered.");
        }
        return cleaned;
    }

    /**
     * Creates the default admin (ADR-007) when no admin account exists, so the system is always usable.
     *
     * @return {@code true} if the admin was created now
     */
    public boolean ensureDefaultAdmin() throws PetAdoptionException {
        for (User user : users.findAll()) {
            if (user.getRole() == Role.ADMIN) {
                return false;
            }
        }
        Admin admin = new Admin(users.nextId(), "System", "Administrator", "admin@petadoption.com",
                "09000000000", "Pet Adoption Center", "admin", "admin123", AccountStatus.ACTIVE, LocalDate.now());
        saveNewAccount(admin);
        return true;
    }

    // ------------------------------------------------------------------ helpers

    /** Checks uniqueness, adds the account, and saves. If saving fails, the account is removed again. */
    private void saveNewAccount(User user) throws DuplicateRecordException, DataFileException {
        if (isUsernameTaken(user.getUsername())) {
            throw new DuplicateRecordException("Username is already taken.");
        }
        if (isEmailTaken(user.getEmail(), null)) {
            throw new DuplicateRecordException("Email is already registered.");
        }
        users.add(user);
        try {
            users.save();
        } catch (DataFileException e) {
            // Undo the in-memory change so memory and file stay the same, then re-throw.
            try {
                users.remove(user.getUserId());
            } catch (RecordNotFoundException notPossible) {
                // Cannot happen: the account was added just above.
            }
            throw e;
        }
    }
}
