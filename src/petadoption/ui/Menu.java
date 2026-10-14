package petadoption.ui;

import petadoption.exception.PetAdoptionException;
import petadoption.util.ConsoleUI;
import petadoption.util.InputHelper;

/**
 * Base class for every menu (Template Method). {@link #run()} owns the loop: print the title and numbered
 * options, read a valid choice, and hand it to {@link #handleChoice(int)}. Subclasses only say what their
 * title and options are and what each choice does.
 *
 * <p>Any {@link PetAdoptionException} thrown while handling a choice is caught here and printed, so one
 * failed action never crashes the program or leaves the menu.
 */
public abstract class Menu {

    public final void run() {
        boolean keepRunning = true;
        while (keepRunning) {
            ConsoleUI.printHeader(getTitle());
            String[] options = getOptions();
            for (int i = 0; i < options.length; i++) {
                System.out.println("  " + (i + 1) + ". " + options[i]);
            }
            int choice = InputHelper.readMenuChoice(options.length);
            try {
                keepRunning = handleChoice(choice);
            } catch (PetAdoptionException e) {
                ConsoleUI.printError(e.getMessage());
            }
        }
    }

    protected abstract String getTitle();

    /** The options, numbered from 1 when shown. The last option is always Back, Logout, or Exit. */
    protected abstract String[] getOptions();

    /**
     * Performs the chosen option.
     *
     * @return {@code false} to leave this menu, {@code true} to show it again
     */
    protected abstract boolean handleChoice(int choice) throws PetAdoptionException;
}
