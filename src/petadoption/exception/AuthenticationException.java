package petadoption.exception;

/** Thrown when login fails: wrong username/email or password, or a disabled account. */
public class AuthenticationException extends PetAdoptionException {

    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
