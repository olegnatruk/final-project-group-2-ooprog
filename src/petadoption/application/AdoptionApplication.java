package petadoption.application;

import java.time.LocalDate;

import petadoption.core.Storable;
import petadoption.exception.InvalidStatusTransitionException;
import petadoption.exception.PetAdoptionException;
import petadoption.exception.ValidationException;

/**
 * Skeleton of Ace's AdoptionApplication model to allow cross-package compilation
 * and method calls until Ace's full implementation is merged.
 */
public class AdoptionApplication implements Storable {

    private final String applicationId;
    private final String userId;
    private final String petId;
    private final String applicantName;
    private final LocalDate applicationDate;
    private ApplicationStatus status;
    private LocalDate decisionDate;
    private String decisionReason;

    public AdoptionApplication(String applicationId, String userId, String petId, String applicantName,
                               LocalDate applicationDate, ApplicationStatus status) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.petId = petId;
        this.applicantName = applicantName;
        this.applicationDate = applicationDate;
        this.status = status;
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

    public String getApplicantName() {
        return applicantName;
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

    public void approve() throws InvalidStatusTransitionException {
        if (status != ApplicationStatus.PENDING) {
            throw new InvalidStatusTransitionException("Only pending applications can be approved.");
        }
        this.status = ApplicationStatus.APPROVED;
        this.decisionDate = LocalDate.now();
    }

    public void reject(String reason) throws PetAdoptionException {
        if (status != ApplicationStatus.PENDING) {
            throw new InvalidStatusTransitionException("Only pending applications can be rejected.");
        }
        this.status = ApplicationStatus.REJECTED;
        this.decisionDate = LocalDate.now();
        this.decisionReason = reason;
    }

    public void cancel() throws InvalidStatusTransitionException {
        if (status != ApplicationStatus.PENDING) {
            throw new InvalidStatusTransitionException("Only pending applications can be cancelled.");
        }
        this.status = ApplicationStatus.CANCELLED;
        this.decisionDate = LocalDate.now();
    }

    public void revokeApproval(String reason) throws PetAdoptionException {
        if (status != ApplicationStatus.APPROVED) {
            throw new InvalidStatusTransitionException("Only approved applications can have approval revoked.");
        }
        this.status = ApplicationStatus.REJECTED;
        this.decisionDate = LocalDate.now();
        this.decisionReason = reason;
    }

    @Override
    public String getId() {
        return applicationId;
    }

    @Override
    public String toRecord() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public static AdoptionApplication fromRecord(String[] fields) throws PetAdoptionException {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
