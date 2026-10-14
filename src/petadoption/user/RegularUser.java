package petadoption.user;

import java.time.LocalDate;

import petadoption.exception.ValidationException;

/** A prospective adopter: browses pets and submits adoption applications. */
public class RegularUser extends User {

    public RegularUser(String userId, String firstName, String lastName, String email, String contactNumber,
                       String address, String username, String password, AccountStatus accountStatus,
                       LocalDate dateCreated) throws ValidationException {
        super(userId, firstName, lastName, email, contactNumber, address, username, password,
                accountStatus, dateCreated);
    }

    @Override
    public Role getRole() {
        return Role.REGULAR_USER;
    }

    @Override
    public boolean canApplyForAdoption() {
        return true;
    }
}
