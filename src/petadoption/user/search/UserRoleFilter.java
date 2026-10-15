package petadoption.user.search;

import petadoption.core.SearchStrategy;
import petadoption.user.Role;
import petadoption.user.User;

/**
 * Strategy to match users by Role.
 */
public class UserRoleFilter implements SearchStrategy<User> {

    private final Role role;

    public UserRoleFilter(Role role) {
        this.role = role;
    }

    @Override
    public boolean matches(User item) {
        if (item == null || role == null) {
            return false;
        }
        return item.getRole() == role;
    }
}
