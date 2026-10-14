package petadoption.core;

/**
 * Strategy (interface): one way of deciding whether a record matches a search or filter.
 *
 * <p>Each concrete strategy is a small class, e.g. {@code PetTypeFilter} or {@code UserKeywordSearch}.
 * The screen picks which one to use at runtime and hands it to a {@link RecordFinder}.
 *
 * @param <T> the record type being searched
 */
@FunctionalInterface
public interface SearchStrategy<T> {

    boolean matches(T item);
}
