package petadoption.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import petadoption.exception.DataFileException;
import petadoption.exception.DuplicateRecordException;
import petadoption.exception.PetAdoptionException;
import petadoption.exception.RecordNotFoundException;

/**
 * Holds every record of one type in memory and saves them to one data file.
 *
 * <p>Objects returned by the find methods are the live in-memory objects: change them through their own
 * methods, then call {@link #save()}.
 *
 * @param <T> the entity type stored
 */
public class Repository<T extends Storable> {

    private final String fileName;
    private final String idPrefix;
    private final String entityName;
    private final RecordParser<T> parser;
    private final List<T> items = new ArrayList<>();

    /**
     * @param fileName   file inside data/, e.g. "users.txt"
     * @param idPrefix   ID prefix, e.g. "U" for U0001
     * @param entityName readable name used in messages, e.g. "User" ("User U0007 not found.")
     * @param parser     turns one line's fields into an object, e.g. User::fromRecord
     */
    public Repository(String fileName, String idPrefix, String entityName, RecordParser<T> parser) {
        this.fileName = fileName;
        this.idPrefix = idPrefix;
        this.entityName = entityName;
        this.parser = parser;
    }

    /**
     * Loads the file into memory, replacing what was there.
     *
     * <p>Uses nested try-catch (ADR-031): the outer try handles the whole file (if it cannot be read,
     * loading stops and the error is re-thrown with the file name added); the inner try handles one line (a
     * bad line is skipped with a warning and loading continues).
     *
     * @return warnings for skipped lines (empty if every line loaded)
     */
    public List<String> load() throws DataFileException {
        items.clear();
        List<String> warnings = new ArrayList<>();
        try {
            List<String> lines = FileStorage.readLines(fileName);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isBlank()) {
                    continue;
                }
                try {
                    // -1 keeps trailing empty fields (see docs/GOTCHAS.md).
                    T item = parser.parse(line.split("\\|", -1));
                    add(item);
                } catch (PetAdoptionException e) {
                    warnings.add(fileName + " line " + (i + 1) + " skipped: " + e.getMessage());
                }
            }
        } catch (DataFileException e) {
            throw new DataFileException("Could not load " + entityName.toLowerCase() + " data. " + e.getMessage(), e);
        }
        return warnings;
    }

    /** Writes every record to the file, one line each. */
    public void save() throws DataFileException {
        List<String> lines = new ArrayList<>();
        for (T item : items) {
            lines.add(item.toRecord());
        }
        FileStorage.writeLines(fileName, lines);
    }

    public void add(T item) throws DuplicateRecordException {
        if (findById(item.getId()).isPresent()) {
            throw new DuplicateRecordException(entityName + " " + item.getId() + " already exists.");
        }
        items.add(item);
    }

    public void remove(String id) throws RecordNotFoundException {
        items.remove(getById(id));
    }

    /** Finds by ID, ignoring upper/lower case. Empty if there is no such record. */
    public Optional<T> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        for (T item : items) {
            if (item.getId().equalsIgnoreCase(id.trim())) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    /** Like {@link #findById(String)}, but throws when the record does not exist. */
    public T getById(String id) throws RecordNotFoundException {
        return findById(id).orElseThrow(
                () -> new RecordNotFoundException(entityName + " " + id + " not found."));
    }

    /** All records, in file order. The list itself cannot be modified. */
    public List<T> findAll() {
        return List.copyOf(items);
    }

    /** The next free ID: highest existing number + 1, zero-padded to 4 digits (e.g. U0007). */
    public String nextId() {
        int highest = 0;
        for (T item : items) {
            String digits = item.getId().substring(idPrefix.length());
            try {
                highest = Math.max(highest, Integer.parseInt(digits));
            } catch (NumberFormatException e) {
                // An ID with an unexpected format cannot be the highest number; ignore it.
            }
        }
        return String.format("%s%04d", idPrefix, highest + 1);
    }
}
