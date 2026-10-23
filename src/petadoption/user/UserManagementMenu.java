package petadoption.user;

import java.util.ArrayList;
import java.util.List;

import petadoption.auth.AuthService;
import petadoption.core.AllOfStrategy;
import petadoption.core.SearchStrategy;
import petadoption.exception.PetAdoptionException;
import petadoption.exception.ValidationException;
import petadoption.ui.Menu;
import petadoption.user.search.UserKeywordSearch;
import petadoption.user.search.UserRoleFilter;
import petadoption.user.search.UserStatusFilter;
import petadoption.util.ConsoleUI;
import petadoption.util.InputHelper;
import petadoption.util.Validator;

/**
 * Admin user management menu with 9 options matching MAP.md.
 */
public class UserManagementMenu extends Menu {

    private final UserService userService = new UserService();
    private final AuthService authService = new AuthService();

    @Override
    protected String getTitle() {
        return "Manage Users";
    }

    @Override
    protected String[] getOptions() {
        return new String[] {
            "View all users",
            "View user details",
            "Add user",
            "Update user",
            "Enable/disable user",
            "Reset user password",
            "Delete user",
            "Search & filter users",
            "Back"
        };
    }

    @Override
    protected boolean handleChoice(int choice) throws PetAdoptionException {
        switch (choice) {
            case 1 -> viewAllUsers();
            case 2 -> viewUserDetails();
            case 3 -> addUser();
            case 4 -> updateUser();
            case 5 -> toggleAccountStatus();
            case 6 -> resetUserPassword();
            case 7 -> deleteUser();
            case 8 -> searchAndFilterUsers();
            case 9 -> {
                return false;
            }
            default -> ConsoleUI.printError("Unknown option.");
        }
        return true;
    }

    private void viewAllUsers() throws PetAdoptionException {
        List<User> list = userService.getAllUsers();
        printUserTable(list);
        InputHelper.pause();
    }

    private void viewUserDetails() throws PetAdoptionException {
        String userId = InputHelper.readId("Enter User ID: ", "U");
        User user = userService.getUserById(userId);
        printUserDetails(user);
        InputHelper.pause();
    }

    private void addUser() throws PetAdoptionException {
        ConsoleUI.printHeader("Add User");

        Role role = InputHelper.readEnum("Select role:", Role.values());
        String firstName = InputHelper.readValidated("First name: ",
                input -> Validator.validateName("First name", input));
        String lastName = InputHelper.readValidated("Last name: ",
                input -> Validator.validateName("Last name", input));
        String email = InputHelper.readValidated("Email: ",
                input -> authService.checkNewEmail(input, null));
        String contact = InputHelper.readValidated("Contact number: ",
                Validator::normalizeContactNumber);
        String address = InputHelper.readValidated("Address: ",
                Validator::validateAddress);
        String username = InputHelper.readValidated("Username: ",
                authService::checkNewUsername);
        String password = InputHelper.readValidated("Password: ",
                input -> Validator.validatePassword(input, username));

        if (!InputHelper.confirm("create this user")) {
            return;
        }

        User created = userService.addUser(firstName, lastName, email, contact, address, username, password, role);
        ConsoleUI.printSuccess("User " + created.getUserId() + " created successfully (" + created.getRole().getLabel() + ").");
    }

    private void updateUser() throws PetAdoptionException {
        ConsoleUI.printHeader("Update User");
        String userId = InputHelper.readId("Enter User ID: ", "U");
        User user = userService.getUserById(userId);

        String firstName = InputHelper.readOptional("First name", user.getFirstName(),
                input -> Validator.validateName("First name", input));
        String lastName = InputHelper.readOptional("Last name", user.getLastName(),
                input -> Validator.validateName("Last name", input));
        String email = InputHelper.readOptional("Email", user.getEmail(),
                input -> authService.checkNewEmail(input, user.getUserId()));
        String contact = InputHelper.readOptional("Contact number", user.getContactNumber(),
                Validator::normalizeContactNumber);
        String address = InputHelper.readOptional("Address", user.getAddress(),
                Validator::validateAddress);

        if (!InputHelper.confirm("save user changes")) {
            return;
        }

        userService.updateUser(userId, firstName, lastName, email, contact, address);
        ConsoleUI.printSuccess("User " + userId + " updated successfully.");
    }

    private void toggleAccountStatus() throws PetAdoptionException {
        ConsoleUI.printHeader("Enable/Disable User");
        String userId = InputHelper.readId("Enter User ID: ", "U");
        User user = userService.getUserById(userId);

        AccountStatus newStatus = user.isActive() ? AccountStatus.DISABLED : AccountStatus.ACTIVE;
        String action = newStatus == AccountStatus.ACTIVE ? "enable" : "disable";
        userService.checkAccountStatusChange(userId, newStatus);

        if (!InputHelper.confirm(action + " user " + userId + " (" + user.getUsername() + ")")) {
            return;
        }

        userService.setAccountStatus(userId, newStatus);
        ConsoleUI.printSuccess("User " + userId + " is now " + newStatus.getLabel() + ".");
    }

    private void resetUserPassword() throws PetAdoptionException {
        ConsoleUI.printHeader("Reset User Password");
        String userId = InputHelper.readId("Enter User ID: ", "U");
        User user = userService.getUserById(userId);

        String newPassword;
        String confirmPassword;
        while (true) {
            newPassword = InputHelper.readValidated("New password: ", input -> {
                String checked = Validator.validatePassword(input, user.getUsername());
                if (user.passwordMatches(checked)) {
                    throw new ValidationException("New password", "The password is already this user's password.");
                }
                return checked;
            });
            confirmPassword = InputHelper.readText("Confirm new password: ");
            if (newPassword.equals(confirmPassword)) {
                break;
            }
            ConsoleUI.printError("Passwords do not match. Please try again.");
        }

        if (!InputHelper.confirm("reset password for user " + userId)) {
            return;
        }

        userService.resetPassword(userId, newPassword, confirmPassword);
        ConsoleUI.printSuccess("Password for user " + userId + " has been reset.");
    }

    private void deleteUser() throws PetAdoptionException {
        ConsoleUI.printHeader("Delete User");
        String userId = InputHelper.readId("Enter User ID: ", "U");
        User user = userService.getUserById(userId);
        userService.checkCanDelete(userId);

        if (!InputHelper.confirm("permanently delete user " + userId + " (" + user.getUsername() + ")")) {
            return;
        }

        userService.deleteUser(userId);
        ConsoleUI.printSuccess("User " + userId + " deleted successfully.");
    }

    private void searchAndFilterUsers() throws PetAdoptionException {
        ConsoleUI.printHeader("Search & Filter Users");
        System.out.println("1. Search by keyword (name, username, email)");
        System.out.println("2. Filter by role");
        System.out.println("3. Filter by account status");
        System.out.println("4. Combined search and filter");
        System.out.println("5. Back");

        int choice = InputHelper.readMenuChoice(5);
        if (choice == 5) {
            return;
        }

        SearchStrategy<User> strategy = null;
        switch (choice) {
            case 1 -> {
                String keyword = InputHelper.readText("Enter search keyword (1-50 chars): ", 1, 50);
                strategy = new UserKeywordSearch(keyword);
            }
            case 2 -> {
                Role role = InputHelper.readEnum("Select role filter:", Role.values());
                strategy = new UserRoleFilter(role);
            }
            case 3 -> {
                AccountStatus status = InputHelper.readEnum("Select account status filter:", AccountStatus.values());
                strategy = new UserStatusFilter(status);
            }
            case 4 -> {
                List<SearchStrategy<User>> filters = new ArrayList<>();
                if (InputHelper.readYesNo("Filter by keyword?")) {
                    String keyword = InputHelper.readText("Keyword: ", 1, 50);
                    filters.add(new UserKeywordSearch(keyword));
                }
                if (InputHelper.readYesNo("Filter by role?")) {
                    Role role = InputHelper.readEnum("Role:", Role.values());
                    filters.add(new UserRoleFilter(role));
                }
                if (InputHelper.readYesNo("Filter by status?")) {
                    AccountStatus status = InputHelper.readEnum("Status:", AccountStatus.values());
                    filters.add(new UserStatusFilter(status));
                }
                if (filters.isEmpty()) {
                    ConsoleUI.printInfo("No filters selected.");
                    return;
                }
                strategy = new AllOfStrategy<>(filters);
            }
        }

        if (strategy != null) {
            List<User> results = userService.search(strategy);
            printUserTable(results);
            InputHelper.pause();
        }
    }

    // ------------------------------------------------------------------ display helpers

    private void printUserTable(List<User> userList) {
        String[] headers = {"User ID", "Full Name", "Username", "Email", "Contact", "Role", "Status"};
        List<String[]> rows = new ArrayList<>();
        for (User u : userList) {
            rows.add(new String[] {
                u.getUserId(),
                u.getFullName(),
                u.getUsername(),
                u.getEmail(),
                u.getContactNumber(),
                u.getRole().getLabel(),
                u.getAccountStatus().getLabel()
            });
        }
        ConsoleUI.printTable(headers, rows);
    }

    private void printUserDetails(User user) {
        ConsoleUI.printHeader("User Details - " + user.getUserId());
        ConsoleUI.printField("User ID", user.getUserId());
        ConsoleUI.printField("Full name", user.getFullName());
        ConsoleUI.printField("Username", user.getUsername());
        ConsoleUI.printField("Role", user.getRole().getLabel());
        ConsoleUI.printField("Email", user.getEmail());
        ConsoleUI.printField("Contact number", user.getContactNumber());
        ConsoleUI.printField("Address", user.getAddress());
        ConsoleUI.printField("Account status", user.getAccountStatus().getLabel());
        ConsoleUI.printField("Date created", user.getDateCreated().toString());
        ConsoleUI.printDivider();
    }
}
