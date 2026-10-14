package petadoption.core;

import petadoption.exception.PetAdoptionException;

/**
 * Turns the fields of one data-file line back into an object. Each domain supplies its own, usually as a
 * method reference such as {@code User::fromRecord}.
 *
 * @param <T> the entity type created
 */
@FunctionalInterface
public interface RecordParser<T> {

    T parse(String[] fields) throws PetAdoptionException;
}
