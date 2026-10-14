package petadoption.core;

/**
 * Anything that can be saved as one line in a data file. Implemented by every entity
 * (User, Pet, AdoptionApplication, AdoptionRecord).
 */
public interface Storable {

    /** The entity's ID, e.g. U0001. */
    String getId();

    /** The entity as one line of fields joined with "|" (layouts in docs/DATA-MODEL.md). */
    String toRecord();
}
