package petadoption.application;

import java.util.ArrayList;
import java.util.List;

import petadoption.exception.RecordNotFoundException;
import petadoption.pet.Pet;
import petadoption.pet.PetService;
import petadoption.util.ConsoleUI;

/**
 * Static utility for displaying application tables and detailed records.
 */
public final class ApplicationDisplay {

    /** Shown by every My Applications option (except Apply) when the user has no applications. */
    public static final String NO_MY_APPLICATIONS_MESSAGE = "No applications applied yet.";

    /** Shown by every admin application option (except Back) when nobody has applied yet. */
    public static final String NO_APPLICATIONS_MESSAGE = "No applications submitted yet.";

    private ApplicationDisplay() {
        // static utility class
    }

    public static void printApplicationTable(List<AdoptionApplication> applications) {
        if (applications == null || applications.isEmpty()) {
            ConsoleUI.printInfo("No applications found.");
            return;
        }

        PetService petService = new PetService();
        String[] headers = {"Application ID", "Applicant", "Pet", "Date", "Status"};
        List<String[]> rows = new ArrayList<>();

        for (AdoptionApplication app : applications) {
            String petDisplay = app.getPetId();
            try {
                Pet pet = petService.getPetById(app.getPetId());
                if (pet != null && pet.getName() != null) {
                    petDisplay = pet.getName() + " (" + app.getPetId() + ")";
                }
            } catch (RecordNotFoundException e) {
                // pet was deleted: show the pet ID only
            }

            rows.add(new String[] {
                app.getApplicationId(),
                app.getApplicantName(),
                petDisplay,
                app.getApplicationDate().toString(),
                app.getStatus().getLabel()
            });
        }

        ConsoleUI.printTable(headers, rows);
    }

    public static void printDetails(AdoptionApplication app) {
        if (app == null) {
            ConsoleUI.printInfo("No application details to display.");
            return;
        }

        PetService petService = new PetService();
        String petDisplay = app.getPetId();
        try {
            Pet pet = petService.getPetById(app.getPetId());
            if (pet != null && pet.getName() != null) {
                petDisplay = pet.getName() + " (" + app.getPetId() + ")";
            }
        } catch (RecordNotFoundException e) {
            // pet was deleted: show the pet ID only
        }

        ConsoleUI.printHeader("Application Details - " + app.getApplicationId());
        ConsoleUI.printField("Application ID", app.getApplicationId());
        ConsoleUI.printField("Applicant Name", app.getApplicantName());
        ConsoleUI.printField("User ID", app.getUserId());
        ConsoleUI.printField("Pet", petDisplay);
        ConsoleUI.printField("Address", app.getAddress());
        ConsoleUI.printField("Contact Number", app.getContactNumber());
        ConsoleUI.printField("Occupation", app.getOccupation());
        ConsoleUI.printField("Living Situation", app.getLivingSituation().getLabel());
        ConsoleUI.printField("Previous Pet Experience", app.hasPreviousPets() ? "Yes" : "No");
        if (app.hasPreviousPets()) {
            ConsoleUI.printField("Experience Details", app.getExperienceDetails());
        }
        ConsoleUI.printField("Reason for Adoption", app.getReason());
        ConsoleUI.printField("Application Date", app.getApplicationDate().toString());
        ConsoleUI.printField("Status", app.getStatus().getLabel());
        if (app.getDecisionDate() != null) {
            ConsoleUI.printField("Decision Date", app.getDecisionDate().toString());
        }
        if (app.getDecisionReason() != null && !app.getDecisionReason().isBlank()) {
            ConsoleUI.printField("Decision Reason", app.getDecisionReason());
        }
        ConsoleUI.printDivider();
    }
}
