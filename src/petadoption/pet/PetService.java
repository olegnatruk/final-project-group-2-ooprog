package petadoption.pet;

import java.util.ArrayList;
import java.util.List;

import petadoption.auth.SessionManager;
import petadoption.core.DataStore;
import petadoption.core.RecordFinder;
import petadoption.core.Repository;
import petadoption.core.SearchStrategy;
import petadoption.exception.PetAdoptionException;
import petadoption.exception.RecordNotFoundException;
import petadoption.user.Role;
import petadoption.user.User;

/**
 * Service for pets: reading, searching, admin CRUD, and the status transitions other members call. Never
 * prints and never reads input; it throws {@link PetAdoptionException} subclasses for menus to show.
 *
 * <p>Strategy context: every search goes through {@link #finder}, which gets a new strategy on each call
 * (ADR-032).
 *
 * <p><b>Skeleton (PR julyan/pet-models):</b> the read methods work; the workflow transitions throw
 * {@link UnsupportedOperationException} until the pet-features PR.
 */
public class PetService {

    private final Repository<Pet> pets = DataStore.getInstance().pets();
    private final RecordFinder<Pet> finder = new RecordFinder<>();

    // ------------------------------------------------------------------ reading

    /** Any pet by ID, whatever its status. Contract method for other members. */
    public Pet getPetById(String petId) throws RecordNotFoundException {
        return pets.getById(petId);
    }

    /** Every pet, including adopted ones (admin view). Contract method for other members. */
    public List<Pet> getAllPets() {
        return pets.findAll();
    }

    /** Pets a Regular User may see: Available and Pending Adoption (ADR-020). Contract method. */
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
     * Pets matching {@code strategy}. Admins search every pet; anyone else searches only the pets they may
     * see (ADR-020), so adopted pets never leak into a Regular User's results. Contract method.
     */
    public List<Pet> search(SearchStrategy<Pet> strategy) {
        finder.setStrategy(strategy);
        return finder.find(isAdminLoggedIn() ? getAllPets() : getVisiblePets());
    }

    // ------------------------------------------------------------------ workflow transitions (Ace, Derick)

    /** AVAILABLE → PENDING_ADOPTION, when Ace approves an application. Contract method. */
    public void markPendingAdoption(String petId) throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /** PENDING_ADOPTION → AVAILABLE, when Ace revokes an approval. Contract method. */
    public void markAvailable(String petId) throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /** PENDING_ADOPTION → ADOPTED, when Derick creates an adoption record. Contract method. */
    public void markAdopted(String petId) throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /** ADOPTED → PENDING_ADOPTION, when Derick deletes an adoption record. Contract method. */
    public void revertToPendingAdoption(String petId) throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // ------------------------------------------------------------------ helpers

    private boolean isAdminLoggedIn() {
        User user = SessionManager.getInstance().getCurrentUser();
        return user != null && user.getRole() == Role.ADMIN;
    }
}
