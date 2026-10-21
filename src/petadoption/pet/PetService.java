package petadoption.pet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import petadoption.application.ApplicationService;
import petadoption.auth.SessionManager;
import petadoption.core.DataStore;
import petadoption.core.RecordFinder;
import petadoption.core.Repository;
import petadoption.core.SearchStrategy;
import petadoption.exception.InvalidStatusTransitionException;
import petadoption.exception.PetAdoptionException;
import petadoption.exception.RecordNotFoundException;
import petadoption.exception.ValidationException;
import petadoption.record.RecordService;
import petadoption.user.Role;
import petadoption.user.User;

/**
 * Service managing pets: reading, searching, admin CRUD, manual status updates, and the workflow
 * transitions called by other members.
 *
 * <p>Strategy context: every search goes through {@link #finder}, setting the strategy at runtime (ADR-032).
 */
public class PetService {

    private final Repository<Pet> pets = DataStore.getInstance().pets();
    private final RecordFinder<Pet> finder = new RecordFinder<>();

    // ------------------------------------------------------------------ reading

    /**
     * Finds any pet by ID, regardless of status. Admins and other services use this. Contract method.
     */
    public Pet getPetById(String petId) throws RecordNotFoundException {
        return pets.getById(petId);
    }

    /**
     * Finds a pet by ID for display to Regular Users. Adopted pets are hidden from Regular Users (ADR-020)
     * and report as not found so their existence is not revealed.
     */
    public Pet getVisiblePetById(String petId) throws RecordNotFoundException {
        Pet pet = getPetById(petId);
        if (pet.getStatus() == PetStatus.ADOPTED && !isAdminLoggedIn()) {
            throw new RecordNotFoundException("Pet " + petId + " not found.");
        }
        return pet;
    }

    /** Every pet in the shelter, including adopted ones (admin view). Contract method. */
    public List<Pet> getAllPets() {
        return pets.findAll();
    }

    /**
     * Pets visible to Regular Users (status is AVAILABLE or PENDING_ADOPTION; ADR-020). Contract method.
     */
    public List<Pet> getVisiblePets() {
        List<Pet> visible = new ArrayList<>();
        for (Pet pet : pets.findAll()) {
            if (pet.getStatus() != PetStatus.ADOPTED) {
                visible.add(pet);
            }
        }
        return visible;
    }

    /**
     * Whether the current user has any pet to see: any pet for Admins, any visible pet for Regular Users.
     * Menus check this before every pet-reading option so an empty shelter shows one clear message.
     */
    public boolean hasListedPets() {
        return isAdminLoggedIn() ? !getAllPets().isEmpty() : !getVisiblePets().isEmpty();
    }

    /**
     * Searches pets using the given strategy. Swaps the strategy on {@link #finder} at runtime (ADR-032).
     * Regular users only search among visible pets; admins search all pets. Contract method.
     */
    public List<Pet> search(SearchStrategy<Pet> strategy) {
        finder.setStrategy(strategy);
        List<Pet> pool = isAdminLoggedIn() ? getAllPets() : getVisiblePets();
        return finder.find(pool);
    }

    // ------------------------------------------------------------------ admin CRUD

    /**
     * Adds a new pet to the system. New pets always start with status {@link PetStatus#AVAILABLE} (ADR-014).
     */
    public Pet addPet(PetType type, String name, String breed, int ageYears, Gender gender, String color,
                      double weightKg, String description, String healthInfo,
                      VaccinationStatus vaccinationStatus, String typeSpecificValue)
            throws PetAdoptionException {
        SessionManager.getInstance().requireAdmin();
        String id = pets.nextId();
        Pet pet = Pet.create(type, id, name, breed, ageYears, gender, color, weightKg, description,
                healthInfo, vaccinationStatus, PetStatus.AVAILABLE, LocalDate.now(), typeSpecificValue);
        pets.add(pet);
        pets.save();
        return pet;
    }

    /**
     * Updates an existing pet's editable information. Type cannot be changed (ADR-018).
     */
    public void updatePet(String petId, String name, String breed, int ageYears, Gender gender, String color,
                          double weightKg, String description, String healthInfo,
                          VaccinationStatus vaccinationStatus, String typeSpecificValue)
            throws PetAdoptionException {
        SessionManager.getInstance().requireAdmin();
        Pet pet = getPetById(petId);

        // Validates all new values atomically before modifying the in-memory object:
        Pet.create(pet.getType(), pet.getPetId(), name, breed, ageYears, gender, color, weightKg,
                description, healthInfo, vaccinationStatus, pet.getStatus(), pet.getDateAdded(),
                typeSpecificValue);

        pet.setName(name);
        pet.setBreed(breed);
        pet.setAgeYears(ageYears);
        pet.setGender(gender);
        pet.setColor(color);
        pet.setWeightKg(weightKg);
        pet.setDescription(description);
        pet.setHealthInfo(healthInfo);
        pet.setVaccinationStatus(vaccinationStatus);
        pet.setTypeSpecificValue(typeSpecificValue);

        pets.save();
    }

    /**
     * Deletes a pet. Blocked if the pet has any application (any status) or adoption record (ADR-022).
     */
    public void deletePet(String petId) throws PetAdoptionException {
        checkCanDelete(petId);
        pets.remove(petId);
        pets.save();
    }

    /**
     * Throws if the pet cannot be deleted (ADR-022), so the menu can stop before asking for confirmation.
     * {@link #deletePet} runs the same check again.
     */
    public void checkCanDelete(String petId) throws PetAdoptionException {
        SessionManager.getInstance().requireAdmin();
        Pet pet = getPetById(petId);

        // Created here, not as a field: ApplicationService already holds a PetService.
        if (new ApplicationService().hasAnyApplicationForPet(pet.getPetId())
                || new RecordService().hasRecordForPet(pet.getPetId())) {
            throw new InvalidStatusTransitionException("Pet has adoption history. Update its status instead.");
        }
    }

    /**
     * Admin manual status update. Consistency-checked against application and record data (ADR-014 table).
     */
    public void updateStatusManually(String petId, PetStatus newStatus) throws PetAdoptionException {
        checkStatusChange(petId, newStatus);
        getPetById(petId).setStatus(newStatus);
        pets.save();
    }

    /**
     * Throws if an admin may not set this status by hand (ADR-014 table), so the menu can stop before asking
     * for confirmation. {@link #updateStatusManually} runs the same check again.
     */
    public void checkStatusChange(String petId, PetStatus newStatus) throws PetAdoptionException {
        SessionManager.getInstance().requireAdmin();
        Pet pet = getPetById(petId);

        if (newStatus == null) {
            throw new ValidationException("Status", "Pet status is required.");
        }
        if (pet.getStatus() == newStatus) {
            throw new ValidationException("Status", "Pet is already " + newStatus.getLabel() + ".");
        }

        boolean hasApprovedApp = new ApplicationService().hasApprovedApplicationForPet(petId);
        boolean hasRecord = new RecordService().hasRecordForPet(petId);

        switch (newStatus) {
            case AVAILABLE -> {
                if (hasApprovedApp || hasRecord) {
                    throw new InvalidStatusTransitionException(
                            "Cannot set status to Available while an approved application or adoption record exists. Revoke the application approval first.");
                }
            }
            case PENDING_ADOPTION -> {
                if (!hasApprovedApp) {
                    throw new InvalidStatusTransitionException(
                            "Cannot set status to Pending Adoption manually. Approve an application to move this pet to Pending Adoption.");
                }
                if (hasRecord) {
                    throw new InvalidStatusTransitionException(
                            "Cannot set status to Pending Adoption while an adoption record exists. Delete the adoption record instead.");
                }
            }
            case ADOPTED -> {
                if (!hasRecord) {
                    throw new InvalidStatusTransitionException(
                            "Cannot set status to Adopted manually. Create an adoption record to mark this pet as Adopted.");
                }
            }
        }
    }

    // ------------------------------------------------------------------ workflow transitions (Ace, Derick)

    /** AVAILABLE -> PENDING_ADOPTION, when an application is approved. Contract method. */
    public void markPendingAdoption(String petId) throws PetAdoptionException {
        Pet pet = getPetById(petId);
        if (pet.getStatus() != PetStatus.AVAILABLE) {
            throw new InvalidStatusTransitionException(
                    "Cannot mark pet as Pending Adoption: current status is " + pet.getStatus().getLabel()
                            + " (expected Available).");
        }
        pet.setStatus(PetStatus.PENDING_ADOPTION);
        pets.save();
    }

    /** PENDING_ADOPTION -> AVAILABLE, when application approval is revoked. Contract method. */
    public void markAvailable(String petId) throws PetAdoptionException {
        Pet pet = getPetById(petId);
        if (pet.getStatus() != PetStatus.PENDING_ADOPTION) {
            throw new InvalidStatusTransitionException(
                    "Cannot mark pet as Available: current status is " + pet.getStatus().getLabel()
                            + " (expected Pending Adoption).");
        }
        pet.setStatus(PetStatus.AVAILABLE);
        pets.save();
    }

    /** PENDING_ADOPTION -> ADOPTED, when an adoption record is created. Contract method. */
    public void markAdopted(String petId) throws PetAdoptionException {
        Pet pet = getPetById(petId);
        if (pet.getStatus() != PetStatus.PENDING_ADOPTION) {
            throw new InvalidStatusTransitionException(
                    "Cannot mark pet as Adopted: current status is " + pet.getStatus().getLabel()
                            + " (expected Pending Adoption).");
        }
        pet.setStatus(PetStatus.ADOPTED);
        pets.save();
    }

    /** ADOPTED -> PENDING_ADOPTION, when an adoption record is deleted. Contract method. */
    public void revertToPendingAdoption(String petId) throws PetAdoptionException {
        Pet pet = getPetById(petId);
        if (pet.getStatus() != PetStatus.ADOPTED) {
            throw new InvalidStatusTransitionException(
                    "Cannot revert pet to Pending Adoption: current status is " + pet.getStatus().getLabel()
                            + " (expected Adopted).");
        }
        pet.setStatus(PetStatus.PENDING_ADOPTION);
        pets.save();
    }

    // ------------------------------------------------------------------ helpers

    private boolean isAdminLoggedIn() {
        User user = SessionManager.getInstance().getCurrentUser();
        return user != null && user.getRole() == Role.ADMIN;
    }
}
