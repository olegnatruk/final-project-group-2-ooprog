package petadoption.application;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import petadoption.core.Storable;
import petadoption.exception.DataFileException;
import petadoption.exception.InvalidStatusTransitionException;
import petadoption.exception.PetAdoptionException;
import petadoption.exception.ValidationException;
import petadoption.util.Validator;

/**
 * An adoption application submitted by a regular user for a pet.
 *
 * <p>Encapsulation: every field is private. There is no {@code setStatus} method; status changes
 * only through {@link #approve()}, {@link #reject(String)}, {@link #cancel()}, and
 * {@link #revokeApproval(String)}. Editable fields can only be updated while the application is
 * {@link ApplicationStatus#PENDING}.
 *
 * <p>Layout in applications.txt (15 fields):
 * applicationId|userId|petId|applicantName|address|contactNumber|occupation|livingSituation|hasPreviousPets|experienceDetails|reason|applicationDate|status|decisionDate|decisionReason
 */
public class AdoptionApplication implements Storable {

    public static final int FIELD_COUNT = 15;
    private static final String APP_ID_REGEX = "APP\\d{4}";
    private static final String USER_ID_REGEX = "U\\d{4}";
    private static final String PET_ID_REGEX = "P\\d{4}";
    private static final String LETTERS = "A-Za-z\\u00D1\\u00F1";
    private static final String OCCUPATION_REGEX = "^[" + LETTERS + "][" + LETTERS + " .,'&/-]{1,49}$";
    private static final int EXPERIENCE_MIN = 10;
    private static final int EXPERIENCE_MAX = 300;
    private static final int REASON_MIN = 20;
    private static final int REASON_MAX = 500;

    private final String applicationId;
    private final String userId;
    private final String petId;
    private final String applicantName;
    private String address;
    private String contactNumber;
    private String occupation;
    private LivingSituation livingSituation;
    private boolean hasPreviousPets;
    private String experienceDetails;
    private String reason;
    private final LocalDate applicationDate;
    private ApplicationStatus status;
    private LocalDate decisionDate;
    private String decisionReason;

    /**
     * Creates a new adoption application in {@link ApplicationStatus#PENDING} status, dated today.
     */
    public AdoptionApplication(String applicationId, String userId, String petId, String applicantName,
                               String address, String contactNumber, String occupation,
                               LivingSituation livingSituation, boolean hasPreviousPets,
                               String experienceDetails, String reason) throws ValidationException {
        this(applicationId, userId, petId, applicantName, address, contactNumber, occupation,
                livingSituation, hasPreviousPets, experienceDetails, reason, LocalDate.now(),
                ApplicationStatus.PENDING, null, "");
    }

    /**
     * Full constructor used internally and by {@link #fromRecord(String[])}.
     */
    private AdoptionApplication(String applicationId, String userId, String petId, String applicantName,
                                String address, String contactNumber, String occupation,
                                LivingSituation livingSituation, boolean hasPreviousPets,
                                String experienceDetails, String reason, LocalDate applicationDate,
                                ApplicationStatus status, LocalDate decisionDate, String decisionReason)
            throws ValidationException {
        this.applicationId = Validator.requireMatches("Application ID", applicationId, APP_ID_REGEX,
                "Application ID must look like APP0001.");
        this.userId = Validator.requireMatches("User ID", userId, USER_ID_REGEX,
                "User ID must look like U0001.");
        this.petId = Validator.requireMatches("Pet ID", petId, PET_ID_REGEX,
                "Pet ID must look like P0001.");
        this.applicantName = Validator.validateName("Applicant name", applicantName);
        if (applicationDate == null) {
            throw new ValidationException("Application date", "Application date is required.");
        }
        this.applicationDate = applicationDate;
        if (status == null) {
            throw new ValidationException("Application status", "Application status is required.");
        }
        this.status = status;
        this.decisionDate = decisionDate;
        this.decisionReason = decisionReason == null ? "" : Validator.requireNoDelimiter("Decision reason", decisionReason);

        this.address = Validator.validateAddress(address);
        this.contactNumber = Validator.normalizeContactNumber(contactNumber);
        this.occupation = validateOccupation(occupation);
        if (livingSituation == null) {
            throw new ValidationException("Living situation", "Living situation is required.");
        }
        this.livingSituation = livingSituation;
        this.hasPreviousPets = hasPreviousPets;
        if (hasPreviousPets) {
            this.experienceDetails = Validator.requireLength("Experience details", experienceDetails,
                    EXPERIENCE_MIN, EXPERIENCE_MAX);
        } else {
            this.experienceDetails = "";
        }
        this.reason = Validator.requireLength("Reason for adoption", reason, REASON_MIN, REASON_MAX);
    }

    // ------------------------------------------------------------------ validation helper

    public static String validateOccupation(String value) throws ValidationException {
        return Validator.requireMatches("Occupation", value, OCCUPATION_REGEX,
                "Occupation must be 2-50 letters (spaces, . , ' & / - allowed) and start with a letter.");
    }

    // ------------------------------------------------------------------ status methods (no setStatus)

    /**
     * Approves this application. Allowed only when status is PENDING.
     */
    public void approve() throws InvalidStatusTransitionException {
        if (status != ApplicationStatus.PENDING) {
            throw new InvalidStatusTransitionException("Cannot approve an application with status "
                    + status.getLabel() + ". Only pending applications can be approved.");
        }
        this.status = ApplicationStatus.APPROVED;
        this.decisionDate = LocalDate.now();
        this.decisionReason = "";
    }

    /**
     * Rejects this application with a required reason. Allowed only when status is PENDING.
     */
    public void reject(String reason) throws PetAdoptionException {
        if (status != ApplicationStatus.PENDING) {
            throw new InvalidStatusTransitionException("Cannot reject an application with status "
                    + status.getLabel() + ". Only pending applications can be rejected.");
        }
        String cleaned = Validator.requireNoDelimiter("Rejection reason",
                Validator.requireNotBlank("Rejection reason", reason));
        this.status = ApplicationStatus.REJECTED;
        this.decisionDate = LocalDate.now();
        this.decisionReason = cleaned;
    }

    /**
     * Cancels this application. Allowed only when status is PENDING.
     */
    public void cancel() throws InvalidStatusTransitionException {
        if (status != ApplicationStatus.PENDING) {
            throw new InvalidStatusTransitionException("Cannot cancel an application with status "
                    + status.getLabel() + ". Only pending applications can be cancelled.");
        }
        this.status = ApplicationStatus.CANCELLED;
        this.decisionDate = LocalDate.now();
        this.decisionReason = "";
    }

    /**
     * Revokes approval for an approved application, returning it to REJECTED with a required reason.
     */
    public void revokeApproval(String reason) throws PetAdoptionException {
        if (status != ApplicationStatus.APPROVED) {
            throw new InvalidStatusTransitionException("Cannot revoke approval for an application with status "
                    + status.getLabel() + ". Only approved applications can have approval revoked.");
        }
        String cleaned = Validator.requireNoDelimiter("Revocation reason",
                Validator.requireNotBlank("Revocation reason", reason));
        this.status = ApplicationStatus.REJECTED;
        this.decisionDate = LocalDate.now();
        this.decisionReason = cleaned;
    }

    // ------------------------------------------------------------------ editable field setters

    private void requirePending() throws InvalidStatusTransitionException {
        if (this.status != ApplicationStatus.PENDING) {
            throw new InvalidStatusTransitionException("Only pending applications can be updated.");
        }
    }

    public void setAddress(String address) throws PetAdoptionException {
        requirePending();
        this.address = Validator.validateAddress(address);
    }

    public void setContactNumber(String contactNumber) throws PetAdoptionException {
        requirePending();
        this.contactNumber = Validator.normalizeContactNumber(contactNumber);
    }

    public void setOccupation(String occupation) throws PetAdoptionException {
        requirePending();
        this.occupation = validateOccupation(occupation);
    }

    public void setLivingSituation(LivingSituation livingSituation) throws PetAdoptionException {
        requirePending();
        if (livingSituation == null) {
            throw new ValidationException("Living situation", "Living situation is required.");
        }
        this.livingSituation = livingSituation;
    }

    public void setPreviousPetExperience(boolean hasPreviousPets, String details) throws PetAdoptionException {
        requirePending();
        this.hasPreviousPets = hasPreviousPets;
        if (hasPreviousPets) {
            this.experienceDetails = Validator.requireLength("Experience details", details,
                    EXPERIENCE_MIN, EXPERIENCE_MAX);
        } else {
            this.experienceDetails = "";
        }
    }

    public void setReason(String reason) throws PetAdoptionException {
        requirePending();
        this.reason = Validator.requireLength("Reason for adoption", reason, REASON_MIN, REASON_MAX);
    }

    // ------------------------------------------------------------------ getters

    public String getApplicationId() {
        return applicationId;
    }

    public String getUserId() {
        return userId;
    }

    public String getPetId() {
        return petId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public String getAddress() {
        return address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getOccupation() {
        return occupation;
    }

    public LivingSituation getLivingSituation() {
        return livingSituation;
    }

    public boolean hasPreviousPets() {
        return hasPreviousPets;
    }

    public String getExperienceDetails() {
        return experienceDetails;
    }

    public String getReason() {
        return reason;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public LocalDate getDecisionDate() {
        return decisionDate;
    }

    public String getDecisionReason() {
        return decisionReason;
    }

    public boolean isPending() {
        return status == ApplicationStatus.PENDING;
    }

    public boolean isActive() {
        return status == ApplicationStatus.PENDING || status == ApplicationStatus.APPROVED;
    }

    @Override
    public String getId() {
        return applicationId;
    }

    // ------------------------------------------------------------------ persistence

    @Override
    public String toRecord() {
        return String.join(Validator.DELIMITER,
                applicationId,
                userId,
                petId,
                applicantName,
                address,
                contactNumber,
                occupation,
                livingSituation.name(),
                Boolean.toString(hasPreviousPets),
                experienceDetails == null ? "" : experienceDetails,
                reason,
                applicationDate.toString(),
                status.name(),
                decisionDate == null ? "" : decisionDate.toString(),
                decisionReason == null ? "" : decisionReason);
    }

    public static AdoptionApplication fromRecord(String[] fields) throws PetAdoptionException {
        if (fields.length != FIELD_COUNT) {
            throw new DataFileException("expected " + FIELD_COUNT + " fields but found " + fields.length + ".");
        }
        try {
            LivingSituation livingSituation = LivingSituation.valueOf(fields[7]);
            boolean hasPrevPets = Boolean.parseBoolean(fields[8]);
            LocalDate appDate = LocalDate.parse(fields[11]);
            ApplicationStatus appStatus = ApplicationStatus.valueOf(fields[12]);
            LocalDate decDate = fields[13].isBlank() ? null : LocalDate.parse(fields[13]);
            String decReason = fields[14];

            return new AdoptionApplication(fields[0], fields[1], fields[2], fields[3], fields[4],
                    fields[5], fields[6], livingSituation, hasPrevPets, fields[9], fields[10],
                    appDate, appStatus, decDate, decReason);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw new DataFileException("invalid application data: " + e.getMessage(), e);
        } catch (ValidationException e) {
            throw new DataFileException("invalid " + e.getFieldName() + ": " + e.getMessage(), e);
        }
    }
}
