package petadoption.user;

import java.time.LocalDate;

import petadoption.exception.ValidationException;

/** An administrator: manages pets, users, applications, and adoption records. Cannot adopt (ADR-024). */
public class Admin extends User {

    public Admin(String userId, String firstName, String lastName, String email, String contactNumber,
                 String address, String username, String password, AccountStatus accountStatus,
                 LocalDate dateCreated) throws ValidationException {
        super(userId, firstName, lastName, email, contactNumber, address, username, password,
                accountStatus, dateCreated);
    }

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }

    @Override
    public boolean canApplyForAdoption() {
        return false;
    }
}
