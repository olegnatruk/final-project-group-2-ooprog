package petadoption.application;

/**
 * Standard reasons for rejecting an application or revoking an approval.
 * For OTHER, the admin types a custom reason.
 */
public enum RejectionReason {
    INCOMPLETE_INFORMATION("Incomplete information"),
    DOES_NOT_MEET_REQUIREMENTS("Does not meet requirements"),
    PET_HAS_APPROVED_ADOPTER("Pet already has an approved adopter"),
    DUPLICATE_APPLICATION("Duplicate application"),
    OTHER("Other");

    private final String label;

    RejectionReason(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** Returns the readable label so menus and tables never show raw enum names. */
    @Override
    public String toString() {
        return label;
    }
}
