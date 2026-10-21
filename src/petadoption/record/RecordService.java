package petadoption.record;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import petadoption.application.AdoptionApplication;
import petadoption.application.ApplicationService;
import petadoption.application.ApplicationStatus;
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
import petadoption.user.Role;
import petadoption.user.User;

/**
 * Service managing adoption records and history queries.
 */
public class RecordService {

    private final Repository<AdoptionRecord> records = DataStore.getInstance().records();
    private final SessionManager session = SessionManager.getInstance();
    private final RecordFinder<AdoptionRecord> finder = new RecordFinder<>();
    private final PetService petService = new PetService();

    // ------------------------------------------------------------------ admin methods

    public List<AdoptionRecord> getAllRecords() {
        return records.findAll();
    }

    public AdoptionRecord getRecordById(String recordId) throws RecordNotFoundException {
        return records.getById(recordId);
    }

    public AdoptionRecord createRecord(String applicationId, LocalDate adoptionDate, String notes)
            throws PetAdoptionException {
        session.requireAdmin();

        if (applicationId == null || applicationId.isBlank()) {
            throw new ValidationException("Application ID", "Application ID is required.");
        }

        if (hasRecordForApplication(applicationId)) {
            throw new DuplicateRecordException("An adoption record already exists for this application.");
        }

        LocalDate recordDate = adoptionDate == null ? LocalDate.now() : adoptionDate;
        if (recordDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Adoption date", "Adoption date cannot be in the future.");
        }

        AdoptionApplication app = new ApplicationService().getApplicationById(applicationId);

        if (app.getStatus() != ApplicationStatus.APPROVED) {
            throw new InvalidStatusTransitionException("Application must be Approved to create an adoption record.");
        }

        if (app.getApplicationDate() != null && recordDate.isBefore(app.getApplicationDate())) {
            throw new ValidationException("Adoption date", "Adoption date cannot be before the application date.");
        }

        String recordId = records.nextId();
        AdoptionRecord record = new AdoptionRecord(recordId, app.getApplicationId(), app.getUserId(), app.getPetId(),
                recordDate, notes);

        records.add(record);
        records.save();

        petService.markAdopted(app.getPetId());

        return record;
    }

    public void updateRecord(String recordId, LocalDate adoptionDate, String notes)
            throws PetAdoptionException {
        session.requireAdmin();
        AdoptionRecord record = records.getById(recordId);

        LocalDate newDate = adoptionDate == null ? record.getAdoptionDate() : adoptionDate;
        if (newDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Adoption date", "Adoption date cannot be in the future.");
        }

        AdoptionApplication app = null;
        try {
            app = new ApplicationService().getApplicationById(record.getApplicationId());
        } catch (PetAdoptionException e) {
            // The application was deleted; only the future-date check above applies.
        }
        if (app != null && app.getApplicationDate() != null && newDate.isBefore(app.getApplicationDate())) {
            throw new ValidationException("Adoption date", "Adoption date cannot be before the application date.");
        }

        record.setAdoptionDate(newDate);
        record.setNotes(notes);

        records.save();
    }

    public void deleteRecord(String recordId) throws PetAdoptionException {
        session.requireAdmin();
        AdoptionRecord record = records.getById(recordId);
        String petId = record.getPetId();

        records.remove(recordId);
        records.save();

        petService.revertToPendingAdoption(petId);
    }

    public List<AdoptionRecord> search(SearchStrategy<AdoptionRecord> strategy)
            throws UnauthorizedAccessException {
        session.requireAdmin();
        finder.setStrategy(strategy);
        return finder.find(records.findAll());
    }

    // ------------------------------------------------------------------ Regular User methods

    public List<AdoptionRecord> getMyRecords() throws UnauthorizedAccessException {
        User user = session.requireLoggedIn();
        if (user.getRole() == Role.ADMIN) {
            throw new UnauthorizedAccessException("Admins do not have personal adoption records.");
        }

        List<AdoptionRecord> userRecords = new ArrayList<>();
        for (AdoptionRecord r : records.findAll()) {
            if (r.getUserId().equalsIgnoreCase(user.getUserId())) {
                userRecords.add(r);
            }
        }
        return userRecords;
    }

    // ------------------------------------------------------------------ queries for other members

    public boolean hasRecordForPet(String petId) {
        if (petId == null) {
            return false;
        }
        for (AdoptionRecord record : records.findAll()) {
            if (record.getPetId().equalsIgnoreCase(petId.trim())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRecordForApplication(String applicationId) {
        if (applicationId == null) {
            return false;
        }
        for (AdoptionRecord record : records.findAll()) {
            if (record.getApplicationId().equalsIgnoreCase(applicationId.trim())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRecordForUser(String userId) {
        if (userId == null) {
            return false;
        }
        for (AdoptionRecord record : records.findAll()) {
            if (record.getUserId().equalsIgnoreCase(userId.trim())) {
                return true;
            }
        }
        return false;
    }
}
