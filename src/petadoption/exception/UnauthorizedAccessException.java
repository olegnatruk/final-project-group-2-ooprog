package petadoption.exception;

/** Thrown when the logged-in user's role is not allowed to perform an action. */
public class UnauthorizedAccessException extends PetAdoptionException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedAccessException(String message) {
        super(message);
    }

    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
