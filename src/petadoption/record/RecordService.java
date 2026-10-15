package petadoption.record;

import java.time.LocalDate;
import java.util.List;

import petadoption.core.DataStore;
import petadoption.core.RecordFinder;
import petadoption.core.Repository;
import petadoption.core.SearchStrategy;
import petadoption.exception.PetAdoptionException;
import petadoption.exception.RecordNotFoundException;
import petadoption.exception.UnauthorizedAccessException;

/**
 * Service managing adoption records and history queries.
 */
public class RecordService {

    private final Repository<AdoptionRecord> records = DataStore.getInstance().records();
    private final RecordFinder<AdoptionRecord> finder = new RecordFinder<>();

    // ------------------------------------------------------------------ admin methods

    public List<AdoptionRecord> getAllRecords() {
        return records.findAll();
    }

    public AdoptionRecord getRecordById(String recordId) throws RecordNotFoundException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public AdoptionRecord createRecord(String applicationId, LocalDate adoptionDate, String notes)
            throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void updateRecord(String recordId, LocalDate adoptionDate, String notes)
            throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void deleteRecord(String recordId) throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<AdoptionRecord> search(SearchStrategy<AdoptionRecord> strategy)
            throws UnauthorizedAccessException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // ------------------------------------------------------------------ Regular User methods

    public List<AdoptionRecord> getMyRecords() throws UnauthorizedAccessException {
        throw new UnsupportedOperationException("Not implemented yet");
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
