package petadoption.application;

import petadoption.auth.SessionManager;
import petadoption.exception.PetAdoptionException;
import petadoption.pet.Pet;
import petadoption.pet.PetService;
import petadoption.user.User;
import petadoption.util.ConsoleUI;
import petadoption.util.InputHelper;
import petadoption.util.Validator;

/**
 * Interactive form screen for submitting new adoption applications and updating existing pending applications.
 * Not a Menu subclass; handles input collection, pre-filling, validation, and submission confirmation.
 */
public class ApplicationFormScreen {

    private final ApplicationService applicationService = new ApplicationService();
    private final PetService petService = new PetService();

    /**
     * Shows pet summary, collects application form input, confirms, and submits the application.
     */
    public void submitFor(String petId) {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !currentUser.canApplyForAdoption()) {
            ConsoleUI.printError("Only regular users can submit adoption applications.");
            return;
        }

        String targetPetId = petId;
        if (targetPetId == null || targetPetId.isBlank()) {
            targetPetId = InputHelper.readId("Enter Pet ID (e.g. P0001): ", "P");
        } else {
            targetPetId = targetPetId.trim().toUpperCase();
        }

        try {
            targetPetId = applicationService.checkCanApply(targetPetId);
            Pet pet = petService.getVisiblePetById(targetPetId);
            ConsoleUI.printInfo("Applying for pet: " + pet.getName() + " (" + pet.getId() + ")");
        } catch (PetAdoptionException e) {
            ConsoleUI.printError(e.getMessage());
            return;
        }

        ConsoleUI.printHeader("Adoption Application Form");
        ConsoleUI.printInfo("Applicant: " + currentUser.getFullName() + " (" + currentUser.getUserId() + ")");

        String defaultAddress = currentUser.getAddress();
        String defaultContact = currentUser.getContactNumber();

        try {
            String address = InputHelper.readOptional("Address",
                    defaultAddress, Validator::validateAddress);
            String contactNumber = InputHelper.readOptional("Contact number",
                    defaultContact, Validator::normalizeContactNumber);
            String occupation = InputHelper.readValidated("Occupation: ",
                    AdoptionApplication::validateOccupation);
            LivingSituation livingSituation = InputHelper.readEnum("Living Situation:",
                    LivingSituation.values());
            boolean hasPreviousPets = InputHelper.readYesNo("Do you have previous pet experience?");
            String experienceDetails = "";
            if (hasPreviousPets) {
                experienceDetails = InputHelper.readValidated("Experience details (10-300 characters): ",
                        input -> Validator.requireLength("Experience details", input, 10, 300));
            }
            String reason = InputHelper.readValidated("Reason for adoption (20-500 characters): ",
                    input -> Validator.requireLength("Reason for adoption", input, 20, 500));

            if (!InputHelper.confirm("submit this application")) {
                return;
            }

            AdoptionApplication app = applicationService.submitApplication(targetPetId, address, contactNumber,
                    occupation, livingSituation, hasPreviousPets, experienceDetails, reason);

            ConsoleUI.printSuccess("Application " + app.getApplicationId() + " submitted successfully. Status: "
                    + app.getStatus().getLabel());
        } catch (PetAdoptionException e) {
            ConsoleUI.printError(e.getMessage());
        }
    }

    /**
     * Displays current values of a pending application and allows editing fields, pressing Enter to keep existing.
     */
    public void editPending(String applicationId) {
        String targetAppId = applicationId;
        if (targetAppId == null || targetAppId.isBlank()) {
            targetAppId = InputHelper.readId("Enter Application ID to update (e.g. APP0001): ", "APP");
        } else {
            targetAppId = targetAppId.trim().toUpperCase();
        }

        try {
            AdoptionApplication app = applicationService.getMyApplicationById(targetAppId);
            if (!app.isPending()) {
                ConsoleUI.printError("Only pending applications can be updated.");
                return;
            }

            ConsoleUI.printHeader("Update Application - " + app.getApplicationId());
            ConsoleUI.printInfo("Press Enter to keep current value.");

            String address = InputHelper.readOptional("Address",
                    app.getAddress(), Validator::validateAddress);
            String contactNumber = InputHelper.readOptional("Contact number",
                    app.getContactNumber(), Validator::normalizeContactNumber);
            String occupation = InputHelper.readOptional("Occupation",
                    app.getOccupation(), AdoptionApplication::validateOccupation);

            LivingSituation livingSituation = app.getLivingSituation();
            if (InputHelper.readYesNo("Change living situation from " + livingSituation.getLabel() + "?")) {
                livingSituation = InputHelper.readEnum("Living Situation:", LivingSituation.values());
            }

            boolean hasPreviousPets = app.hasPreviousPets();
            String experienceDetails = app.getExperienceDetails();
            if (InputHelper.readYesNo("Change pet experience info (current: "
                    + (hasPreviousPets ? "Yes" : "No") + ")?")) {
                hasPreviousPets = InputHelper.readYesNo("Do you have previous pet experience?");
                if (hasPreviousPets) {
                    experienceDetails = InputHelper.readValidated("Experience details (10-300 characters): ",
                            input -> Validator.requireLength("Experience details", input, 10, 300));
                } else {
                    experienceDetails = "";
                }
            }

            String reason = InputHelper.readOptional("Reason for adoption",
                    app.getReason(), input -> Validator.requireLength("Reason for adoption", input, 20, 500));

            if (!InputHelper.confirm("save changes to application " + app.getApplicationId())) {
                return;
            }

            applicationService.updateMyApplication(app.getApplicationId(), address, contactNumber,
                    occupation, livingSituation, hasPreviousPets, experienceDetails, reason);

            ConsoleUI.printSuccess("Application " + app.getApplicationId() + " updated successfully.");
        } catch (PetAdoptionException e) {
            ConsoleUI.printError(e.getMessage());
        }
    }
}
