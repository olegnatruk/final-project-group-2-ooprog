package petadoption.pet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import petadoption.auth.SessionManager;
import petadoption.core.AllOfStrategy;
import petadoption.core.SearchStrategy;
import petadoption.exception.PetAdoptionException;
import petadoption.pet.search.PetAgeRangeFilter;
import petadoption.pet.search.PetBreedFilter;
import petadoption.pet.search.PetGenderFilter;
import petadoption.pet.search.PetKeywordSearch;
import petadoption.pet.search.PetStatusFilter;
import petadoption.pet.search.PetTypeFilter;
import petadoption.ui.Menu;
import petadoption.user.Role;
import petadoption.user.User;
import petadoption.util.ConsoleUI;
import petadoption.util.InputHelper;

/**
 * Menu for browsing pets, keyword search, filtering, and viewing the Pet Passport. Available to both
 * Regular Users and Admins (Regular Users see Available and Pending Adoption pets; Admins see all).
 */
public class PetBrowseMenu extends Menu {

    private final PetService petService = new PetService();

    @Override
    protected String getTitle() {
        return "Browse Pets";
    }

    @Override
    protected String[] getOptions() {
        return new String[] {
                "View Pets",
                "Search by Keyword",
                "Filter Pets",
                "View Pet Passport",
                "Back"
        };
    }

    @Override
    protected boolean handleChoice(int choice) throws PetAdoptionException {
        switch (choice) {
            case 1 -> viewPets();
            case 2 -> searchByKeyword();
            case 3 -> filterPets();
            case 4 -> viewPetPassport();
            case 5 -> {
                return false;
            }
            default -> ConsoleUI.printError("Unknown option.");
        }
        return true;
    }

    // ------------------------------------------------------------------ option handlers

    private void viewPets() {
        ConsoleUI.printHeader("AVAILABLE PETS");
        List<Pet> pets = isAdmin() ? petService.getAllPets() : petService.getVisiblePets();
        PetDisplay.printPetTable(pets);
    }

    private void searchByKeyword() {
        ConsoleUI.printHeader("SEARCH PETS BY KEYWORD");
        String keyword = InputHelper.readText("Enter search keyword (name, breed, or type): ", 1, 50);
        List<Pet> results = petService.search(new PetKeywordSearch(keyword));
        if (results.isEmpty()) {
            ConsoleUI.printInfo("No pets match your search.");
        } else {
            PetDisplay.printPetTable(results);
        }
    }

    private void filterPets() {
        ConsoleUI.printHeader("FILTER PETS");
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
            PetStatus[] availableStatuses = isAdmin()
                    ? PetStatus.values()
                    : new PetStatus[] {PetStatus.AVAILABLE, PetStatus.PENDING_ADOPTION};
            PetStatus status = InputHelper.readEnum("Select status:", availableStatuses);
            filters.add(new PetStatusFilter(status));
        }

        if (filters.isEmpty()) {
            ConsoleUI.printInfo("No filters selected.");
            return;
        }

        SearchStrategy<Pet> combined = new AllOfStrategy<>(filters);
        List<Pet> results = petService.search(combined);
        if (results.isEmpty()) {
            ConsoleUI.printInfo("No pets match your search.");
        } else {
            PetDisplay.printPetTable(results);
        }
    }

    private void viewPetPassport() throws PetAdoptionException {
        ConsoleUI.printHeader("VIEW PET PASSPORT");
        String petId = InputHelper.readId("Enter Pet ID: ", "P");
        Pet pet = petService.getVisiblePetById(petId);
        PetDisplay.printPassport(pet);

        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getRole() == Role.REGULAR_USER && pet.isAvailable()) {
            if (InputHelper.readYesNo("Apply for this pet?")) {
                openApplicationForm(pet.getPetId());
            }
        }
    }

    // ------------------------------------------------------------------ helpers

    private boolean isAdmin() {
        User user = SessionManager.getInstance().getCurrentUser();
        return user != null && user.getRole() == Role.ADMIN;
    }

    /**
     * Calls Ace's ApplicationFormScreen.submitFor(petId). If Ace's code is not yet merged, shows a
     * friendly notification.
     */
    private void openApplicationForm(String petId) {
        try {
            Class<?> clazz = Class.forName("petadoption.application.ApplicationFormScreen");
            Object form = clazz.getConstructor().newInstance();
            Method method = clazz.getMethod("submitFor", String.class);
            method.invoke(form, petId);
        } catch (ClassNotFoundException e) {
            ConsoleUI.printInfo("Coming soon - Adoption Applications (Ace).");
        } catch (Exception e) {
            ConsoleUI.printError("Could not open application form: " + e.getMessage());
        }
    }
}
