package petadoption.pet;

/**
 * Where a pet is in the adoption workflow. Changes only through {@link PetService} (ADR-014); see the pet
 * status flow in docs/BUSINESS-RULES.md.
 */
public enum PetStatus {
    AVAILABLE("Available"),
    PENDING_ADOPTION("Pending Adoption"),
    ADOPTED("Adopted");

    private final String label;

    PetStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** Returns the readable label so menus and tables never show raw names like PENDING_ADOPTION. */
    @Override
    public String toString() {
        return label;
    }
}
