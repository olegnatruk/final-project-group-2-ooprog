package petadoption.application;

import java.util.ArrayList;
import java.util.List;

import petadoption.core.DataStore;
import petadoption.core.RecordFinder;
import petadoption.core.Repository;
import petadoption.core.SearchStrategy;
import petadoption.exception.PetAdoptionException;
import petadoption.exception.RecordNotFoundException;
import petadoption.exception.UnauthorizedAccessException;
import petadoption.record.RecordService;

/**
 * Service managing adoption application business rules and queries.
 */
public class ApplicationService {

    public static final int MAX_PENDING_PER_USER = 3;

    private final Repository<AdoptionApplication> applications = DataStore.getInstance().applications();
    private final RecordFinder<AdoptionApplication> finder = new RecordFinder<>();

    // ------------------------------------------------------------------ Regular User methods

    public AdoptionApplication submitApplication(String petId, String address, String contactNumber,
            String occupation, LivingSituation livingSituation, boolean hasPreviousPets,
            String experienceDetails, String reason) throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<AdoptionApplication> getMyApplications() throws UnauthorizedAccessException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public AdoptionApplication getMyApplicationById(String applicationId) throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void updateMyApplication(String applicationId, String address, String contactNumber,
            String occupation, LivingSituation livingSituation, boolean hasPreviousPets,
            String experienceDetails, String reason) throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void cancelMyApplication(String applicationId) throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // ------------------------------------------------------------------ Admin methods

    public List<AdoptionApplication> getAllApplications() {
        return applications.findAll();
    }

    public AdoptionApplication getApplicationById(String applicationId) throws RecordNotFoundException {
        return applications.getById(applicationId);
    }

    public int approveApplication(String applicationId) throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void rejectApplication(String applicationId, String reason) throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void revokeApproval(String applicationId, String reason) throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void deleteApplication(String applicationId) throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<AdoptionApplication> search(SearchStrategy<AdoptionApplication> strategy)
            throws UnauthorizedAccessException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // ------------------------------------------------------------------ queries for other members

    public boolean hasAnyApplicationForPet(String petId) {
        if (petId == null) {
            return false;
        }
        for (AdoptionApplication app : applications.findAll()) {
            if (app.getPetId().equalsIgnoreCase(petId.trim())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasApprovedApplicationForPet(String petId) {
        if (petId == null) {
            return false;
        }
        for (AdoptionApplication app : applications.findAll()) {
            if (app.getPetId().equalsIgnoreCase(petId.trim())
                    && app.getStatus() == ApplicationStatus.APPROVED) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAnyApplicationForUser(String userId) {
        if (userId == null) {
            return false;
        }
        for (AdoptionApplication app : applications.findAll()) {
            if (app.getUserId().equalsIgnoreCase(userId.trim())) {
                return true;
            }
        }
        return false;
    }

    public List<AdoptionApplication> getApprovedApplicationsWithoutRecord() {
        RecordService recordService = new RecordService();
        List<AdoptionApplication> result = new ArrayList<>();
        for (AdoptionApplication app : applications.findAll()) {
            if (app.getStatus() == ApplicationStatus.APPROVED
                    && !recordService.hasRecordForApplication(app.getApplicationId())) {
                result.add(app);
            }
        }
        return result;
    }
}
