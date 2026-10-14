package petadoption.user;

/** The two account roles. Set when an account is created and never changed (ADR-023). */
public enum Role {
    ADMIN("Admin"),
    REGULAR_USER("Regular User");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** Returns the readable label so menus and tables never show raw names like REGULAR_USER. */
    @Override
    public String toString() {
        return label;
    }
}
