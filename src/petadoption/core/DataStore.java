package petadoption.core;

import java.util.ArrayList;
import java.util.List;

import petadoption.exception.DataFileException;
import petadoption.pet.Pet;
import petadoption.record.AdoptionRecord;
import petadoption.user.User;

/**
 * Singleton: the one in-memory copy of all the system's data. Exactly one instance may exist, because two
 * copies would overwrite each other's saves. Reach it only through {@link #getInstance()}.
 *
 * <p>Each entity has its own {@link Repository}. Members register theirs here when their model class is
 * merged (see the commented lines below and docs/TEAM.md).
 */
public class DataStore {

    private static DataStore instance;

    private final Repository<User> users;
    private final Repository<Pet> pets;
    // Registered by each member when their model is merged:
    // private final Repository<AdoptionApplication> applications;  // Ace: "applications.txt", "APP", "Application", AdoptionApplication::fromRecord
    private final Repository<AdoptionRecord> records;

    private DataStore() {
        users = new Repository<>("users.txt", "U", "User", User::fromRecord);
        pets = new Repository<>("pets.txt", "P", "Pet", Pet::fromRecord);
        records = new Repository<>("records.txt", "REC", "Adoption record", AdoptionRecord::fromRecord);
    }

    /**
     * Returns the single instance, creating it on first use. {@code synchronized} so two threads can never
     * create two instances (ADR-034); this console app has one thread, so it is safe either way.
     */
    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    /**
     * Loads every data file into memory.
     *
     * @return warnings about lines that were skipped
     */
    public List<String> load() throws DataFileException {
        List<String> warnings = new ArrayList<>();
        warnings.addAll(users.load());
        warnings.addAll(pets.load());
        warnings.addAll(records.load());
        return warnings;
    }

    public Repository<User> users() {
        return users;
    }

    public Repository<Pet> pets() {
        return pets;
    }

    public Repository<AdoptionRecord> records() {
        return records;
    }
}
