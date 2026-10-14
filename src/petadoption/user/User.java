package petadoption.user;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import petadoption.core.Storable;
import petadoption.exception.DataFileException;
import petadoption.exception.PetAdoptionException;
import petadoption.exception.ValidationException;
import petadoption.util.Validator;

/**
 * A system account. Abstract: every account is either an {@link Admin} or a {@link RegularUser}, and each
 * subclass decides its own role and permissions.
 *
 * <p>Encapsulation: every field is private. Editable fields have setters that validate and throw
 * {@link ValidationException}. The ID, username, role, and creation date can never change, so they have no
 * setters. The password has no getter at all; use {@link #passwordMatches(String)}.
 */
public abstract class User implements Storable {

    private static final int FIELD_COUNT = 11;
    private static final String ID_REGEX = "U\\d{4}";

    private final String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String address;
    private final String username;
    private String password;
    private AccountStatus accountStatus;
    private final LocalDate dateCreated;

    protected User(String userId, String firstName, String lastName, String email, String contactNumber,
                   String address, String username, String password, AccountStatus accountStatus,
                   LocalDate dateCreated) throws ValidationException {
        this.userId = Validator.requireMatches("User ID", userId, ID_REGEX, "User ID must look like U0001.");
        this.username = Validator.validateUsername(username);
        if (dateCreated == null) {
            throw new ValidationException("Date created", "Date created is required.");
        }
        this.dateCreated = dateCreated;
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setContactNumber(contactNumber);
        setAddress(address);
        setPassword(password);
        setAccountStatus(accountStatus);
    }

    // ------------------------------------------------------------------ role-specific behavior

    /** Each subclass returns its own role. */
    public abstract Role getRole();

    /** Whether this kind of account may submit adoption applications (admins may not, ADR-024). */
    public abstract boolean canApplyForAdoption();

    // ------------------------------------------------------------------ getters

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getUsername() {
        return username;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public boolean isActive() {
        return accountStatus == AccountStatus.ACTIVE;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    /** Checks a password attempt without ever exposing the stored password. Case-sensitive. */
    public boolean passwordMatches(String candidate) {
        return password.equals(candidate);
    }

    // ------------------------------------------------------------------ validating setters

    public final void setFirstName(String firstName) throws ValidationException {
        this.firstName = Validator.validateName("First name", firstName);
    }

    public final void setLastName(String lastName) throws ValidationException {
        this.lastName = Validator.validateName("Last name", lastName);
    }

    public final void setEmail(String email) throws ValidationException {
        this.email = Validator.validateEmail(email);
    }

    public final void setContactNumber(String contactNumber) throws ValidationException {
        this.contactNumber = Validator.normalizeContactNumber(contactNumber);
    }

    public final void setAddress(String address) throws ValidationException {
        this.address = Validator.validateAddress(address);
    }

    public final void setPassword(String password) throws ValidationException {
        this.password = Validator.validatePassword(password, username);
    }

    public final void setAccountStatus(AccountStatus accountStatus) throws ValidationException {
        if (accountStatus == null) {
            throw new ValidationException("Account status", "Account status is required.");
        }
        this.accountStatus = accountStatus;
    }

    // ------------------------------------------------------------------ persistence

    @Override
    public String getId() {
        return userId;
    }

    /** Layout: userId|role|firstName|lastName|email|contactNumber|address|username|password|accountStatus|dateCreated */
    @Override
    public String toRecord() {
        return String.join(Validator.DELIMITER,
                userId, getRole().name(), firstName, lastName, email, contactNumber, address,
                username, password, accountStatus.name(), dateCreated.toString());
    }

    /**
     * Rebuilds a user from one line of users.txt, creating an {@link Admin} or {@link RegularUser} based on
     * the role field.
     */
    public static User fromRecord(String[] fields) throws PetAdoptionException {
        if (fields.length != FIELD_COUNT) {
            throw new DataFileException("expected " + FIELD_COUNT + " fields but found " + fields.length + ".");
        }
        try {
            Role role = Role.valueOf(fields[1]);
            AccountStatus status = AccountStatus.valueOf(fields[9]);
            LocalDate created = LocalDate.parse(fields[10]);
            return switch (role) {
                case ADMIN -> new Admin(fields[0], fields[2], fields[3], fields[4], fields[5], fields[6],
                        fields[7], fields[8], status, created);
                case REGULAR_USER -> new RegularUser(fields[0], fields[2], fields[3], fields[4], fields[5],
                        fields[6], fields[7], fields[8], status, created);
            };
        } catch (IllegalArgumentException | DateTimeParseException e) {
            // Multi-catch: unknown enum name (IllegalArgumentException) or bad date (DateTimeParseException).
            throw new DataFileException("invalid value: " + e.getMessage(), e);
        } catch (ValidationException e) {
            throw new DataFileException("invalid " + e.getFieldName() + ": " + e.getMessage(), e);
        }
    }
}
