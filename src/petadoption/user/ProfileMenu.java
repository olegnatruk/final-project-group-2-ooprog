package petadoption.user;

import petadoption.auth.AuthService;
import petadoption.auth.SessionManager;
import petadoption.exception.PetAdoptionException;
import petadoption.exception.ValidationException;
import petadoption.ui.Menu;
import petadoption.util.ConsoleUI;
import petadoption.util.InputHelper;
import petadoption.util.Validator;

/**
 * Menu for viewing and updating personal profile information, and changing password.
 * Available to both Regular Users and Admins.
 */
public class ProfileMenu extends Menu {

    private final UserService userService = new UserService();
    private final AuthService authService = new AuthService();

    @Override
    protected String getTitle() {
        return "Manage Profile";
    }

    @Override
    protected String[] getOptions() {
        return new String[] {
            "View profile",
            "Update personal information",
            "Change password",
            "Back"
        };
    }

    @Override
    protected boolean handleChoice(int choice) throws PetAdoptionException {
        switch (choice) {
            case 1 -> viewProfile();
            case 2 -> updatePersonalInfo();
            case 3 -> changePassword();
            case 4 -> {
                return false;
            }
            default -> ConsoleUI.printError("Unknown option.");
        }
        return true;
    }

    private void viewProfile() throws PetAdoptionException {
        User user = userService.getMyProfile();
        ConsoleUI.printHeader("My Profile");
        ConsoleUI.printField("User ID", user.getUserId());
        ConsoleUI.printField("Full name", user.getFullName());
        ConsoleUI.printField("Role", user.getRole().getLabel());
        ConsoleUI.printField("Username", user.getUsername());
        ConsoleUI.printField("Email", user.getEmail());
        ConsoleUI.printField("Contact number", user.getContactNumber());
        ConsoleUI.printField("Address", user.getAddress());
        ConsoleUI.printField("Account status", user.getAccountStatus().getLabel());
        ConsoleUI.printField("Member since", user.getDateCreated().toString());
        ConsoleUI.printDivider();
        InputHelper.pause();
    }

    private void updatePersonalInfo() throws PetAdoptionException {
        User user = userService.getMyProfile();
        ConsoleUI.printHeader("Update Personal Information");

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

        if (!InputHelper.confirm("save these profile changes")) {
            return;
        }

        userService.updateMyProfile(firstName, lastName, email, contact, address);
        ConsoleUI.printSuccess("Profile updated successfully.");
    }

    private void changePassword() throws PetAdoptionException {
        ConsoleUI.printHeader("Change Password");

        User user = SessionManager.getInstance().requireLoggedIn();
        String currentPassword = InputHelper.readValidated("Current password: ",
                input -> Validator.requireNotBlank("Current password", input));
        if (!user.passwordMatches(currentPassword)) {
            ConsoleUI.printError("Current password is incorrect.");
            return;
        }

        String newPassword;
        String confirmPassword;
        while (true) {
            newPassword = InputHelper.readValidated("New password: ", input -> {
                String checked = Validator.validatePassword(input, user.getUsername());
                if (checked.equals(currentPassword)) {
                    throw new ValidationException("New password",
                            "New password must be different from the current password.");
                }
                return checked;
            });
            confirmPassword = InputHelper.readText("Confirm new password: ");
            if (newPassword.equals(confirmPassword)) {
                break;
            }
            ConsoleUI.printError("Passwords do not match. Please try again.");
        }

        if (!InputHelper.confirm("change your password")) {
            return;
        }

        userService.changeMyPassword(currentPassword, newPassword, confirmPassword);
        ConsoleUI.printSuccess("Password changed successfully.");
    }
}
