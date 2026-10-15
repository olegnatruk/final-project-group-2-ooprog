package petadoption.record.search;

import java.time.LocalDate;

import petadoption.core.SearchStrategy;
import petadoption.record.AdoptionRecord;

/**
 * Filter adoption records where from <= adoptionDate <= to.
 */
public class RecordDateRangeFilter implements SearchStrategy<AdoptionRecord> {

    private final LocalDate from;
    private final LocalDate to;

    public RecordDateRangeFilter(LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean matches(AdoptionRecord item) {
        if (item == null || item.getAdoptionDate() == null) {
            return false;
        }
        LocalDate date = item.getAdoptionDate();
        if (from != null && date.isBefore(from)) {
            return false;
        }
        if (to != null && date.isAfter(to)) {
            return false;
        }
        return true;
    }
}
