package petadoption.application;

import java.util.ArrayList;
import java.util.List;

import petadoption.auth.SessionManager;
import petadoption.core.DataStore;
import petadoption.core.RecordFinder;
import petadoption.core.Repository;
import petadoption.core.SearchStrategy;
import petadoption.exception.DuplicateRecordException;
import petadoption.exception.InvalidStatusTransitionException;
import petadoption.exception.PetAdoptionException;
import petadoption.exception.RecordNotFoundException;
import petadoption.exception.UnauthorizedAccessException;
import petadoption.exception.ValidationException;
import petadoption.pet.PetService;
import petadoption.record.RecordService;
import petadoption.user.User;
import petadoption.util.Validator;

/**
 * Service managing all adoption application business rules, status transitions, and queries.
 */
public class ApplicationService {

    public static final int MAX_PENDING_PER_USER = 3;

    private final Repository<AdoptionApplication> applications = DataStore.getInstance().applications();
    private final RecordFinder<AdoptionApplication> finder = new RecordFinder<>();
    private final SessionManager session = SessionManager.getInstance();
    private final PetService petService = new PetService();
    private final RecordService recordService = new RecordService();

    // ------------------------------------------------------------------ Regular User methods

    /**
     * Submits a new adoption application for an available pet by the logged-in regular user.
     */
    public AdoptionApplication submitApplication(String petId, String address, String contactNumber,
            String occupation, LivingSituation livingSituation, boolean hasPreviousPets,
            String experienceDetails, String reason) throws PetAdoptionException {
        User user = session.requireRegularUser();

        String normalizedPetId = Validator.requireMatches("Pet ID", petId, "P\\d{4}",
                "Pet ID must look like P0001.");

        // Check pet existence through PetService if available
        try {
            petService.getPetById(normalizedPetId);
        } catch (RecordNotFoundException e) {
            throw e;
        } catch (UnsupportedOperationException e) {
            // Skeleton handling
        }

        if (recordService.hasRecordForPet(normalizedPetId)) {
            throw new InvalidStatusTransitionException("Pet " + normalizedPetId + " is already adopted.");
        }
        if (hasApprovedApplicationForPet(normalizedPetId)) {
            throw new InvalidStatusTransitionException("Pet " + normalizedPetId
                    + " is pending adoption with another applicant.");
        }

        // Duplicate active application check (ADR-017)
        for (AdoptionApplication app : applications.findAll()) {
            if (app.getUserId().equalsIgnoreCase(user.getUserId())
                    && app.getPetId().equalsIgnoreCase(normalizedPetId)
                    && app.isActive()) {
                throw new DuplicateRecordException("You already have an active application for this pet.");
            }
        }

        // Pending applications limit check (< 3)
        int pendingCount = 0;
        for (AdoptionApplication app : applications.findAll()) {
            if (app.getUserId().equalsIgnoreCase(user.getUserId()) && app.isPending()) {
                pendingCount++;
            }
        }
        if (pendingCount >= MAX_PENDING_PER_USER) {
            throw new ValidationException("Application limit",
                    "You cannot have more than " + MAX_PENDING_PER_USER + " pending applications at once.");
        }

        String appId = applications.nextId();
        AdoptionApplication newApp = new AdoptionApplication(appId, user.getUserId(), normalizedPetId,
                user.getFullName(), address, contactNumber, occupation, livingSituation,
                hasPreviousPets, experienceDetails, reason);

        applications.add(newApp);
        applications.save();
        return newApp;
    }

    /**
     * Returns all applications submitted by the logged-in regular user.
     */
    public List<AdoptionApplication> getMyApplications() throws UnauthorizedAccessException {
        User user = session.requireRegularUser();
        List<AdoptionApplication> result = new ArrayList<>();
        for (AdoptionApplication app : applications.findAll()) {
            if (app.getUserId().equalsIgnoreCase(user.getUserId())) {
                result.add(app);
            }
        }
        return result;
    }

    /**
     * Returns a specific application owned by the logged-in regular user.
     * Throws RecordNotFoundException if another user's ID is requested.
     */
    public AdoptionApplication getMyApplicationById(String applicationId) throws PetAdoptionException {
        User user = session.requireRegularUser();
        AdoptionApplication app;
        try {
            app = applications.getById(applicationId);
        } catch (RecordNotFoundException e) {
            throw new RecordNotFoundException("Application " + applicationId + " not found.");
        }
        if (!app.getUserId().equalsIgnoreCase(user.getUserId())) {
            throw new RecordNotFoundException("Application " + applicationId + " not found.");
        }
        return app;
    }

    /**
     * Updates an owned pending application.
     */
    public void updateMyApplication(String applicationId, String address, String contactNumber,
            String occupation, LivingSituation livingSituation, boolean hasPreviousPets,
            String experienceDetails, String reason) throws PetAdoptionException {
        AdoptionApplication app = getMyApplicationById(applicationId);

        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new InvalidStatusTransitionException("Only pending applications can be updated.");
        }

        app.setAddress(address);
        app.setContactNumber(contactNumber);
        app.setOccupation(occupation);
        app.setLivingSituation(livingSituation);
        app.setPreviousPetExperience(hasPreviousPets, experienceDetails);
        app.setReason(reason);

        applications.save();
    }

    /**
     * Cancels an owned pending application.
     */
    public void cancelMyApplication(String applicationId) throws PetAdoptionException {
        AdoptionApplication app = getMyApplicationById(applicationId);

        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new InvalidStatusTransitionException("Only pending applications can be cancelled.");
        }

        app.cancel();
        applications.save();
    }

    // ------------------------------------------------------------------ Admin methods

    /**
     * Returns all applications in the system (admin view).
     */
    public List<AdoptionApplication> getAllApplications() {
        return applications.findAll();
    }

    /**
     * Returns an application by its ID (admin view).
     */
    public AdoptionApplication getApplicationById(String applicationId) throws RecordNotFoundException {
        return applications.getById(applicationId);
    }

    /**
     * Approves an application, moves the pet to PENDING_ADOPTION, and automatically rejects
     * any other pending applications for the same pet.
     *
     * @return the number of other applications that were automatically rejected
     */
    public int approveApplication(String applicationId) throws PetAdoptionException {
        session.requireAdmin();
        AdoptionApplication app = getApplicationById(applicationId);

        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new InvalidStatusTransitionException("Cannot approve an application with status "
                    + app.getStatus().getLabel() + ". Only pending applications can be approved.");
        }

        if (hasApprovedApplicationForPet(app.getPetId())) {
            throw new InvalidStatusTransitionException("Pet " + app.getPetId() + " already has an approved adopter.");
        }
        if (recordService.hasRecordForPet(app.getPetId())) {
            throw new InvalidStatusTransitionException("Pet " + app.getPetId() + " is already adopted.");
        }

        app.approve();

        try {
            petService.markPendingAdoption(app.getPetId());
        } catch (UnsupportedOperationException e) {
            // Skeleton handling
        }

        // Auto-reject every other pending application for the same pet (ADR-013)
        int autoRejectedCount = 0;
        String autoRejectReason = RejectionReason.PET_HAS_APPROVED_ADOPTER.getLabel();
        for (AdoptionApplication other : applications.findAll()) {
            if (!other.getApplicationId().equalsIgnoreCase(app.getApplicationId())
                    && other.getPetId().equalsIgnoreCase(app.getPetId())
                    && other.getStatus() == ApplicationStatus.PENDING) {
                other.reject(autoRejectReason);
                autoRejectedCount++;
            }
        }

        applications.save();
        return autoRejectedCount;
    }

    /**
     * Rejects an application with a required reason.
     */
    public void rejectApplication(String applicationId, String reason) throws PetAdoptionException {
        session.requireAdmin();
        AdoptionApplication app = getApplicationById(applicationId);

        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new InvalidStatusTransitionException("Cannot reject an application with status "
                    + app.getStatus().getLabel() + ". Only pending applications can be rejected.");
        }

        app.reject(reason);
        applications.save();
    }

    /**
     * Revokes an approved application, setting its status back to REJECTED and returning the pet
     * to AVAILABLE. Only allowed if no adoption record has been finalized yet.
     */
    public void revokeApproval(String applicationId, String reason) throws PetAdoptionException {
        session.requireAdmin();
        AdoptionApplication app = getApplicationById(applicationId);

        if (app.getStatus() != ApplicationStatus.APPROVED) {
            throw new InvalidStatusTransitionException("Cannot revoke approval for an application with status "
                    + app.getStatus().getLabel() + ". Only approved applications can have approval revoked.");
        }

        if (recordService.hasRecordForApplication(applicationId)) {
            throw new InvalidStatusTransitionException(
                    "Cannot revoke approval: an official adoption record already exists for this application.");
        }

        app.revokeApproval(reason);

        try {
            petService.markAvailable(app.getPetId());
        } catch (UnsupportedOperationException e) {
            // Skeleton handling
        }

        applications.save();
    }

    /**
     * Deletes an invalid application (PENDING, REJECTED, or CANCELLED only).
     */
    public void deleteApplication(String applicationId) throws PetAdoptionException {
        session.requireAdmin();
        AdoptionApplication app = getApplicationById(applicationId);

        if (app.getStatus() == ApplicationStatus.APPROVED) {
            throw new InvalidStatusTransitionException("Approved applications cannot be deleted. Revoke approval first.");
        }

        applications.remove(applicationId);
        applications.save();
    }

    /**
     * Searches applications using a strategy via RecordFinder (ADR-032).
     */
    public List<AdoptionApplication> search(SearchStrategy<AdoptionApplication> strategy)
            throws UnauthorizedAccessException {
        session.requireAdmin();
        finder.setStrategy(strategy);
        return finder.find(applications.findAll());
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
        RecordService recService = new RecordService();
        List<AdoptionApplication> result = new ArrayList<>();
        for (AdoptionApplication app : applications.findAll()) {
            if (app.getStatus() == ApplicationStatus.APPROVED
                    && !recService.hasRecordForApplication(app.getApplicationId())) {
                result.add(app);
            }
        }
        return result;
    }
}
