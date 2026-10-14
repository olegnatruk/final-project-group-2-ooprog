package petadoption.user;

/** Whether an account may log in. Disabled accounts keep their data but cannot log in. */
public enum AccountStatus {
    ACTIVE("Active"),
    DISABLED("Disabled");

    private final String label;

    AccountStatus(String label) {
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
