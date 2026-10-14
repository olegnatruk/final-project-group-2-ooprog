package petadoption.exception;

/** Thrown when a value must be unique but already exists (username, email, ID, active application). */
public class DuplicateRecordException extends PetAdoptionException {

    private static final long serialVersionUID = 1L;

    public DuplicateRecordException(String message) {
        super(message);
    }

    public DuplicateRecordException(String message, Throwable cause) {
        super(message, cause);
    }
}
