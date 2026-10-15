package petadoption.pet;

/** The kinds of pet the shelter handles. Each value matches one {@link Pet} subclass (ADR-018). */
public enum PetType {
    DOG("Dog"),
    CAT("Cat"),
    BIRD("Bird");

    private final String label;

    PetType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** Returns the readable label so menus and tables never show raw names like DOG. */
    @Override
    public String toString() {
        return label;
    }
}
