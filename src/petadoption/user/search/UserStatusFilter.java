package petadoption.user.search;

import petadoption.core.SearchStrategy;
import petadoption.user.AccountStatus;
import petadoption.user.User;

/**
 * Strategy to match users by AccountStatus.
 */
public class UserStatusFilter implements SearchStrategy<User> {

    private final AccountStatus status;

    public UserStatusFilter(AccountStatus status) {
        this.status = status;
    }

    @Override
    public boolean matches(User item) {
        if (item == null || status == null) {
            return false;
        }
        return item.getAccountStatus() == status;
    }
}
