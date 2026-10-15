package petadoption.pet;

import java.util.List;

import petadoption.core.SearchStrategy;
import petadoption.exception.PetAdoptionException;
import petadoption.exception.RecordNotFoundException;

/**
 * Skeleton of Julyan's PetService containing contract methods.
 */
public class PetService {

    public Pet getPetById(String petId) throws RecordNotFoundException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<Pet> getAllPets() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<Pet> getVisiblePets() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<Pet> search(SearchStrategy<Pet> strategy) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void markPendingAdoption(String petId) throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void markAvailable(String petId) throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void markAdopted(String petId) throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void revertToPendingAdoption(String petId) throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
