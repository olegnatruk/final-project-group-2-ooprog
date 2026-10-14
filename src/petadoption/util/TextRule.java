package petadoption.util;

import petadoption.exception.ValidationException;

/**
 * A validation rule for one piece of text input. Returns the cleaned value (trimmed, normalized) or throws
 * {@link ValidationException}. Lets {@link InputHelper} re-prompt with any rule, for example:
 * {@code InputHelper.readValidated("Email: ", Validator::validateEmail)}.
 */
@FunctionalInterface
public interface TextRule {

    String check(String input) throws ValidationException;
}
