package petadoption;

import java.util.List;

import petadoption.auth.AuthService;
import petadoption.auth.WelcomeMenu;
import petadoption.core.DataStore;
import petadoption.exception.DataFileException;
import petadoption.exception.PetAdoptionException;
import petadoption.util.ConsoleUI;
import petadoption.util.InputHelper;

/**
 * Entry point. Loads the data files, makes sure an admin exists, and shows the welcome menu.
 *
 * <p>Run from the repository root so the {@code data/} folder is found:
 * {@code java -cp out petadoption.Main}
 */
public class Main {

    public static void main(String[] args) {
        try {
            List<String> warnings = DataStore.getInstance().load();
            for (String warning : warnings) {
                ConsoleUI.printInfo("Skipped bad data - " + warning);
            }
            if (new AuthService().ensureDefaultAdmin()) {
                ConsoleUI.printInfo("Default admin account created (username: admin).");
            }
            new WelcomeMenu().run();
        } catch (DataFileException e) {
            // Most specific first: the data files could not be read or written.
            ConsoleUI.printError(e.getMessage());
            ConsoleUI.printError("The program cannot run without its data files. Check the data folder.");
        } catch (PetAdoptionException e) {
            ConsoleUI.printError("Could not start: " + e.getMessage());
        } catch (Exception e) {
            // Safety net for anything unexpected: show a message instead of a stack trace.
            ConsoleUI.printError("Something went wrong: " + e.getMessage());
        } finally {
            InputHelper.close();
        }
    }
}
