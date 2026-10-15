package petadoption.user.search;

import petadoption.core.SearchStrategy;
import petadoption.user.User;

/**
 * Strategy to match users whose full name, username, or email contains a keyword (case-insensitive).
 */
public class UserKeywordSearch implements SearchStrategy<User> {

    private final String keyword;

    public UserKeywordSearch(String keyword) {
        this.keyword = keyword == null ? "" : keyword.trim().toLowerCase();
    }

    @Override
    public boolean matches(User item) {
        if (item == null || keyword.isEmpty()) {
            return false;
        }
        return item.getFullName().toLowerCase().contains(keyword)
                || item.getUsername().toLowerCase().contains(keyword)
                || item.getEmail().toLowerCase().contains(keyword);
    }
}
