package petadoption.user;

import java.util.List;

import petadoption.application.ApplicationService;
import petadoption.auth.AuthService;
import petadoption.auth.SessionManager;
import petadoption.core.DataStore;
import petadoption.core.RecordFinder;
import petadoption.core.Repository;
import petadoption.core.SearchStrategy;
import petadoption.exception.DuplicateRecordException;
import petadoption.exception.InvalidStatusTransitionException;
import petadoption.exception.PetAdoptionException;
import petadoption.exception.RecordNotFoundException;
import petadoption.exception.UnauthorizedAccessException;
import petadoption.exception.ValidationException;
import petadoption.record.RecordService;
import petadoption.util.Validator;

/**
 * Service managing user accounts, profiles, password changes, and admin user management.
 */
public class UserService {

    private final Repository<User> users = DataStore.getInstance().users();
    private final SessionManager session = SessionManager.getInstance();
    private final AuthService authService = new AuthService();
    private final RecordFinder<User> finder = new RecordFinder<>();

    // ------------------------------------------------------------------ general queries

    public User getUserById(String userId) throws RecordNotFoundException {
        return users.getById(userId);
    }

    public List<User> getAllUsers() throws UnauthorizedAccessException {
        session.requireAdmin();
        return users.findAll();
    }

    public List<User> search(SearchStrategy<User> strategy) throws UnauthorizedAccessException {
        session.requireAdmin();
        finder.setStrategy(strategy);
        return finder.find(users.findAll());
    }

    // ------------------------------------------------------------------ own profile

    public User getMyProfile() throws UnauthorizedAccessException {
        return session.requireLoggedIn();
    }

    public void updateMyProfile(String firstName, String lastName, String email, String contactNumber, String address)
            throws PetAdoptionException {
        User currentUser = session.requireLoggedIn();

        String cleanedEmail = Validator.validateEmail(email);
        if (authService.isEmailTaken(cleanedEmail, currentUser.getUserId())) {
            throw new DuplicateRecordException("Email is already registered.");
        }

        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setEmail(cleanedEmail);
        currentUser.setContactNumber(contactNumber);
        currentUser.setAddress(address);

        users.save();
    }

    public void changeMyPassword(String currentPassword, String newPassword, String confirmPassword)
            throws PetAdoptionException {
        User currentUser = session.requireLoggedIn();

        if (currentPassword == null || !currentUser.passwordMatches(currentPassword)) {
            throw new ValidationException("Current password", "Current password is incorrect.");
        }
        if (currentPassword.equals(newPassword)) {
            throw new ValidationException("New password", "New password must be different from the current password.");
        }
        if (newPassword == null || !newPassword.equals(confirmPassword)) {
            throw new ValidationException("Confirm password", "Passwords do not match.");
        }

        currentUser.setPassword(newPassword);
        users.save();
    }

    // ------------------------------------------------------------------ admin user management

    public User addUser(String firstName, String lastName, String email, String contactNumber, String address,
                        String username, String password, Role role) throws PetAdoptionException {
        session.requireAdmin();
        return authService.createAccount(firstName, lastName, email, contactNumber, address, username, password, role);
    }

    public void updateUser(String userId, String firstName, String lastName, String email, String contactNumber,
                           String address) throws PetAdoptionException {
        session.requireAdmin();
        User user = users.getById(userId);

        String cleanedEmail = Validator.validateEmail(email);
        if (authService.isEmailTaken(cleanedEmail, user.getUserId())) {
            throw new DuplicateRecordException("Email is already registered.");
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(cleanedEmail);
        user.setContactNumber(contactNumber);
        user.setAddress(address);

        users.save();
    }

    public void setAccountStatus(String userId, AccountStatus status) throws PetAdoptionException {
        checkAccountStatusChange(userId, status);
        users.getById(userId).setAccountStatus(status);
        users.save();
    }

    /**
     * Throws if the account cannot be set to this status, so the menu can stop before asking for confirmation.
     * {@link #setAccountStatus} runs the same check again.
     */
    public void checkAccountStatusChange(String userId, AccountStatus status) throws PetAdoptionException {
        User admin = session.requireAdmin();
        User target = users.getById(userId);

        if (status == null) {
            throw new ValidationException("Account status", "Account status is required.");
        }
        if (target.getAccountStatus() == status) {
            throw new ValidationException("Account status", "Account is already " + status.getLabel() + ".");
        }

        if (status == AccountStatus.DISABLED) {
            if (target.getUserId().equalsIgnoreCase(admin.getUserId())) {
                throw new ValidationException("Account status", "You cannot disable your own account.");
            }
            if (target.getRole() == Role.ADMIN && countActiveAdmins() <= 1) {
                throw new ValidationException("Account status", "Cannot disable the last active administrator.");
            }
        }
    }

    public void resetPassword(String userId, String newPassword, String confirmPassword)
            throws PetAdoptionException {
        session.requireAdmin();
        User target = users.getById(userId);

        if (newPassword == null || !newPassword.equals(confirmPassword)) {
            throw new ValidationException("Confirm password", "Passwords do not match.");
        }

        target.setPassword(newPassword);
        users.save();
    }

    public void deleteUser(String userId) throws PetAdoptionException {
        checkCanDelete(userId);
        users.remove(users.getById(userId).getUserId());
        users.save();
    }

    /**
     * Throws if the user cannot be deleted (own account, last admin, or adoption history), so the menu can stop
     * before asking for confirmation. {@link #deleteUser} runs the same check again.
     */
    public void checkCanDelete(String userId) throws PetAdoptionException {
        User admin = session.requireAdmin();
        User target = users.getById(userId);

        if (target.getUserId().equalsIgnoreCase(admin.getUserId())) {
            throw new ValidationException("User", "You cannot delete your own account.");
        }
        if (target.getRole() == Role.ADMIN && countActiveAdmins() <= 1 && target.isActive()) {
            throw new ValidationException("User", "Cannot delete the last active administrator.");
        }

        // Check history: records and applications
        RecordService recordService = new RecordService();
        if (recordService.hasRecordForUser(target.getUserId())) {
            throw new InvalidStatusTransitionException("User has adoption records. Disable the account instead.");
        }

        if (new ApplicationService().hasAnyApplicationForUser(target.getUserId())) {
            throw new InvalidStatusTransitionException("User has adoption applications. Disable the account instead.");
        }
    }

    // ------------------------------------------------------------------ helpers

    private int countActiveAdmins() {
        int count = 0;
        for (User user : users.findAll()) {
            if (user.getRole() == Role.ADMIN && user.isActive()) {
                count++;
            }
        }
        return count;
    }
}
