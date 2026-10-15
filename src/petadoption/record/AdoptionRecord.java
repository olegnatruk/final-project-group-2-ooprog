package petadoption.record;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import petadoption.core.Storable;
import petadoption.exception.DataFileException;
import petadoption.exception.PetAdoptionException;
import petadoption.exception.ValidationException;
import petadoption.util.Validator;

/**
 * An official adoption record created when an approved application is finalized.
 * Immutable IDs and references; only adoptionDate and notes can be edited by an admin.
 *
 * <p>Layout in records.txt:
 * recordId|applicationId|userId|petId|adoptionDate|notes
 */
public class AdoptionRecord implements Storable {

    private static final int FIELD_COUNT = 6;
    private static final String ID_REGEX = "REC\\d{4}";
    private static final String APP_ID_REGEX = "APP\\d{4}";
    private static final String USER_ID_REGEX = "U\\d{4}";
    private static final String PET_ID_REGEX = "P\\d{4}";
    private static final int MAX_NOTES_LENGTH = 200;

    private final String recordId;
    private final String applicationId;
    private final String userId;
    private final String petId;
    private LocalDate adoptionDate;
    private String notes;

    public AdoptionRecord(String recordId, String applicationId, String userId, String petId,
                          LocalDate adoptionDate, String notes) throws ValidationException {
        this.recordId = Validator.requireMatches("Record ID", recordId, ID_REGEX,
                "Record ID must look like REC0001.");
        this.applicationId = Validator.requireMatches("Application ID", applicationId, APP_ID_REGEX,
                "Application ID must look like APP0001.");
        this.userId = Validator.requireMatches("User ID", userId, USER_ID_REGEX,
                "User ID must look like U0001.");
        this.petId = Validator.requireMatches("Pet ID", petId, PET_ID_REGEX,
                "Pet ID must look like P0001.");
        setAdoptionDate(adoptionDate);
        setNotes(notes);
    }

    public String getRecordId() {
        return recordId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getUserId() {
        return userId;
    }

    public String getPetId() {
        return petId;
    }

    public LocalDate getAdoptionDate() {
        return adoptionDate;
    }

    public final void setAdoptionDate(LocalDate date) throws ValidationException {
        if (date == null) {
            throw new ValidationException("Adoption date", "Adoption date is required.");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new ValidationException("Adoption date", "Adoption date cannot be in the future.");
        }
        this.adoptionDate = date;
    }

    public String getNotes() {
        return notes;
    }

    public final void setNotes(String notes) throws ValidationException {
        if (notes == null || notes.isBlank()) {
            this.notes = "";
            return;
        }
        Validator.requireNoDelimiter("Notes", notes);
        String trimmed = notes.trim();
        if (trimmed.length() > MAX_NOTES_LENGTH) {
            throw new ValidationException("Notes", "Notes must be at most " + MAX_NOTES_LENGTH + " characters.");
        }
        this.notes = trimmed;
    }

    @Override
    public String getId() {
        return recordId;
    }

    @Override
    public String toRecord() {
        return String.join(Validator.DELIMITER,
                recordId,
                applicationId,
                userId,
                petId,
                adoptionDate.toString(),
                notes == null ? "" : notes);
    }

    public static AdoptionRecord fromRecord(String[] fields) throws PetAdoptionException {
        if (fields.length != FIELD_COUNT) {
            throw new DataFileException("expected " + FIELD_COUNT + " fields but found " + fields.length + ".");
        }
        try {
            LocalDate date = LocalDate.parse(fields[4]);
            return new AdoptionRecord(fields[0], fields[1], fields[2], fields[3], date, fields[5]);
        } catch (DateTimeParseException e) {
            throw new DataFileException("invalid adoption date: " + fields[4], e);
        } catch (ValidationException e) {
            throw new DataFileException("invalid " + e.getFieldName() + ": " + e.getMessage(), e);
        }
    }
}
