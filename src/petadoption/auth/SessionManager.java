package petadoption.auth;

import petadoption.exception.UnauthorizedAccessException;
import petadoption.user.Role;
import petadoption.user.User;

/**
 * Singleton: remembers who is logged in. Services call the {@code require...} methods to check role and
 * login before doing anything, even when the menu already hides the option.
 */
public class SessionManager {

    private static SessionManager instance;

    private User currentUser;

    private SessionManager() {
        // only getInstance() may create it
    }

    /** {@code synchronized} so only one instance can ever be created (ADR-034). */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    void login(User user) {
        this.currentUser = user;
    }

    void logout() {
        this.currentUser = null;
    }

    /** The logged-in user, or {@code null} when nobody is logged in. */
    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User requireLoggedIn() throws UnauthorizedAccessException {
        if (currentUser == null) {
            throw new UnauthorizedAccessException("Please log in first.");
        }
        return currentUser;
    }

    public User requireAdmin() throws UnauthorizedAccessException {
        User user = requireLoggedIn();
        if (user.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Only admins can do this.");
        }
        return user;
    }

    public User requireRegularUser() throws UnauthorizedAccessException {
        User user = requireLoggedIn();
        if (user.getRole() != Role.REGULAR_USER) {
            throw new UnauthorizedAccessException("Only regular users can do this.");
        }
        return user;
    }
}
