package petadoption.exception;

/**
 * Thrown when a single field has an invalid value. Carries the field name so screens can say which
 * input was wrong.
 */
public class ValidationException extends PetAdoptionException {

    private static final long serialVersionUID = 1L;

    private final String fieldName;

    public ValidationException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public ValidationException(String fieldName, String message, Throwable cause) {
        super(message, cause);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
