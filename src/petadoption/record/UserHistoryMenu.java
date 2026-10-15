package petadoption.record;

import java.util.ArrayList;
import java.util.List;

import petadoption.exception.PetAdoptionException;
import petadoption.pet.Pet;
import petadoption.pet.PetService;
import petadoption.ui.Menu;
import petadoption.util.ConsoleUI;
import petadoption.util.InputHelper;

/**
 * Regular User menu for viewing own adoption history:
 * 1 View my adoption history · 2 View record details · 3 Back
 */
public class UserHistoryMenu extends Menu {

    private final RecordService recordService = new RecordService();
    private final PetService petService = new PetService();

    @Override
    protected String getTitle() {
        return "My Adoption History";
    }

    @Override
    protected String[] getOptions() {
        return new String[] {
            "View my adoption history",
            "View record details",
            "Back"
        };
    }

    @Override
    protected boolean handleChoice(int choice) throws PetAdoptionException {
        switch (choice) {
            case 1 -> viewMyHistory();
            case 2 -> viewRecordDetails();
            case 3 -> {
                return false;
            }
            default -> ConsoleUI.printError("Unknown option.");
        }
        return true;
    }

    private void viewMyHistory() throws PetAdoptionException {
        List<AdoptionRecord> records = recordService.getMyRecords();
        String[] headers = {"Record ID", "Pet Name", "Type", "Breed", "Adoption Date", "Application ID"};
        List<String[]> rows = new ArrayList<>();
        for (AdoptionRecord r : records) {
            String petName = r.getPetId();
            String petType = "-";
            String breed = "-";
            try {
                Pet pet = petService.getPetById(r.getPetId());
                petName = pet.getName();
                petType = pet.getType().getLabel();
                breed = pet.getBreed();
            } catch (PetAdoptionException | UnsupportedOperationException e) {
                // keep defaults
            }

            rows.add(new String[] {
                r.getRecordId(),
                petName,
                petType,
                breed,
                r.getAdoptionDate().toString(),
                r.getApplicationId()
            });
        }
        ConsoleUI.printTable(headers, rows);
        InputHelper.pause();
    }

    private void viewRecordDetails() throws PetAdoptionException {
        String recordId = InputHelper.readId("Enter Record ID: ", "REC");
        AdoptionRecord record = recordService.getRecordById(recordId);

        // Security check: users can only view their own record details
        List<AdoptionRecord> myRecords = recordService.getMyRecords();
        boolean isMine = false;
        for (AdoptionRecord r : myRecords) {
            if (r.getRecordId().equalsIgnoreCase(record.getRecordId())) {
                isMine = true;
                break;
            }
        }
        if (!isMine) {
            throw new petadoption.exception.RecordNotFoundException("Adoption record " + recordId + " not found.");
        }

        ConsoleUI.printHeader("Adoption Record Details - " + record.getRecordId());
        ConsoleUI.printField("Record ID", record.getRecordId());
        ConsoleUI.printField("Application ID", record.getApplicationId());

        String petStr = record.getPetId();
        try {
            Pet p = petService.getPetById(record.getPetId());
            petStr = p.getName() + " (" + p.getType().getLabel() + " - " + p.getBreed() + ", ID: " + p.getId() + ")";
        } catch (PetAdoptionException | UnsupportedOperationException e) {
            // keep
        }
        ConsoleUI.printField("Adopted Pet", petStr);
        ConsoleUI.printField("Adoption Date", record.getAdoptionDate().toString());
        ConsoleUI.printField("Notes", record.getNotes().isEmpty() ? "-" : record.getNotes());
        ConsoleUI.printDivider();
        InputHelper.pause();
    }
}
