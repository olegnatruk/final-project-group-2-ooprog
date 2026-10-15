package petadoption.application;

/**
 * Status of an adoption application in its lifecycle.
 */
public enum ApplicationStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    CANCELLED("Cancelled");

    private final String label;

    ApplicationStatus(String label) {
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
