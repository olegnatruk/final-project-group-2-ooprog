package petadoption.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy context: holds the current {@link SearchStrategy} and uses it to filter a list. The strategy
 * can be swapped at runtime with {@link #setStrategy(SearchStrategy)} without changing this class.
 *
 * <p>Every service that searches keeps its own {@code RecordFinder} (ADR-032).
 *
 * @param <T> the record type being searched
 */
public class RecordFinder<T> {

    private SearchStrategy<T> strategy;

    /** Creates a finder with no strategy yet; {@link #find(List)} then returns every item. */
    public RecordFinder() {
        this(null);
    }

    public RecordFinder(SearchStrategy<T> strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(SearchStrategy<T> strategy) {
        this.strategy = strategy;
    }

    /** Returns the items that match the current strategy, in their original order. */
    public List<T> find(List<T> items) {
        List<T> matches = new ArrayList<>();
        for (T item : items) {
            if (strategy == null || strategy.matches(item)) {
                matches.add(item);
            }
        }
        return matches;
    }
}
