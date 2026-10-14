package petadoption.auth;

import petadoption.exception.AuthenticationException;
import petadoption.exception.PetAdoptionException;
import petadoption.exception.ValidationException;
import petadoption.ui.AdminMenu;
import petadoption.ui.Menu;
import petadoption.ui.UserMenu;
import petadoption.user.Role;
import petadoption.user.User;
import petadoption.util.ConsoleUI;
import petadoption.util.InputHelper;
import petadoption.util.Validator;

/** The first menu: Login, Register, Exit. After login it opens the home menu for the user's role. */
public class WelcomeMenu extends Menu {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    private final AuthService authService = new AuthService();

    @Override
    protected String getTitle() {
        return "Pet Adoption System";
    }

    @Override
    protected String[] getOptions() {
        return new String[] {"Login", "Register", "Exit"};
    }

    @Override
    protected boolean handleChoice(int choice) throws PetAdoptionException {
        switch (choice) {
            case 1 -> loginFlow();
            case 2 -> registerFlow();
            case 3 -> {
                ConsoleUI.printInfo("Goodbye!");
                return false;
            }
            default -> ConsoleUI.printError("Unknown option.");
        }
        return true;
    }

    /** Up to 3 attempts in a row (ADR-027), then back to this menu. */
    private void loginFlow() throws ValidationException {
        ConsoleUI.printHeader("Login");
        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS; attempt++) {
            String identifier = InputHelper.readText("Username or email: ");
            String password = InputHelper.readText("Password: ");
            try {
                User user = authService.login(identifier, password);
                ConsoleUI.printSuccess("Welcome, " + user.getFullName() + "!");
                openHomeMenu(user);
                return;
            } catch (AuthenticationException e) {
                ConsoleUI.printError(e.getMessage());
                int attemptsLeft = MAX_LOGIN_ATTEMPTS - attempt;
                if (attemptsLeft > 0) {
                    ConsoleUI.printInfo("Attempts left: " + attemptsLeft);
                }
            }
        }
        ConsoleUI.printError("Too many failed attempts. Returning to the main menu.");
    }

    /** Polymorphism: both home menus are used through the same {@link Menu} type. */
    private void openHomeMenu(User user) {
        Menu home = (user.getRole() == Role.ADMIN) ? new AdminMenu() : new UserMenu();
        home.run();
    }

    /** Asks for each field until it is valid, then creates a Regular User account. */
    private void registerFlow() throws PetAdoptionException {
        ConsoleUI.printHeader("Register a new account");
        String firstName = InputHelper.readValidated("First name: ",
                input -> Validator.validateName("First name", input));
        String lastName = InputHelper.readValidated("Last name: ",
                input -> Validator.validateName("Last name", input));
        String email = InputHelper.readValidated("Email: ", input -> {
            String cleaned = Validator.validateEmail(input);
            if (authService.isEmailTaken(cleaned, null)) {
                throw new ValidationException("Email", "Email is already registered.");
            }
            return cleaned;
        });
        String contactNumber = InputHelper.readValidated("Contact number (e.g. 09171234567): ",
                Validator::normalizeContactNumber);
        String address = InputHelper.readValidated("Address: ", Validator::validateAddress);
        String username = InputHelper.readValidated("Username (4-20 letters, digits, or _): ", input -> {
            String cleaned = Validator.validateUsername(input);
            if (authService.isUsernameTaken(cleaned)) {
                throw new ValidationException("Username", "Username is already taken.");
            }
            return cleaned;
        });

        String password;
        while (true) {
            password = InputHelper.readValidated("Password (8-30 characters, letters and numbers): ",
                    input -> Validator.validatePassword(input, username));
            String confirmPassword = InputHelper.readText("Confirm password: ");
            if (password.equals(confirmPassword)) {
                break;
            }
            ConsoleUI.printError("Passwords do not match. Please enter the password again.");
        }

        User user = authService.register(firstName, lastName, email, contactNumber, address, username,
                password, password);
        ConsoleUI.printSuccess("Account created. Your user ID is " + user.getUserId() + ". You can now log in.");
    }
}
