package petadoption.pet;

/** A dog's size: the extra field that only {@link Dog} has. */
public enum DogSize {
    SMALL("Small"),
    MEDIUM("Medium"),
    LARGE("Large");

    private final String label;

    DogSize(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** Returns the readable label so menus and tables never show raw names like MEDIUM. */
    @Override
    public String toString() {
        return label;
    }
}
