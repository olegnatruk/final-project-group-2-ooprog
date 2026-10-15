package petadoption.pet;

/** How far along a pet is with its vaccinations. */
public enum VaccinationStatus {
    FULLY_VACCINATED("Fully Vaccinated"),
    PARTIALLY_VACCINATED("Partially Vaccinated"),
    NOT_VACCINATED("Not Vaccinated");

    private final String label;

    VaccinationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** Returns the readable label so menus and tables never show raw names like NOT_VACCINATED. */
    @Override
    public String toString() {
        return label;
    }
}
