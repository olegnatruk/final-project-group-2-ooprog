package petadoption.pet;

import java.util.ArrayList;
import java.util.List;

import petadoption.core.AllOfStrategy;
import petadoption.core.SearchStrategy;
import petadoption.exception.PetAdoptionException;
import petadoption.exception.ValidationException;
import petadoption.pet.search.PetAgeRangeFilter;
import petadoption.pet.search.PetBreedFilter;
import petadoption.pet.search.PetGenderFilter;
import petadoption.pet.search.PetKeywordSearch;
import petadoption.pet.search.PetStatusFilter;
import petadoption.pet.search.PetTypeFilter;
import petadoption.ui.Menu;
import petadoption.util.ConsoleUI;
import petadoption.util.InputHelper;

/**
 * Admin menu for managing pets: view all, passport, add, update, update status, delete, search/filter.
 */
public class PetAdminMenu extends Menu {

    private final PetService petService = new PetService();

    @Override
    protected String getTitle() {
        return "Manage Pets (Admin)";
    }

    @Override
    protected String[] getOptions() {
        return new String[] {
                "View All Pets",
                "View Pet Passport",
                "Add Pet",
                "Update Pet Information",
                "Update Pet Status",
                "Delete Pet",
                "Search & Filter Pets",
                "Back"
        };
    }

    @Override
    protected boolean handleChoice(int choice) throws PetAdoptionException {
        switch (choice) {
            case 1 -> viewAllPets();
            case 2 -> viewPetPassport();
            case 3 -> addPetFlow();
            case 4 -> updatePetFlow();
            case 5 -> updatePetStatusFlow();
            case 6 -> deletePetFlow();
            case 7 -> searchAndFilterFlow();
            case 8 -> {
                return false;
            }
            default -> ConsoleUI.printError("Unknown option.");
        }
        return true;
    }

    // ------------------------------------------------------------------ option handlers

    private void viewAllPets() {
        ConsoleUI.printHeader("ALL PETS");
        List<Pet> pets = petService.getAllPets();
        PetDisplay.printPetTable(pets);
    }

    private void viewPetPassport() throws PetAdoptionException {
        ConsoleUI.printHeader("VIEW PET PASSPORT");
        String petId = InputHelper.readId("Enter Pet ID: ", "P");
        Pet pet = petService.getPetById(petId);
        PetDisplay.printPassport(pet);
    }

    private void addPetFlow() throws PetAdoptionException {
        ConsoleUI.printHeader("ADD NEW PET");

        PetType type = InputHelper.readEnum("Select pet type:", PetType.values());
        String name = InputHelper.readValidated("Name: ", Pet::validateName);
        String breed = InputHelper.readValidated("Breed: ", Pet::validateBreed);

        int maxAge = Pet.maxAgeFor(type);
        int ageYears = InputHelper.readInt("Age in whole years (0-" + maxAge + ", 0 = under 1 yr): ", 0, maxAge);

        Gender gender = InputHelper.readEnum("Select gender:", Gender.values());
        String color = InputHelper.readValidated("Color: ", Pet::validateColor);

        double maxWeight = Pet.maxWeightFor(type);
        double weightKg;
        while (true) {
            weightKg = InputHelper.readDouble("Weight in kg (up to " + maxWeight + "): ", 0.01, maxWeight);
            try {
                weightKg = Pet.validateWeight(weightKg, maxWeight, type.getLabel());
                break;
            } catch (ValidationException e) {
                ConsoleUI.printError(e.getMessage());
            }
        }

        String description = InputHelper.readValidated("Description (10-300 chars): ", Pet::validateDescription);
        String healthInfo = InputHelper.readValidated("Health info (3-200 chars): ", Pet::validateHealthInfo);
        VaccinationStatus vaccinationStatus = InputHelper.readEnum("Select vaccination status:", VaccinationStatus.values());

        String typeSpecificValue;
        switch (type) {
            case DOG -> {
                DogSize size = InputHelper.readEnum("Select dog size:", DogSize.values());
                typeSpecificValue = size.name();
            }
            case CAT -> {
                boolean indoorOnly = InputHelper.readYesNo("Is the cat indoor-only?");
                typeSpecificValue = String.valueOf(indoorOnly);
            }
            case BIRD -> {
                boolean canFly = InputHelper.readYesNo("Can the bird fly?");
                typeSpecificValue = String.valueOf(canFly);
            }
            default -> throw new ValidationException("Type", "Unknown pet type.");
        }

        Pet created = petService.addPet(type, name, breed, ageYears, gender, color, weightKg,
                description, healthInfo, vaccinationStatus, typeSpecificValue);
        ConsoleUI.printSuccess("Pet " + created.getPetId() + " (" + created.getName() + ") added successfully.");
    }

    private void updatePetFlow() throws PetAdoptionException {
        ConsoleUI.printHeader("UPDATE PET INFORMATION");
        String petId = InputHelper.readId("Enter Pet ID to update: ", "P");
        Pet pet = petService.getPetById(petId);

        System.out.println("Updating " + pet.getName() + " (" + pet.getPetId() + ", " + pet.getType().getLabel() + ").");
        System.out.println("Press Enter on any field to keep its current value.");

        String name = InputHelper.readOptional("Name", pet.getName(), Pet::validateName);
        String breed = InputHelper.readOptional("Breed", pet.getBreed(), Pet::validateBreed);

        String ageStr = InputHelper.readOptional("Age (years)", String.valueOf(pet.getAgeYears()), input -> {
            try {
                int parsed = Integer.parseInt(input);
                Pet.validateAge(parsed, pet.getMaxAgeYears(), pet.getType().getLabel());
                return input;
            } catch (NumberFormatException e) {
                throw new ValidationException("Age", "Please enter a whole number.");
            }
        });
        int ageYears = Integer.parseInt(ageStr);

        Gender gender = readOptionalEnum("Gender", pet.getGender(), Gender.values());
        String color = InputHelper.readOptional("Color", pet.getColor(), Pet::validateColor);

        String weightStr = InputHelper.readOptional("Weight (kg)", String.valueOf(pet.getWeightKg()), input -> {
            try {
                double parsed = Double.parseDouble(input);
                Pet.validateWeight(parsed, pet.getMaxWeightKg(), pet.getType().getLabel());
                return input;
            } catch (NumberFormatException e) {
                throw new ValidationException("Weight", "Please enter a valid number.");
            }
        });
        double weightKg = Double.parseDouble(weightStr);

        String description = InputHelper.readOptional("Description", pet.getDescription(), Pet::validateDescription);
        String healthInfo = InputHelper.readOptional("Health info", pet.getHealthInfo(), Pet::validateHealthInfo);
        VaccinationStatus vaccinationStatus = readOptionalEnum("Vaccination status", pet.getVaccinationStatus(),
                VaccinationStatus.values());

        String typeSpecificValue;
        switch (pet.getType()) {
            case DOG -> {
                Dog dog = (Dog) pet;
                DogSize size = readOptionalEnum("Dog size", dog.getSize(), DogSize.values());
                typeSpecificValue = size.name();
            }
            case CAT -> {
                Cat cat = (Cat) pet;
                boolean indoorOnly = readOptionalBoolean("Indoor only", cat.isIndoorOnly());
                typeSpecificValue = String.valueOf(indoorOnly);
            }
            case BIRD -> {
                Bird bird = (Bird) pet;
                boolean canFly = readOptionalBoolean("Can fly", bird.canFly());
                typeSpecificValue = String.valueOf(canFly);
            }
            default -> typeSpecificValue = "";
        }

        if (!InputHelper.confirm("save updates to pet " + pet.getPetId())) {
            return;
        }

        petService.updatePet(petId, name, breed, ageYears, gender, color, weightKg,
                description, healthInfo, vaccinationStatus, typeSpecificValue);
        ConsoleUI.printSuccess("Pet " + petId + " updated successfully.");
    }

    private void updatePetStatusFlow() throws PetAdoptionException {
        ConsoleUI.printHeader("UPDATE PET STATUS");
        String petId = InputHelper.readId("Enter Pet ID: ", "P");
        Pet pet = petService.getPetById(petId);

        System.out.println("Pet: " + pet.getName() + " (" + pet.getPetId() + ")");
        System.out.println("Current status: " + pet.getStatus().getLabel());

        PetStatus newStatus = InputHelper.readEnum("Select new status:", PetStatus.values());
        if (!InputHelper.confirm("change status of pet " + petId + " to " + newStatus.getLabel())) {
            return;
        }

        petService.updateStatusManually(petId, newStatus);
        ConsoleUI.printSuccess("Pet " + petId + " status updated to " + newStatus.getLabel() + ".");
    }

    private void deletePetFlow() throws PetAdoptionException {
        ConsoleUI.printHeader("DELETE PET");
        String petId = InputHelper.readId("Enter Pet ID to delete: ", "P");
        Pet pet = petService.getPetById(petId);

        System.out.println("Pet to delete: " + pet.getName() + " (" + pet.getPetId() + ", "
                + pet.getType().getLabel() + ", status: " + pet.getStatus().getLabel() + ")");
        if (!InputHelper.confirm("delete pet " + petId + " (" + pet.getName() + ")")) {
            return;
        }

        petService.deletePet(petId);
        ConsoleUI.printSuccess("Pet " + petId + " deleted successfully.");
    }

    private void searchAndFilterFlow() {
        ConsoleUI.printHeader("SEARCH & FILTER PETS");
        System.out.println("  1. Keyword Search (name, breed, type)");
        System.out.println("  2. Filter by Criteria (type, breed, gender, age, status)");
        System.out.println("  3. Back");
        int choice = InputHelper.readMenuChoice(3);

        if (choice == 1) {
            String keyword = InputHelper.readText("Enter search keyword: ", 1, 50);
            List<Pet> results = petService.search(new PetKeywordSearch(keyword));
            if (results.isEmpty()) {
                ConsoleUI.printInfo("No pets match your search.");
            } else {
                PetDisplay.printPetTable(results);
            }
        } else if (choice == 2) {
            runFilterFlow();
        }
    }

    private void runFilterFlow() {
        List<SearchStrategy<Pet>> filters = new ArrayList<>();

        if (InputHelper.readYesNo("Filter by pet type?")) {
            PetType type = InputHelper.readEnum("Select pet type:", PetType.values());
            filters.add(new PetTypeFilter(type));
        }
        if (InputHelper.readYesNo("Filter by breed?")) {
            String breed = InputHelper.readText("Enter breed keyword: ", 1, 40);
            filters.add(new PetBreedFilter(breed));
        }
        if (InputHelper.readYesNo("Filter by gender?")) {
            Gender gender = InputHelper.readEnum("Select gender:", Gender.values());
            filters.add(new PetGenderFilter(gender));
        }
        if (InputHelper.readYesNo("Filter by age range?")) {
            int minAge = InputHelper.readInt("Minimum age in years (min 0): ", 0, 100);
            int maxAge = InputHelper.readInt("Maximum age in years (max 100): ", minAge, 100);
            filters.add(new PetAgeRangeFilter(minAge, maxAge));
        }
        if (InputHelper.readYesNo("Filter by status?")) {
            PetStatus status = InputHelper.readEnum("Select status:", PetStatus.values());
            filters.add(new PetStatusFilter(status));
        }

        if (filters.isEmpty()) {
            ConsoleUI.printInfo("No filters selected.");
            return;
        }

        SearchStrategy<Pet> combined = new AllOfStrategy<>(filters);
        List<Pet> results = petService.search(combined);
        if (results.isEmpty()) {
            ConsoleUI.printInfo("No pets match your filters.");
        } else {
            PetDisplay.printPetTable(results);
        }
    }

    // ------------------------------------------------------------------ input helpers

    private <E extends Enum<E>> E readOptionalEnum(String prompt, E currentValue, E[] values) {
        System.out.println(prompt + " [Current: " + currentValue + "] (Press Enter to keep, or choose new):");
        for (int i = 0; i < values.length; i++) {
            System.out.println("  " + (i + 1) + ". " + values[i]);
        }
        while (true) {
            String line = InputHelper.readOptional("Enter choice (1-" + values.length + ")", "", input -> input);
            if (line.isEmpty()) {
                return currentValue;
            }
            try {
                int choice = Integer.parseInt(line);
                if (choice >= 1 && choice <= values.length) {
                    return values[choice - 1];
                }
            } catch (NumberFormatException e) {
                // fall through
            }
            ConsoleUI.printError("Please enter a number from 1 to " + values.length + " or press Enter to keep current.");
        }
    }

    private boolean readOptionalBoolean(String prompt, boolean currentValue) {
        String currentLabel = currentValue ? "y" : "n";
        String answer = InputHelper.readOptional(prompt + " (y/n)", currentLabel, input -> {
            String lower = input.toLowerCase();
            if (lower.equals("y") || lower.equals("yes") || lower.equals("n") || lower.equals("no")) {
                return lower;
            }
            throw new ValidationException(prompt, "Please enter y or n.");
        });
        return answer.startsWith("y");
    }
}
