package petadoption.pet;

/**
 * Pet species type.
 */
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

    @Override
    public String toString() {
        return label;
    }
}
