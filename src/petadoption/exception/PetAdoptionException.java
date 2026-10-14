package petadoption.exception;

/**
 * Base class for every error the Pet Adoption System expects and can recover from.
 *
 * <p>It is a checked exception on purpose (ADR-033): every case is a recoverable condition such as bad
 * input, a wrong status, or a missing record. The compiler forces callers to handle or declare it, and
 * menus catch it and print the message instead of crashing.
 */
public class PetAdoptionException extends Exception {

    private static final long serialVersionUID = 1L;

    public PetAdoptionException(String message) {
        super(message);
    }

    public PetAdoptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
