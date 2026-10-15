package petadoption.pet;

import java.time.LocalDate;

import petadoption.exception.ValidationException;
import petadoption.util.Validator;

/** A dog. Adds a {@link DogSize}; dogs can be up to 25 years old and 100 kg (ADR-018). */
public final class Dog extends Pet {

    public static final int MAX_AGE_YEARS = 25;
    public static final double MAX_WEIGHT_KG = 100.0;

    private DogSize size;

    public Dog(String petId, String name, String breed, int ageYears, Gender gender, String color, double weightKg,
               String description, String healthInfo, VaccinationStatus vaccinationStatus, PetStatus status,
               LocalDate dateAdded, DogSize size) throws ValidationException {
        super(petId, name, breed, gender, color, description, healthInfo, vaccinationStatus, status, dateAdded);
        // Set here, not in Pet's constructor: these checks call this class's overrides (see Pet's constructor).
        setAgeYears(ageYears);
        setWeightKg(weightKg);
        setSize(size);
    }

    public DogSize getSize() {
        return size;
    }

    public void setSize(DogSize size) throws ValidationException {
        if (size == null) {
            throw new ValidationException("Size", "Size is required.");
        }
        this.size = size;
    }

    @Override
    public PetType getType() {
        return PetType.DOG;
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
        return "Size: " + size.getLabel();
    }

    @Override
    protected String typeSpecificToRecord() {
        return size.name();
    }

    @Override
    protected void setTypeSpecificValue(String value) throws ValidationException {
        setSize(parseSize(value));
    }

    /** Parses a stored size name such as "MEDIUM" (any case). */
    static DogSize parseSize(String value) throws ValidationException {
        String cleaned = Validator.requireNotBlank("Size", value);
        try {
            return DogSize.valueOf(cleaned.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Size", "Size must be SMALL, MEDIUM, or LARGE.", e);
        }
    }
}
