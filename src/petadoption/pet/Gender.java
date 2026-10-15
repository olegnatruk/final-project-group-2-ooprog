package petadoption.pet;

/** A pet's gender. {@code UNKNOWN} is for pets whose gender has not been determined (e.g. some birds). */
public enum Gender {
    MALE("Male"),
    FEMALE("Female"),
    UNKNOWN("Unknown");

    private final String label;

    Gender(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** Returns the readable label so menus and tables never show raw names like FEMALE. */
    @Override
    public String toString() {
        return label;
    }
}
