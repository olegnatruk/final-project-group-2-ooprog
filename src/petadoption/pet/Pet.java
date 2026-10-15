package petadoption.pet;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import petadoption.core.Storable;
import petadoption.exception.DataFileException;
import petadoption.exception.PetAdoptionException;
import petadoption.exception.ValidationException;
import petadoption.util.Validator;

/**
 * A pet in the shelter. Abstract: every pet is a {@link Dog}, {@link Cat}, or {@link Bird}, and each subclass
 * decides its own type, age and weight limits, and extra field (ADR-018).
 *
 * <p>Encapsulation: every field is private and every editable field has a validating setter that throws
 * {@link ValidationException}. The ID, type, and date added never change, so they have no setters. Status
 * has only a package-private setter, so only {@link PetService} can change it (ADR-014).
 *
 * <p>Polymorphism: {@link #setAgeYears(int)} and {@link #setWeightKg(double)} check against
 * {@link #getMaxAgeYears()} and {@link #getMaxWeightKg()}, which each subclass overrides. A 40-year-old
 * bird is accepted; a 40-year-old dog is rejected.
 */
public abstract class Pet implements Storable {

    private static final int FIELD_COUNT = 14;
    private static final String ID_REGEX = "P\\d{4}";

    // Letters include Ñ / ñ. Unicode escapes keep the source file encoding-safe (same as Validator).
    private static final String LETTERS = "A-Za-z\\u00D1\\u00F1";
    private static final String NAME_REGEX = "^[" + LETTERS + "0-9][" + LETTERS + "0-9 .'-]{0,29}$";
    private static final String BREED_REGEX = "^[" + LETTERS + "][" + LETTERS + " .'()-]{1,39}$";
    private static final String COLOR_REGEX = "^[A-Za-z][A-Za-z ,/-]{1,29}$";
    private static final int DESCRIPTION_MIN = 10;
    private static final int DESCRIPTION_MAX = 300;
    private static final int HEALTH_INFO_MIN = 3;
    private static final int HEALTH_INFO_MAX = 200;
    private static final int MAX_WEIGHT_DECIMALS = 2;

    private final String petId;
    private String name;
    private String breed;
    private int ageYears;
    private Gender gender;
    private String color;
    private double weightKg;
    private String description;
    private String healthInfo;
    private VaccinationStatus vaccinationStatus;
    private PetStatus status;
    private final LocalDate dateAdded;

    /**
     * Sets every field whose rules are the same for all pet types. Age, weight, and the type-specific field
     * are set by the subclass constructor, because their limits come from overridden methods: calling those
     * from here would run them before the subclass is fully built (javac's "this-escape" warning).
     */
    protected Pet(String petId, String name, String breed, Gender gender, String color, String description,
                  String healthInfo, VaccinationStatus vaccinationStatus, PetStatus status, LocalDate dateAdded)
            throws ValidationException {
        this.petId = Validator.requireMatches("Pet ID", petId, ID_REGEX, "Pet ID must look like P0001.");
        if (dateAdded == null) {
            throw new ValidationException("Date added", "Date added is required.");
        }
        this.dateAdded = dateAdded;
        if (status == null) {
            throw new ValidationException("Status", "Status is required.");
        }
        this.status = status;
        setName(name);
        setBreed(breed);
        setGender(gender);
        setColor(color);
        setDescription(description);
        setHealthInfo(healthInfo);
        setVaccinationStatus(vaccinationStatus);
    }

    // ------------------------------------------------------------------ type-specific behavior

    /** Each subclass returns its own type. */
    public abstract PetType getType();

    /** Oldest allowed age for this type of pet; used by {@link #setAgeYears(int)}. */
    public abstract int getMaxAgeYears();

    /** Heaviest allowed weight for this type of pet; used by {@link #setWeightKg(double)}. */
    public abstract double getMaxWeightKg();

    /** The extra field as a readable line for the Pet Passport, e.g. "Size: Medium". */
    public abstract String getTypeSpecificDetails();

    /** The extra field as stored in the last column of pets.txt, e.g. "MEDIUM" or "true". */
    protected abstract String typeSpecificToRecord();

    /** Sets the extra field from its stored/text form (the same form {@link #typeSpecificToRecord()} writes). */
    protected abstract void setTypeSpecificValue(String value) throws ValidationException;

    // ------------------------------------------------------------------ getters

    public String getPetId() {
        return petId;
    }

    public String getName() {
        return name;
    }

    public String getBreed() {
        return breed;
    }

    /** Age in whole years; 0 means under 1 year (ADR-019). */
    public int getAgeYears() {
        return ageYears;
    }

    public Gender getGender() {
        return gender;
    }

    public String getColor() {
        return color;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public String getDescription() {
        return description;
    }

    public String getHealthInfo() {
        return healthInfo;
    }

    public VaccinationStatus getVaccinationStatus() {
        return vaccinationStatus;
    }

    public PetStatus getStatus() {
        return status;
    }

    /** Only available pets can receive adoption applications (ADR-020). */
    public boolean isAvailable() {
        return status == PetStatus.AVAILABLE;
    }

    public LocalDate getDateAdded() {
        return dateAdded;
    }

    // ------------------------------------------------------------------ validating setters

    public final void setName(String name) throws ValidationException {
        this.name = validateName(name);
    }

    public final void setBreed(String breed) throws ValidationException {
        this.breed = validateBreed(breed);
    }

    /** Polymorphism: the limit comes from the subclass's {@link #getMaxAgeYears()}. */
    public final void setAgeYears(int ageYears) throws ValidationException {
        this.ageYears = validateAge(ageYears, getMaxAgeYears(), getType().getLabel());
    }

    public final void setGender(Gender gender) throws ValidationException {
        if (gender == null) {
            throw new ValidationException("Gender", "Gender is required.");
        }
        this.gender = gender;
    }

    public final void setColor(String color) throws ValidationException {
        this.color = validateColor(color);
    }

    /** Polymorphism: the limit comes from the subclass's {@link #getMaxWeightKg()}. */
    public final void setWeightKg(double weightKg) throws ValidationException {
        this.weightKg = validateWeight(weightKg, getMaxWeightKg(), getType().getLabel());
    }

    public final void setDescription(String description) throws ValidationException {
        this.description = validateDescription(description);
    }

    public final void setHealthInfo(String healthInfo) throws ValidationException {
        this.healthInfo = validateHealthInfo(healthInfo);
    }

    public final void setVaccinationStatus(VaccinationStatus vaccinationStatus) throws ValidationException {
        if (vaccinationStatus == null) {
            throw new ValidationException("Vaccination status", "Vaccination status is required.");
        }
        this.vaccinationStatus = vaccinationStatus;
    }

    /**
     * Package-private on purpose: only {@link PetService} (same package) may change a pet's status, and it
     * checks the workflow rules first. There is no public {@code setStatus}.
     */
    final void setStatus(PetStatus status) {
        this.status = Objects.requireNonNull(status, "status");
    }

    // ------------------------------------------------------------------ field rules (docs/VALIDATION.md)
    // Static so input forms can use the exact same rules as TextRules, e.g. readValidated("Name: ", Pet::validateName).

    public static String validateName(String value) throws ValidationException {
        return Validator.requireMatches("Name", value, NAME_REGEX,
                "Name must be 1-30 characters (letters, digits, spaces, . ' -) and start with a letter or digit.");
    }

    public static String validateBreed(String value) throws ValidationException {
        return Validator.requireMatches("Breed", value, BREED_REGEX,
                "Breed must be 2-40 letters (spaces, . ' ( ) - allowed) and start with a letter.");
    }

    public static String validateColor(String value) throws ValidationException {
        return Validator.requireMatches("Color", value, COLOR_REGEX,
                "Color must be 2-30 letters (spaces, , / - allowed) and start with a letter.");
    }

    public static String validateDescription(String value) throws ValidationException {
        return Validator.requireLength("Description", value, DESCRIPTION_MIN, DESCRIPTION_MAX);
    }

    public static String validateHealthInfo(String value) throws ValidationException {
        return Validator.requireLength("Health info", value, HEALTH_INFO_MIN, HEALTH_INFO_MAX);
    }

    /** Age must be a whole number from 0 to {@code maxAgeYears}. */
    public static int validateAge(int ageYears, int maxAgeYears, String typeLabel) throws ValidationException {
        if (ageYears < 0 || ageYears > maxAgeYears) {
            throw new ValidationException("Age", "Age for a " + typeLabel + " must be from 0 to " + maxAgeYears
                    + " years (0 = under 1 year).");
        }
        return ageYears;
    }

    /** Weight must be more than 0, at most {@code maxWeightKg}, with at most 2 decimal places. */
    public static double validateWeight(double weightKg, double maxWeightKg, String typeLabel)
            throws ValidationException {
        if (Double.isNaN(weightKg) || Double.isInfinite(weightKg) || weightKg <= 0 || weightKg > maxWeightKg) {
            throw new ValidationException("Weight", "Weight for a " + typeLabel + " must be more than 0 and at most "
                    + maxWeightKg + " kg.");
        }
        if (BigDecimal.valueOf(weightKg).stripTrailingZeros().scale() > MAX_WEIGHT_DECIMALS) {
            throw new ValidationException("Weight", "Weight can have at most " + MAX_WEIGHT_DECIMALS
                    + " decimal places (e.g. 4.55).");
        }
        return weightKg;
    }

    /**
     * The age limit for a type before a pet exists (the Add form needs it). Reads the same constant the
     * subclass's {@link #getMaxAgeYears()} returns, so the limit is written only once.
     */
    public static int maxAgeFor(PetType type) {
        return switch (type) {
            case DOG -> Dog.MAX_AGE_YEARS;
            case CAT -> Cat.MAX_AGE_YEARS;
            case BIRD -> Bird.MAX_AGE_YEARS;
        };
    }

    /** The weight limit for a type before a pet exists. See {@link #maxAgeFor(PetType)}. */
    public static double maxWeightFor(PetType type) {
        return switch (type) {
            case DOG -> Dog.MAX_WEIGHT_KG;
            case CAT -> Cat.MAX_WEIGHT_KG;
            case BIRD -> Bird.MAX_WEIGHT_KG;
        };
    }

    /** Parses a stored boolean strictly: only "true" or "false" (any case). Used by Cat and Bird. */
    protected static boolean parseFlag(String field, String value) throws ValidationException {
        String cleaned = Validator.requireNotBlank(field, value);
        if (cleaned.equalsIgnoreCase("true")) {
            return true;
        }
        if (cleaned.equalsIgnoreCase("false")) {
            return false;
        }
        throw new ValidationException(field, field + " must be true or false.");
    }

    /** "Yes" / "No" for the Pet Passport. */
    protected static String yesNo(boolean value) {
        return value ? "Yes" : "No";
    }

    // ------------------------------------------------------------------ persistence

    @Override
    public String getId() {
        return petId;
    }

    /**
     * Layout (14 fields): petId|type|name|breed|ageYears|gender|color|weightKg|description|healthInfo|
     * vaccinationStatus|status|dateAdded|typeSpecific. Final so every subclass writes the same layout; only
     * the last field differs, through {@link #typeSpecificToRecord()}.
     */
    @Override
    public final String toRecord() {
        return String.join(Validator.DELIMITER,
                petId, getType().name(), name, breed, String.valueOf(ageYears), gender.name(), color,
                String.valueOf(weightKg), description, healthInfo, vaccinationStatus.name(), status.name(),
                dateAdded.toString(), typeSpecificToRecord());
    }

    /**
     * Builds the right subclass for {@code type}. {@code typeSpecificValue} uses the stored form: a
     * {@link DogSize} name for dogs, "true"/"false" for cats (indoor only) and birds (can fly).
     */
    public static Pet create(PetType type, String petId, String name, String breed, int ageYears, Gender gender,
                             String color, double weightKg, String description, String healthInfo,
                             VaccinationStatus vaccinationStatus, PetStatus status, LocalDate dateAdded,
                             String typeSpecificValue) throws ValidationException {
        if (type == null) {
            throw new ValidationException("Type", "Pet type is required.");
        }
        return switch (type) {
            case DOG -> new Dog(petId, name, breed, ageYears, gender, color, weightKg, description, healthInfo,
                    vaccinationStatus, status, dateAdded, Dog.parseSize(typeSpecificValue));
            case CAT -> new Cat(petId, name, breed, ageYears, gender, color, weightKg, description, healthInfo,
                    vaccinationStatus, status, dateAdded, parseFlag(Cat.INDOOR_ONLY_FIELD, typeSpecificValue));
            case BIRD -> new Bird(petId, name, breed, ageYears, gender, color, weightKg, description, healthInfo,
                    vaccinationStatus, status, dateAdded, parseFlag(Bird.CAN_FLY_FIELD, typeSpecificValue));
        };
    }

    /**
     * Rebuilds a pet from one line of pets.txt. Reads the type field first and lets {@link #create} pick the
     * subclass.
     */
    public static Pet fromRecord(String[] fields) throws PetAdoptionException {
        if (fields.length != FIELD_COUNT) {
            throw new DataFileException("expected " + FIELD_COUNT + " fields but found " + fields.length + ".");
        }
        try {
            PetType type = PetType.valueOf(fields[1]);
            int ageYears = Integer.parseInt(fields[4]);
            Gender gender = Gender.valueOf(fields[5]);
            double weightKg = Double.parseDouble(fields[7]);
            VaccinationStatus vaccinationStatus = VaccinationStatus.valueOf(fields[10]);
            PetStatus status = PetStatus.valueOf(fields[11]);
            LocalDate dateAdded = LocalDate.parse(fields[12]);
            return create(type, fields[0], fields[2], fields[3], ageYears, gender, fields[6], weightKg,
                    fields[8], fields[9], vaccinationStatus, status, dateAdded, fields[13]);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            // Multi-catch: unknown enum name or bad number (NumberFormatException is an
            // IllegalArgumentException), or a bad date (DateTimeParseException).
            throw new DataFileException("invalid value: " + e.getMessage(), e);
        } catch (ValidationException e) {
            throw new DataFileException("invalid " + e.getFieldName() + ": " + e.getMessage(), e);
        }
    }
}
