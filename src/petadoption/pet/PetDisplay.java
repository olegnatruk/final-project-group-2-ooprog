package petadoption.pet;

import java.util.ArrayList;
import java.util.List;

import petadoption.util.ConsoleUI;

/**
 * Shared printing utilities for pets so both {@link PetAdminMenu} and {@link PetBrowseMenu} display pets
 * consistently (aligned table and Pet Passport).
 */
public final class PetDisplay {

    /** Shown by every pet-reading option when there are no pets to read. */
    public static final String NO_PETS_MESSAGE = "No pets listed yet.";

    private PetDisplay() {
        // static utility class
    }

    /**
     * Prints {@link #NO_PETS_MESSAGE} and returns {@code true} when the current user has no pets to see,
     * so a menu option can stop before asking for any input.
     */
    public static boolean printIfNoPets(PetService petService) {
        if (petService.hasListedPets()) {
            return false;
        }
        ConsoleUI.printInfo(NO_PETS_MESSAGE);
        return true;
    }

    /** Prints a list of pets as an aligned table: ID, Name, Type, Breed, Age, Gender, Status. */
    public static void printPetTable(List<Pet> pets) {
        if (pets == null || pets.isEmpty()) {
            ConsoleUI.printInfo("No pets to display.");
            return;
        }
        String[] headers = {"ID", "Name", "Type", "Breed", "Age", "Gender", "Status"};
        List<String[]> rows = new ArrayList<>();
        for (Pet pet : pets) {
            String ageStr = pet.getAgeYears() == 0 ? "0 (< 1 yr)" : String.valueOf(pet.getAgeYears());
            rows.add(new String[] {
                    pet.getPetId(),
                    pet.getName(),
                    pet.getType().getLabel(),
                    pet.getBreed(),
                    ageStr,
                    pet.getGender().getLabel(),
                    pet.getStatus().getLabel()
            });
        }
        ConsoleUI.printTable(headers, rows);
    }

    /**
     * Prints the complete Pet Passport: every common field plus the type-specific details with readable
     * labels.
     */
    public static void printPassport(Pet pet) {
        if (pet == null) {
            ConsoleUI.printError("Pet not found.");
            return;
        }
        ConsoleUI.printHeader("PET PASSPORT - " + pet.getName().toUpperCase());
        ConsoleUI.printField("Pet ID", pet.getPetId());
        ConsoleUI.printField("Type", pet.getType().getLabel());
        ConsoleUI.printField("Name", pet.getName());
        ConsoleUI.printField("Breed", pet.getBreed());
        String ageDisplay = pet.getAgeYears() == 0 ? "0 (under 1 year)" : pet.getAgeYears() + " year" + (pet.getAgeYears() == 1 ? "" : "s");
        ConsoleUI.printField("Age", ageDisplay);
        ConsoleUI.printField("Gender", pet.getGender().getLabel());
        ConsoleUI.printField("Color", pet.getColor());
        ConsoleUI.printField("Weight", String.format("%.2f kg", pet.getWeightKg()));

        String details = pet.getTypeSpecificDetails();
        if (details != null && details.contains(":")) {
            String[] parts = details.split(":\\s*", 2);
            ConsoleUI.printField(parts[0], parts[1]);
        } else if (details != null) {
            ConsoleUI.printField("Details", details);
        }

        ConsoleUI.printField("Vaccination", pet.getVaccinationStatus().getLabel());
        ConsoleUI.printField("Status", pet.getStatus().getLabel());
        ConsoleUI.printField("Date Added", pet.getDateAdded().toString());
        ConsoleUI.printField("Health Info", pet.getHealthInfo());
        ConsoleUI.printField("Description", pet.getDescription());
        ConsoleUI.printDivider();
    }
}
