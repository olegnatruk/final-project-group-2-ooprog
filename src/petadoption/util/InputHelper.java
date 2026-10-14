package petadoption.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import petadoption.exception.ValidationException;

/**
 * The only class that reads from the keyboard. Every method keeps asking until the input is valid, printing
 * an {@code [ERROR]} line for each bad attempt, so callers always get a clean value.
 *
 * <p>Always reads whole lines and parses them (never {@code nextInt()}), which avoids the skipped-prompt
 * problem described in docs/GOTCHAS.md.
 */
public final class InputHelper {

    private static final Scanner SCANNER = new Scanner(System.in);

    private InputHelper() {
        // static utility class
    }

    // ------------------------------------------------------------------ menus

    /** Reads a menu choice from 1 to {@code optionCount}. */
    public static int readMenuChoice(int optionCount) {
        while (true) {
            String line = readLine("Enter choice: ");
            try {
                int choice = Integer.parseInt(line);
                if (choice >= 1 && choice <= optionCount) {
                    return choice;
                }
            } catch (NumberFormatException e) {
                // fall through to the error message below
            }
            ConsoleUI.printError("Please enter a number from 1 to " + optionCount + ".");
        }
    }

    // ------------------------------------------------------------------ numbers

    /** Reads any whole number. */
    public static int readInt(String prompt) {
        return readInt(prompt, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /** Reads a whole number from {@code min} to {@code max} (overload of {@link #readInt(String)}). */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            String line = readLine(prompt);
            try {
                return Validator.requireRange("Value", Integer.parseInt(line), min, max);
            } catch (NumberFormatException e) {
                ConsoleUI.printError("Please enter a whole number.");
            } catch (ValidationException e) {
                ConsoleUI.printError("Please enter a whole number from " + min + " to " + max + ".");
            }
        }
    }

    /** Reads a decimal number from {@code min} to {@code max}. */
    public static double readDouble(String prompt, double min, double max) {
        while (true) {
            String line = readLine(prompt);
            try {
                double value = Double.parseDouble(line);
                if (Double.isNaN(value) || Double.isInfinite(value)) {
                    throw new NumberFormatException("not a finite number");
                }
                return Validator.requireRange("Value", value, min, max);
            } catch (NumberFormatException e) {
                ConsoleUI.printError("Please enter a valid number.");
            } catch (ValidationException e) {
                ConsoleUI.printError("Please enter a number from " + min + " to " + max + ".");
            }
        }
    }

    // ------------------------------------------------------------------ text

    /** Reads required text of any length (no {@code |}). */
    public static String readText(String prompt) {
        return readValidated(prompt, input -> Validator.requireNoDelimiter("Value",
                Validator.requireNotBlank("Value", input)));
    }

    /** Reads required text with a length limit (overload of {@link #readText(String)}). */
    public static String readText(String prompt, int minLength, int maxLength) {
        return readValidated(prompt, input -> Validator.requireLength("Value", input, minLength, maxLength));
    }

    /** Reads text and checks it with {@code rule}, re-prompting until the rule accepts it. */
    public static String readValidated(String prompt, TextRule rule) {
        while (true) {
            String line = readLine(prompt);
            try {
                return rule.check(line);
            } catch (ValidationException e) {
                ConsoleUI.printError(e.getMessage());
            }
        }
    }

    /**
     * For update forms: shows the current value; pressing Enter keeps it, anything else is checked with
     * {@code rule}.
     */
    public static String readOptional(String prompt, String currentValue, TextRule rule) {
        while (true) {
            String line = readLine(prompt + " [" + currentValue + "] (Enter to keep): ");
            if (line.isEmpty()) {
                return currentValue;
            }
            try {
                return rule.check(line);
            } catch (ValidationException e) {
                ConsoleUI.printError(e.getMessage());
            }
        }
    }

    // ------------------------------------------------------------------ choices

    public static boolean readYesNo(String prompt) {
        while (true) {
            String line = readLine(prompt + " (y/n): ").toLowerCase();
            switch (line) {
                case "y", "yes" -> {
                    return true;
                }
                case "n", "no" -> {
                    return false;
                }
                default -> ConsoleUI.printError("Please enter y or n.");
            }
        }
    }

    /** Asks "Are you sure you want to {@code action}?" and prints "Action cancelled." on no. */
    public static boolean confirm(String action) {
        boolean confirmed = readYesNo("Are you sure you want to " + action + "?");
        if (!confirmed) {
            ConsoleUI.printInfo("Action cancelled.");
        }
        return confirmed;
    }

    /**
     * Shows the values as a numbered list and returns the one picked. Enums display through
     * {@code toString()}, which returns their readable label.
     */
    public static <E extends Enum<E>> E readEnum(String prompt, E[] values) {
        System.out.println(prompt);
        for (int i = 0; i < values.length; i++) {
            System.out.println("  " + (i + 1) + ". " + values[i]);
        }
        return values[readMenuChoice(values.length) - 1];
    }

    // ------------------------------------------------------------------ dates and IDs

    /** Reads a date as yyyy-MM-dd. If {@code defaultValue} is not null, pressing Enter returns it. */
    public static LocalDate readDate(String prompt, LocalDate defaultValue) {
        while (true) {
            String suffix = defaultValue == null ? " (yyyy-MM-dd): " : " (yyyy-MM-dd) [" + defaultValue + "]: ";
            String line = readLine(prompt + suffix);
            if (line.isEmpty() && defaultValue != null) {
                return defaultValue;
            }
            try {
                return LocalDate.parse(line);
            } catch (DateTimeParseException e) {
                ConsoleUI.printError("Please enter a valid date as yyyy-MM-dd.");
            }
        }
    }

    /** Reads an ID such as P0001. Accepts lower case and surrounding spaces; returns upper case. */
    public static String readId(String prompt, String prefix) {
        while (true) {
            String line = readLine(prompt).toUpperCase();
            if (line.matches(prefix + "\\d{4}")) {
                return line;
            }
            ConsoleUI.printError("ID must look like " + prefix + "0001.");
        }
    }

    // ------------------------------------------------------------------ misc

    public static void pause() {
        readLine("Press Enter to continue...");
    }

    public static void close() {
        SCANNER.close();
    }

    /**
     * Prints the prompt and returns the trimmed line. If input has ended (for example, piped input ran
     * out), exits cleanly instead of crashing with NoSuchElementException.
     */
    private static String readLine(String prompt) {
        System.out.print(prompt);
        try {
            return SCANNER.nextLine().trim();
        } catch (NoSuchElementException e) {
            System.out.println();
            ConsoleUI.printInfo("Input ended. Exiting.");
            System.exit(0);
            return ""; // never reached; required by the compiler
        }
    }
}
