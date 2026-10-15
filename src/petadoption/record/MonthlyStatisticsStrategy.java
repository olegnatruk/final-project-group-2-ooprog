package petadoption.record;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import petadoption.application.AdoptionApplication;
import petadoption.application.ApplicationStatus;

/**
 * Strategy computing adoption statistics for a single month.
 */
public class MonthlyStatisticsStrategy implements StatisticsStrategy {

    private final int year;
    private final int month;

    public MonthlyStatisticsStrategy(int year, int month) {
        this.year = year;
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    @Override
    public String getPeriodLabel() {
        String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return monthName + " " + year;
    }

    @Override
    public StatisticsReport generate(List<AdoptionApplication> applications, List<AdoptionRecord> records) {
        int received = 0;
        int approved = 0;
        int rejected = 0;
        int cancelled = 0;
        int stillPending = 0;
        int completed = 0;

        if (applications != null) {
            for (AdoptionApplication app : applications) {
                LocalDate date = app.getApplicationDate();
                if (date != null && date.getYear() == year && date.getMonthValue() == month) {
                    received++;
                    if (app.getStatus() == ApplicationStatus.APPROVED) {
                        approved++;
                    } else if (app.getStatus() == ApplicationStatus.REJECTED) {
                        rejected++;
                    } else if (app.getStatus() == ApplicationStatus.CANCELLED) {
                        cancelled++;
                    } else if (app.getStatus() == ApplicationStatus.PENDING) {
                        stillPending++;
                    }
                }
            }
        }

        if (records != null) {
            for (AdoptionRecord rec : records) {
                LocalDate date = rec.getAdoptionDate();
                if (date != null && date.getYear() == year && date.getMonthValue() == month) {
                    completed++;
                }
            }
        }

        return new StatisticsReport(getPeriodLabel(), received, approved, rejected, cancelled, stillPending, completed, List.of());
    }
}
