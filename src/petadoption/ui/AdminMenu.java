package petadoption.ui;

import petadoption.auth.AuthService;
import petadoption.auth.SessionManager;
import petadoption.util.ConsoleUI;

/**
 * Home menu for Admins. Each option opens a menu owned by another member. Until that menu is merged, the
 * option shows a "Coming soon" message; the owner replaces only their own line
 * (see docs/TEAM.md, Menu wiring).
 */
public class AdminMenu extends Menu {

    private final AuthService authService = new AuthService();

    @Override
    protected String getTitle() {
        return "Admin Menu - " + SessionManager.getInstance().getCurrentUser().getFullName();
    }

    @Override
    protected String[] getOptions() {
        return new String[] {"Manage Pets", "Manage Users", "Adoption Applications", "Adoption Records",
                "Adoption Statistics", "My Profile", "Logout"};
    }

    @Override
    protected boolean handleChoice(int choice) {
        switch (choice) {
            case 1 -> comingSoon("Manage Pets", "Julyan");               // new PetAdminMenu().run();
            case 2 -> comingSoon("Manage Users", "Derick");              // new UserManagementMenu().run();
            case 3 -> comingSoon("Adoption Applications", "Ace");        // new AdminApplicationMenu().run();
            case 4 -> comingSoon("Adoption Records", "Derick");          // new AdoptionRecordMenu().run();
            case 5 -> comingSoon("Adoption Statistics", "Derick");       // new StatisticsMenu().run();
            case 6 -> comingSoon("My Profile", "Derick");                // new ProfileMenu().run();
            case 7 -> {
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
