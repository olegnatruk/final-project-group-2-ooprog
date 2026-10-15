package petadoption.record.search;

import petadoption.core.SearchStrategy;
import petadoption.exception.RecordNotFoundException;
import petadoption.record.AdoptionRecord;
import petadoption.user.User;
import petadoption.user.UserService;

/**
 * Strategy to match adoption records where the adopter's full name or username contains the keyword.
 */
public class RecordAdopterSearch implements SearchStrategy<AdoptionRecord> {

    private final String keyword;
    private final UserService userService;

    public RecordAdopterSearch(String keyword) {
        this(keyword, new UserService());
    }

    public RecordAdopterSearch(String keyword, UserService userService) {
        this.keyword = keyword == null ? "" : keyword.trim().toLowerCase();
        this.userService = userService;
    }

    @Override
    public boolean matches(AdoptionRecord item) {
        if (item == null || keyword.isEmpty()) {
            return false;
        }
        try {
            User adopter = userService.getUserById(item.getUserId());
            return adopter.getFullName().toLowerCase().contains(keyword)
                    || adopter.getUsername().toLowerCase().contains(keyword);
        } catch (RecordNotFoundException e) {
            return false;
        }
    }
}
