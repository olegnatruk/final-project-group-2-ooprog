package petadoption.util;

import petadoption.exception.ValidationException;

/**
 * Shared field validation rules (see docs/VALIDATION.md). Every method trims the input, checks it, and
 * returns the cleaned value, or throws {@link ValidationException} with a message that says what is wrong
 * and what is expected. Because they return the cleaned value, single-argument methods can be used
 * directly as a {@link TextRule}.
 */
public final class Validator {

    /** The file delimiter. Never allowed inside stored text (ADR-002). */
    public static final String DELIMITER = "|";

    // Letters include Ñ / ñ (Ñ / ñ). Unicode escapes keep the source file encoding-safe.
    private static final String LETTERS = "A-Za-z\\u00D1\\u00F1";
    private static final String NAME_REGEX = "^[" + LETTERS + "][" + LETTERS + " .'-]{1,49}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String CONTACT_REGEX = "^09\\d{9}$";
    private static final String USERNAME_REGEX = "^[A-Za-z0-9_]{4,20}$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)\\S{8,30}$";

    private static final int EMAIL_MAX_LENGTH = 100;
    private static final int ADDRESS_MIN_LENGTH = 5;
    private static final int ADDRESS_MAX_LENGTH = 150;

    private Validator() {
        // static utility class
    }

    // ------------------------------------------------------------------ generic checks

    public static String requireNotBlank(String field, String value) throws ValidationException {
        if (value == null || value.isBlank()) {
            throw new ValidationException(field, field + " is required.");
        }
        return value.trim();
    }

    public static String requireNoDelimiter(String field, String value) throws ValidationException {
        if (value != null && value.contains(DELIMITER)) {
            throw new ValidationException(field, "The | character is not allowed.");
        }
        return value == null ? null : value.trim();
    }

    public static String requireLength(String field, String value, int min, int max) throws ValidationException {
        String cleaned = requireNoDelimiter(field, requireNotBlank(field, value));
        if (cleaned.length() < min || cleaned.length() > max) {
            throw new ValidationException(field, field + " must be " + min + "-" + max + " characters.");
        }
        return cleaned;
    }

    public static String requireMatches(String field, String value, String regex, String message)
            throws ValidationException {
        String cleaned = requireNoDelimiter(field, requireNotBlank(field, value));
        if (!cleaned.matches(regex)) {
            throw new ValidationException(field, message);
        }
        return cleaned;
    }

    /** Overload for whole numbers. */
    public static int requireRange(String field, int value, int min, int max) throws ValidationException {
        if (value < min || value > max) {
            throw new ValidationException(field, field + " must be between " + min + " and " + max + ".");
        }
        return value;
    }

    /** Overload for decimal numbers. */
    public static double requireRange(String field, double value, double min, double max)
            throws ValidationException {
        if (Double.isNaN(value) || value < min || value > max) {
            throw new ValidationException(field, field + " must be between " + min + " and " + max + ".");
        }
        return value;
    }

    // ------------------------------------------------------------------ account fields

    /** First name, last name, or any person's name. */
    public static String validateName(String field, String value) throws ValidationException {
        return requireMatches(field, value, NAME_REGEX,
                field + " must be 2-50 letters (spaces, . ' - allowed) and start with a letter.");
    }

    /** Returns the email in lower case. */
    public static String validateEmail(String value) throws ValidationException {
        String cleaned = requireNoDelimiter("Email", requireNotBlank("Email", value));
        if (cleaned.length() > EMAIL_MAX_LENGTH) {
            throw new ValidationException("Email", "Email must be at most " + EMAIL_MAX_LENGTH + " characters.");
        }
        if (!cleaned.matches(EMAIL_REGEX)) {
            throw new ValidationException("Email", "Email must look like name@example.com.");
        }
        return cleaned.toLowerCase();
    }

    /**
     * Accepts "09171234567", "0917-123-4567", "0917 123 4567", or "+639171234567" and returns the
     * normalized form "09171234567".
     */
    public static String normalizeContactNumber(String value) throws ValidationException {
        String cleaned = requireNoDelimiter("Contact number", requireNotBlank("Contact number", value));
        cleaned = cleaned.replace(" ", "").replace("-", "");
        if (cleaned.startsWith("+639")) {
            cleaned = "0" + cleaned.substring(3);
        }
        if (!cleaned.matches(CONTACT_REGEX)) {
            throw new ValidationException("Contact number",
                    "Contact number must be 11 digits starting with 09 (e.g. 09171234567).");
        }
        return cleaned;
    }

    public static String validateUsername(String value) throws ValidationException {
        return requireMatches("Username", value, USERNAME_REGEX,
                "Username must be 4-20 characters: letters, digits, or _ only.");
    }

    /** Checks the password rules. Passwords are not trimmed: spaces are simply not allowed. */
    public static String validatePassword(String password, String username) throws ValidationException {
        if (password == null || password.isEmpty()) {
            throw new ValidationException("Password", "Password is required.");
        }
        requireNoDelimiter("Password", password);
        if (!password.matches(PASSWORD_REGEX)) {
            throw new ValidationException("Password",
                    "Password must be 8-30 characters with at least one letter and one number, no spaces.");
        }
        if (username != null && password.equalsIgnoreCase(username.trim())) {
            throw new ValidationException("Password", "Password must not be the same as the username.");
        }
        return password;
    }

    public static String validateAddress(String value) throws ValidationException {
        String cleaned = requireNoDelimiter("Address", requireNotBlank("Address", value));
        if (cleaned.length() < ADDRESS_MIN_LENGTH || cleaned.length() > ADDRESS_MAX_LENGTH) {
            throw new ValidationException("Address",
                    "Address must be " + ADDRESS_MIN_LENGTH + "-" + ADDRESS_MAX_LENGTH + " characters.");
        }
        return cleaned;
    }
}
