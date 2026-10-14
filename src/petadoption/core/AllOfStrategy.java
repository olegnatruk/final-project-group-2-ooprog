package petadoption.core;

import java.util.List;

/**
 * Strategy (concrete): combines several strategies and matches only when every one of them matches
 * (AND). Used when the user applies more than one filter at once, e.g. Type = Dog and Gender = Female.
 *
 * @param <T> the record type being searched
 */
public class AllOfStrategy<T> implements SearchStrategy<T> {

    private final List<SearchStrategy<T>> strategies;

    public AllOfStrategy(List<SearchStrategy<T>> strategies) {
        this.strategies = List.copyOf(strategies);
    }

    @Override
    public boolean matches(T item) {
        for (SearchStrategy<T> strategy : strategies) {
            if (!strategy.matches(item)) {
                return false;
            }
        }
        return true;
    }
}
