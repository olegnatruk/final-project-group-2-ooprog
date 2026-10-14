package petadoption.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import petadoption.exception.DataFileException;

/**
 * The only class that touches the data files. Files live in {@code data/}, relative to the folder the
 * program is started from (run it from the repository root).
 */
public final class FileStorage {

    public static final String DATA_DIR = "data";

    private FileStorage() {
        // static utility class
    }

    /** Reads every line of a data file. A file that does not exist yet counts as empty. */
    public static List<String> readLines(String fileName) throws DataFileException {
        Path file = Path.of(DATA_DIR, fileName);
        List<String> lines = new ArrayList<>();
        if (Files.notExists(file)) {
            return lines;
        }
        // try-with-resources: the reader is closed automatically, even if reading fails.
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            // Exception chaining: keep the original IOException as the cause.
            throw new DataFileException("Could not read " + file + " (" + e.getMessage() + ").", e);
        }
        return lines;
    }

    /** Replaces the whole file with the given lines, creating {@code data/} if needed. */
    public static void writeLines(String fileName, List<String> lines) throws DataFileException {
        Path file = Path.of(DATA_DIR, fileName);
        try {
            Files.createDirectories(file.getParent());
        } catch (IOException e) {
            throw new DataFileException("Could not create the " + DATA_DIR + " folder (" + e.getMessage() + ").", e);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new DataFileException("Could not save " + file + " (" + e.getMessage() + ").", e);
        }
    }
}
