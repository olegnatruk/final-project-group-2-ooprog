package petadoption.application;

import java.util.List;

import petadoption.exception.PetAdoptionException;
import petadoption.pet.PetDisplay;
import petadoption.pet.PetService;
import petadoption.ui.Menu;
import petadoption.util.ConsoleUI;
import petadoption.util.InputHelper;

/**
 * Regular User menu for viewing and managing own adoption applications.
 */
public class UserApplicationMenu extends Menu {

    private final ApplicationService applicationService = new ApplicationService();
    private final PetService petService = new PetService();

    @Override
    protected String getTitle() {
        return "My Adoption Applications";
    }

    @Override
    protected String[] getOptions() {
        return new String[] {
            "Apply for a pet",
            "View my applications",
            "View application details",
            "Update pending application",
            "Cancel pending application",
            "Back"
        };
    }

    @Override
    protected boolean handleChoice(int choice) throws PetAdoptionException {
        // Options 2-5 all read the user's own applications.
        if (choice >= 2 && choice <= 5 && applicationService.getMyApplications().isEmpty()) {
            ConsoleUI.printInfo(ApplicationDisplay.NO_MY_APPLICATIONS_MESSAGE);
            return true;
        }
        switch (choice) {
            case 1 -> {
                if (PetDisplay.printIfNoPets(petService)) {
                    return true;
                }
                String petId = InputHelper.readId("Enter Pet ID to apply for (e.g. P0001): ", "P");
                new ApplicationFormScreen().submitFor(petId);
            }
            case 2 -> {
                List<AdoptionApplication> list = applicationService.getMyApplications();
                ApplicationDisplay.printApplicationTable(list);
                InputHelper.pause();
            }
            case 3 -> {
                String id = InputHelper.readId("Enter Application ID (e.g. APP0001): ", "APP");
                AdoptionApplication app = applicationService.getMyApplicationById(id);
                ApplicationDisplay.printDetails(app);
                InputHelper.pause();
            }
            case 4 -> {
                String id = InputHelper.readId("Enter Application ID to update (e.g. APP0001): ", "APP");
                new ApplicationFormScreen().editPending(id);
            }
            case 5 -> {
                String id = InputHelper.readId("Enter Application ID to cancel (e.g. APP0001): ", "APP");
                AdoptionApplication app = applicationService.getMyApplicationById(id);
                if (!app.isPending()) {
                    ConsoleUI.printError("Cannot cancel an application with status " + app.getStatus().getLabel()
                            + ". Only pending applications can be cancelled.");
                    return true;
                }
                if (InputHelper.confirm("cancel application " + id)) {
                    applicationService.cancelMyApplication(id);
                    ConsoleUI.printSuccess("Application " + id + " has been cancelled.");
                }
            }
            case 6 -> {
                return false;
            }
            default -> ConsoleUI.printError("Unknown option.");
        }
        return true;
    }
}
