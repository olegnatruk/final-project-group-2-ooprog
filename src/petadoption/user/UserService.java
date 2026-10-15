package petadoption.user;

import java.util.List;

import petadoption.core.DataStore;
import petadoption.core.RecordFinder;
import petadoption.core.Repository;
import petadoption.core.SearchStrategy;
import petadoption.exception.PetAdoptionException;
import petadoption.exception.RecordNotFoundException;
import petadoption.exception.UnauthorizedAccessException;

/**
 * Service managing profile updates, password changes, admin user management, and user queries.
 */
public class UserService {

    private final Repository<User> users = DataStore.getInstance().users();
    private final RecordFinder<User> finder = new RecordFinder<>();

    public User getUserById(String userId) throws RecordNotFoundException {
        return users.getById(userId);
    }

    public List<User> getAllUsers() throws UnauthorizedAccessException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<User> search(SearchStrategy<User> strategy) throws UnauthorizedAccessException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // ------------------------------------------------------------------ own profile

    public User getMyProfile() throws UnauthorizedAccessException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void updateMyProfile(String firstName, String lastName, String email, String contactNumber, String address)
            throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void changeMyPassword(String currentPassword, String newPassword, String confirmPassword)
            throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // ------------------------------------------------------------------ admin only

    public User addUser(String firstName, String lastName, String email, String contactNumber, String address,
                        String username, String password, Role role) throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void updateUser(String userId, String firstName, String lastName, String email, String contactNumber,
                           String address) throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void setAccountStatus(String userId, AccountStatus status) throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void resetPassword(String userId, String newPassword, String confirmPassword)
            throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void deleteUser(String userId) throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
