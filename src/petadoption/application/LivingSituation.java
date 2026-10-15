package petadoption.application;

/**
 * Living situation of the applicant.
 */
public enum LivingSituation {
    HOUSE_WITH_YARD("House with Yard"),
    HOUSE_WITHOUT_YARD("House without Yard"),
    APARTMENT_OR_CONDO("Apartment or Condo"),
    OTHER("Other");

    private final String label;

    LivingSituation(String label) {
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
