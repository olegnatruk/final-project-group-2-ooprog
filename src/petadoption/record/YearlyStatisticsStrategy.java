package petadoption.record;

import java.util.ArrayList;
import java.util.List;

import petadoption.application.AdoptionApplication;

/**
 * Strategy computing adoption statistics for an entire year, including month-by-month breakdown and total.
 */
public class YearlyStatisticsStrategy implements StatisticsStrategy {

    private final int year;

    public YearlyStatisticsStrategy(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String getPeriodLabel() {
        return String.valueOf(year);
    }

    @Override
    public StatisticsReport generate(List<AdoptionApplication> applications, List<AdoptionRecord> records) {
        List<StatisticsReport> monthlyReports = new ArrayList<>();

        int totalReceived = 0;
        int totalApproved = 0;
        int totalRejected = 0;
        int totalCancelled = 0;
        int totalStillPending = 0;
        int totalCompleted = 0;

        for (int m = 1; m <= 12; m++) {
            MonthlyStatisticsStrategy monthlyStrategy = new MonthlyStatisticsStrategy(year, m);
            StatisticsReport monthReport = monthlyStrategy.generate(applications, records);
            monthlyReports.add(monthReport);

            totalReceived += monthReport.applicationsReceived();
            totalApproved += monthReport.approved();
            totalRejected += monthReport.rejected();
            totalCancelled += monthReport.cancelled();
            totalStillPending += monthReport.stillPending();
            totalCompleted += monthReport.adoptionsCompleted();
        }

        return new StatisticsReport(
                getPeriodLabel(),
                totalReceived,
                totalApproved,
                totalRejected,
                totalCancelled,
                totalStillPending,
                totalCompleted,
                monthlyReports
        );
    }
}
