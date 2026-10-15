package petadoption.record;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import petadoption.application.AdoptionApplication;
import petadoption.application.ApplicationService;
import petadoption.core.AllOfStrategy;
import petadoption.core.SearchStrategy;
import petadoption.exception.PetAdoptionException;
import petadoption.pet.Pet;
import petadoption.pet.PetService;
import petadoption.pet.PetType;
import petadoption.record.search.RecordAdopterSearch;
import petadoption.record.search.RecordDateRangeFilter;
import petadoption.record.search.RecordPetSearch;
import petadoption.record.search.RecordPetTypeFilter;
import petadoption.ui.Menu;
import petadoption.user.User;
import petadoption.user.UserService;
import petadoption.util.ConsoleUI;
import petadoption.util.InputHelper;
import petadoption.util.Validator;

/**
 * Admin adoption records menu:
 * 1 View adoption history · 2 View record details · 3 Create record from approved application ·
 * 4 Update record · 5 Delete record · 6 Search & filter records · 7 Back
 */
public class AdoptionRecordMenu extends Menu {

    private final RecordService recordService = new RecordService();
    private final UserService userService = new UserService();
    private final PetService petService = new PetService();
    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected String getTitle() {
        return "Manage Adoption Records";
    }

    @Override
    protected String[] getOptions() {
        return new String[] {
            "View adoption history",
            "View record details",
            "Create record from approved application",
            "Update record",
            "Delete record",
            "Search & filter records",
            "Back"
        };
    }

    @Override
    protected boolean handleChoice(int choice) throws PetAdoptionException {
        switch (choice) {
            case 1 -> viewAdoptionHistory();
            case 2 -> viewRecordDetails();
            case 3 -> createRecord();
            case 4 -> updateRecord();
            case 5 -> deleteRecord();
            case 6 -> searchAndFilterRecords();
            case 7 -> {
                return false;
            }
            default -> ConsoleUI.printError("Unknown option.");
        }
        return true;
    }

    private void viewAdoptionHistory() {
        List<AdoptionRecord> records = recordService.getAllRecords();
        printAdminRecordTable(records);
        InputHelper.pause();
    }

    private void viewRecordDetails() throws PetAdoptionException {
        String recordId = InputHelper.readId("Enter Record ID: ", "REC");
        AdoptionRecord record = recordService.getRecordById(recordId);
        printRecordDetails(record);
        InputHelper.pause();
    }

    private void createRecord() throws PetAdoptionException {
        ConsoleUI.printHeader("Create Adoption Record");

        List<AdoptionApplication> approvedApps;
        try {
            approvedApps = applicationService.getApprovedApplicationsWithoutRecord();
        } catch (UnsupportedOperationException e) {
            approvedApps = List.of();
        }

        if (!approvedApps.isEmpty()) {
            ConsoleUI.printInfo("Approved applications ready for record creation:");
            String[] headers = {"App ID", "Applicant", "Pet ID", "Date"};
            List<String[]> rows = new ArrayList<>();
            for (AdoptionApplication app : approvedApps) {
                rows.add(new String[] {
                    app.getApplicationId(),
                    app.getApplicantName(),
                    app.getPetId(),
                    app.getApplicationDate() != null ? app.getApplicationDate().toString() : ""
                });
            }
            ConsoleUI.printTable(headers, rows);
            ConsoleUI.printDivider();
        }

        String appId = InputHelper.readId("Application ID: ", "APP");
        LocalDate adoptionDate = InputHelper.readDate("Adoption date", LocalDate.now());
        String notes = InputHelper.readValidated("Notes (optional, max 200 chars): ",
                input -> {
                    if (input == null || input.isBlank()) {
                        return "";
                    }
                    Validator.requireNoDelimiter("Notes", input);
                    String trimmed = input.trim();
                    if (trimmed.length() > 200) {
                        throw new petadoption.exception.ValidationException("Notes", "Notes must be at most 200 characters.");
                    }
                    return trimmed;
                });

        if (!InputHelper.confirm("create this adoption record")) {
            return;
        }

        AdoptionRecord created = recordService.createRecord(appId, adoptionDate, notes);
        ConsoleUI.printSuccess("Adoption record " + created.getRecordId() + " created successfully. Pet marked as Adopted.");
    }

    private void updateRecord() throws PetAdoptionException {
        ConsoleUI.printHeader("Update Adoption Record");
        String recordId = InputHelper.readId("Enter Record ID: ", "REC");
        AdoptionRecord record = recordService.getRecordById(recordId);

        LocalDate date = InputHelper.readDate("Adoption date", record.getAdoptionDate());
        String notes = InputHelper.readOptional("Notes", record.getNotes(),
                input -> {
                    Validator.requireNoDelimiter("Notes", input);
                    String trimmed = input.trim();
                    if (trimmed.length() > 200) {
                        throw new petadoption.exception.ValidationException("Notes", "Notes must be at most 200 characters.");
                    }
                    return trimmed;
                });

        if (!InputHelper.confirm("save record changes")) {
            return;
        }

        recordService.updateRecord(recordId, date, notes);
        ConsoleUI.printSuccess("Record " + recordId + " updated successfully.");
    }

    private void deleteRecord() throws PetAdoptionException {
        ConsoleUI.printHeader("Delete Adoption Record");
        String recordId = InputHelper.readId("Enter Record ID: ", "REC");
        AdoptionRecord record = recordService.getRecordById(recordId);

        if (!InputHelper.confirm("delete record " + recordId + " (pet " + record.getPetId() + " will revert to Pending Adoption)")) {
            return;
        }

        recordService.deleteRecord(recordId);
        ConsoleUI.printSuccess("Adoption record " + recordId + " deleted. Pet status reverted to Pending Adoption.");
    }

    private void searchAndFilterRecords() throws PetAdoptionException {
        ConsoleUI.printHeader("Search & Filter Records");
        System.out.println("1. Search by adopter (name or username)");
        System.out.println("2. Search by pet (name or ID)");
        System.out.println("3. Filter by date range");
        System.out.println("4. Filter by pet type");
        System.out.println("5. Combined search & filter");
        System.out.println("6. Back");

        int choice = InputHelper.readMenuChoice(6);
        if (choice == 6) {
            return;
        }

        SearchStrategy<AdoptionRecord> strategy = null;
        switch (choice) {
            case 1 -> {
                String kw = InputHelper.readText("Adopter name or username: ", 1, 50);
                strategy = new RecordAdopterSearch(kw, userService);
            }
            case 2 -> {
                String kw = InputHelper.readText("Pet name or ID: ", 1, 50);
                strategy = new RecordPetSearch(kw, petService);
            }
            case 3 -> {
                LocalDate from = InputHelper.readDate("From date", null);
                LocalDate to = InputHelper.readDate("To date", null);
                strategy = new RecordDateRangeFilter(from, to);
            }
            case 4 -> {
                PetType type = InputHelper.readEnum("Select pet type:", PetType.values());
                strategy = new RecordPetTypeFilter(type, petService);
            }
            case 5 -> {
                List<SearchStrategy<AdoptionRecord>> filters = new ArrayList<>();
                if (InputHelper.readYesNo("Filter by adopter?")) {
                    String kw = InputHelper.readText("Adopter: ", 1, 50);
                    filters.add(new RecordAdopterSearch(kw, userService));
                }
                if (InputHelper.readYesNo("Filter by pet?")) {
                    String kw = InputHelper.readText("Pet: ", 1, 50);
                    filters.add(new RecordPetSearch(kw, petService));
                }
                if (InputHelper.readYesNo("Filter by date range?")) {
                    LocalDate from = InputHelper.readDate("From date", null);
                    LocalDate to = InputHelper.readDate("To date", null);
                    filters.add(new RecordDateRangeFilter(from, to));
                }
                if (InputHelper.readYesNo("Filter by pet type?")) {
                    PetType type = InputHelper.readEnum("Pet type:", PetType.values());
                    filters.add(new RecordPetTypeFilter(type, petService));
                }
                if (filters.isEmpty()) {
                    ConsoleUI.printInfo("No filters selected.");
                    return;
                }
                strategy = new AllOfStrategy<>(filters);
            }
        }

        if (strategy != null) {
            List<AdoptionRecord> results = recordService.search(strategy);
            printAdminRecordTable(results);
            InputHelper.pause();
        }
    }

    // ------------------------------------------------------------------ formatting

    private void printAdminRecordTable(List<AdoptionRecord> recordList) {
        String[] headers = {"Record ID", "Adopter", "Pet", "Application ID", "Adoption Date"};
        List<String[]> rows = new ArrayList<>();
        for (AdoptionRecord r : recordList) {
            String adopterName = r.getUserId();
            try {
                User u = userService.getUserById(r.getUserId());
                adopterName = u.getFullName();
            } catch (PetAdoptionException e) {
                // keep userId
            }

            String petName = r.getPetId();
            try {
                Pet p = petService.getPetById(r.getPetId());
                petName = p.getName() + " (" + r.getPetId() + ")";
            } catch (PetAdoptionException | UnsupportedOperationException e) {
                // keep petId
            }

            rows.add(new String[] {
                r.getRecordId(),
                adopterName,
                petName,
                r.getApplicationId(),
                r.getAdoptionDate().toString()
            });
        }
        ConsoleUI.printTable(headers, rows);
    }

    private void printRecordDetails(AdoptionRecord record) {
        ConsoleUI.printHeader("Adoption Record Details - " + record.getRecordId());
        ConsoleUI.printField("Record ID", record.getRecordId());
        ConsoleUI.printField("Application ID", record.getApplicationId());

        String adopterStr = record.getUserId();
        try {
            User u = userService.getUserById(record.getUserId());
            adopterStr = u.getFullName() + " (" + u.getUsername() + ", ID: " + u.getUserId() + ")";
        } catch (PetAdoptionException e) {
            // keep
        }
        ConsoleUI.printField("Adopter", adopterStr);

        String petStr = record.getPetId();
        try {
            Pet p = petService.getPetById(record.getPetId());
            petStr = p.getName() + " (" + p.getType().getLabel() + " - " + p.getBreed() + ", ID: " + p.getId() + ")";
        } catch (PetAdoptionException | UnsupportedOperationException e) {
            // keep
        }
        ConsoleUI.printField("Pet", petStr);
        ConsoleUI.printField("Adoption Date", record.getAdoptionDate().toString());
        ConsoleUI.printField("Notes", record.getNotes().isEmpty() ? "-" : record.getNotes());
        ConsoleUI.printDivider();
    }
}
