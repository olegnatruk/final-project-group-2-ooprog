package petadoption.record;

import java.util.List;

/**
 * Statistics report for adoption activity in a period.
 * For monthly reports, {@code breakdown} is empty.
 * For yearly reports, {@code breakdown} contains 12 monthly reports.
 */
public record StatisticsReport(
        String periodLabel,
        int applicationsReceived,
        int approved,
        int rejected,
        int cancelled,
        int stillPending,
        int adoptionsCompleted,
        List<StatisticsReport> breakdown
) {
    public StatisticsReport {
        if (breakdown == null) {
            breakdown = List.of();
        } else {
            breakdown = List.copyOf(breakdown);
        }
    }
}
