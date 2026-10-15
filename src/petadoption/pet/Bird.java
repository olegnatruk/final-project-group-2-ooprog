package petadoption.pet;

import java.time.LocalDate;

import petadoption.exception.ValidationException;

/** A bird. Adds whether it can fly; birds can be up to 80 years old and 2 kg (ADR-018). */
public final class Bird extends Pet {

    public static final int MAX_AGE_YEARS = 80;
    public static final double MAX_WEIGHT_KG = 2.0;
    static final String CAN_FLY_FIELD = "Can fly";

    private boolean canFly;

    public Bird(String petId, String name, String breed, int ageYears, Gender gender, String color, double weightKg,
                String description, String healthInfo, VaccinationStatus vaccinationStatus, PetStatus status,
                LocalDate dateAdded, boolean canFly) throws ValidationException {
        super(petId, name, breed, gender, color, description, healthInfo, vaccinationStatus, status, dateAdded);
        // Set here, not in Pet's constructor: these checks call this class's overrides (see Pet's constructor).
        setAgeYears(ageYears);
        setWeightKg(weightKg);
        setCanFly(canFly);
    }

    public boolean canFly() {
        return canFly;
    }

    public void setCanFly(boolean canFly) {
        this.canFly = canFly;
    }

    @Override
    public PetType getType() {
        return PetType.BIRD;
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
        return CAN_FLY_FIELD + ": " + yesNo(canFly);
    }

    @Override
    protected String typeSpecificToRecord() {
        return String.valueOf(canFly);
    }

    @Override
    protected void setTypeSpecificValue(String value) throws ValidationException {
        setCanFly(parseFlag(CAN_FLY_FIELD, value));
    }
}
