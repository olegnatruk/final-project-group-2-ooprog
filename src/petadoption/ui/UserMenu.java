package petadoption.ui;

import petadoption.auth.AuthService;
import petadoption.auth.SessionManager;
import petadoption.util.ConsoleUI;

/**
 * Home menu for Regular Users. Each option opens a menu owned by another member. Until that menu is
 * merged, the option shows a "Coming soon" message; the owner replaces only their own line
 * (see docs/TEAM.md, Menu wiring).
 */
public class UserMenu extends Menu {

    private final AuthService authService = new AuthService();

    @Override
    protected String getTitle() {
        return "Regular User Menu - " + SessionManager.getInstance().getCurrentUser().getFullName();
    }

    @Override
    protected String[] getOptions() {
        return new String[] {"Browse Pets", "My Applications", "Adoption History", "My Profile", "Logout"};
    }

    @Override
    protected boolean handleChoice(int choice) {
        switch (choice) {
            case 1 -> comingSoon("Browse Pets", "Julyan");        // new PetBrowseMenu().run();
            case 2 -> comingSoon("My Applications", "Ace");       // new UserApplicationMenu().run();
            case 3 -> new petadoption.record.UserHistoryMenu().run();
            case 4 -> new petadoption.user.ProfileMenu().run();
            case 5 -> {
                authService.logout();
                ConsoleUI.printSuccess("You have been logged out.");
                return false;
            }
            default -> ConsoleUI.printError("Unknown option.");
        }
        return true;
    }

    private void comingSoon(String feature, String owner) {
        ConsoleUI.printInfo("Coming soon - " + feature + " (" + owner + ").");
    }
}
