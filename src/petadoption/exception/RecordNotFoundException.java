package petadoption.exception;

/** Thrown when an ID does not exist, or belongs to another user's record. */
public class RecordNotFoundException extends PetAdoptionException {

    private static final long serialVersionUID = 1L;

    public RecordNotFoundException(String message) {
        super(message);
    }

    public RecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
