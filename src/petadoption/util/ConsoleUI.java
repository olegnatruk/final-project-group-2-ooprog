package petadoption.util;

import java.util.List;

/**
 * All console output formatting in one place, so every screen looks the same.
 * Menus and screens print through this class; services never print.
 */
public final class ConsoleUI {

    private static final int LINE_WIDTH = 60;

    private ConsoleUI() {
        // static utility class
    }

    public static void printHeader(String title) {
        System.out.println();
        System.out.println("=".repeat(LINE_WIDTH));
        System.out.println("  " + title);
        System.out.println("=".repeat(LINE_WIDTH));
    }

    public static void printError(String message) {
        System.out.println("[ERROR] " + message);
    }

    public static void printSuccess(String message) {
        System.out.println("[OK] " + message);
    }

    public static void printInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    public static void printDivider() {
        System.out.println("-".repeat(LINE_WIDTH));
    }

    /** Prints one labeled value, e.g. "Name:            Brownie". */
    public static void printField(String label, String value) {
        System.out.printf("%-20s %s%n", label + ":", value == null || value.isEmpty() ? "-" : value);
    }

    /**
     * Prints rows as an aligned table. Each column is as wide as its longest value.
     * Prints an info message instead when there are no rows.
     */
    public static void printTable(String[] headers, List<String[]> rows) {
        if (rows.isEmpty()) {
            printInfo("No records to show.");
            return;
        }

        int[] widths = new int[headers.length];
        for (int col = 0; col < headers.length; col++) {
            widths[col] = headers[col].length();
        }
        for (String[] row : rows) {
            for (int col = 0; col < headers.length && col < row.length; col++) {
                widths[col] = Math.max(widths[col], valueOf(row[col]).length());
            }
        }

        printRow(headers, widths);
        StringBuilder separator = new StringBuilder();
        for (int width : widths) {
            separator.append("-".repeat(width)).append("  ");
        }
        System.out.println(separator.toString().stripTrailing());
        for (String[] row : rows) {
            printRow(row, widths);
        }
    }

    private static void printRow(String[] values, int[] widths) {
        StringBuilder line = new StringBuilder();
        for (int col = 0; col < widths.length; col++) {
            String value = col < values.length ? valueOf(values[col]) : "";
            line.append(String.format("%-" + widths[col] + "s", value)).append("  ");
        }
        System.out.println(line.toString().stripTrailing());
    }

    private static String valueOf(String value) {
        return value == null ? "" : value;
    }
}
