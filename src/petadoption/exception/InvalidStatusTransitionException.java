package petadoption.exception;

/** Thrown when an action is not allowed in the record's current status. */
public class InvalidStatusTransitionException extends PetAdoptionException {

    private static final long serialVersionUID = 1L;

    public InvalidStatusTransitionException(String message) {
        super(message);
    }

    public InvalidStatusTransitionException(String message, Throwable cause) {
        super(message, cause);
    }
}
