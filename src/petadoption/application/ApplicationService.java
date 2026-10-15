package petadoption.application;

import java.util.List;

import petadoption.exception.PetAdoptionException;
import petadoption.exception.RecordNotFoundException;
import petadoption.exception.UnauthorizedAccessException;

/**
 * Skeleton of Ace's ApplicationService containing contract methods.
 */
public class ApplicationService {

    public List<AdoptionApplication> getAllApplications() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public AdoptionApplication getApplicationById(String applicationId) throws RecordNotFoundException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean hasAnyApplicationForPet(String petId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean hasApprovedApplicationForPet(String petId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean hasAnyApplicationForUser(String userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<AdoptionApplication> getApprovedApplicationsWithoutRecord() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
