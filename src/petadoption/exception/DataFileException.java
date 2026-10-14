package petadoption.exception;

/** Thrown when a data file cannot be read, written, or parsed. Wraps the original cause when there is one. */
public class DataFileException extends PetAdoptionException {

    private static final long serialVersionUID = 1L;

    public DataFileException(String message) {
        super(message);
    }

    public DataFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
