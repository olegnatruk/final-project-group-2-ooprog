package petadoption.pet;

import java.time.LocalDate;

import petadoption.exception.ValidationException;

/** A cat. Adds whether it must stay indoors; cats can be up to 30 years old and 15 kg (ADR-018). */
public final class Cat extends Pet {

    public static final int MAX_AGE_YEARS = 30;
    public static final double MAX_WEIGHT_KG = 15.0;
    static final String INDOOR_ONLY_FIELD = "Indoor only";

    private boolean indoorOnly;

    public Cat(String petId, String name, String breed, int ageYears, Gender gender, String color, double weightKg,
               String description, String healthInfo, VaccinationStatus vaccinationStatus, PetStatus status,
               LocalDate dateAdded, boolean indoorOnly) throws ValidationException {
        super(petId, name, breed, gender, color, description, healthInfo, vaccinationStatus, status, dateAdded);
        // Set here, not in Pet's constructor: these checks call this class's overrides (see Pet's constructor).
        setAgeYears(ageYears);
        setWeightKg(weightKg);
        setIndoorOnly(indoorOnly);
    }

    public boolean isIndoorOnly() {
        return indoorOnly;
    }

    public void setIndoorOnly(boolean indoorOnly) {
        this.indoorOnly = indoorOnly;
    }

    @Override
    public PetType getType() {
        return PetType.CAT;
    }

    @Override
    public int getMaxAgeYears() {
        return MAX_AGE_YEARS;
    }

    @Override
    public double getMaxWeightKg() {
        return MAX_WEIGHT_KG;
    }

    @Override
    public String getTypeSpecificDetails() {
        return INDOOR_ONLY_FIELD + ": " + yesNo(indoorOnly);
    }

    @Override
    protected String typeSpecificToRecord() {
        return String.valueOf(indoorOnly);
    }

    @Override
    protected void setTypeSpecificValue(String value) throws ValidationException {
        setIndoorOnly(parseFlag(INDOOR_ONLY_FIELD, value));
    }
}
